package com.makeup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.makeup.entities.Role;
import com.makeup.entities.User;
import com.makeup.entities.VerificationToken;
import com.makeup.exception.EmailAlreadyExistsException;
import com.makeup.exception.ExpiredTokenException;
import com.makeup.exception.InvalidTokenException;
import com.makeup.register.RegistationRequest;
import com.makeup.repos.RoleRepository;
import com.makeup.repos.UserRepository;
import com.makeup.repos.VerificationTokenRepository;
import com.makeup.util.EmailSender;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUserSavesDisabledUserWithRoleTokenAndEmail() {
        RegistationRequest request = new RegistationRequest("nadh", "pass123", "nadh@example.com");
        when(userRepository.findByEmail("nadh@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(roleRepository.findByRole("USER")).thenReturn(Optional.of(new Role("USER")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(request);

        assertFalse(result.getEnabled());
        assertEquals("encoded", result.getPassword());
        assertEquals("nadh@example.com", result.getEmail());
        assertEquals("USER", result.getRoles().get(0).getRole());

        ArgumentCaptor<VerificationToken> tokenCaptor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(tokenCaptor.capture());
        assertTrue(tokenCaptor.getValue().getToken().matches("\\d{6}"));
        verify(emailSender).sendEmail(eq("nadh@example.com"), contains(tokenCaptor.getValue().getToken()));
    }

    @Test
    void registerUserRejectsDuplicateEmail() {
        when(userRepository.findByEmail("deja@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.registerUser(new RegistationRequest("nadh", "pass123", "deja@example.com")));
        verifyNoInteractions(emailSender, verificationTokenRepository);
    }

    @Test
    void validateTokenEnablesUser() {
        User user = new User("nadh", "encoded");
        user.setEnabled(false);
        VerificationToken token = new VerificationToken("123456", user);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        token.setExpirationTime(calendar.getTime());
        when(verificationTokenRepository.findByToken("123456")).thenReturn(token);

        User result = userService.validateToken("123456");

        assertTrue(result.getEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void validateTokenRejectsUnknownCode() {
        when(verificationTokenRepository.findByToken("000000")).thenReturn(null);

        assertThrows(InvalidTokenException.class, () -> userService.validateToken("000000"));
    }

    @Test
    void validateTokenRejectsExpiredCodeAndDeletesIt() {
        User user = new User("nadh", "encoded");
        VerificationToken token = new VerificationToken("123456", user);
        token.setExpirationTime(new Date(0));
        when(verificationTokenRepository.findByToken("123456")).thenReturn(token);

        assertThrows(ExpiredTokenException.class, () -> userService.validateToken("123456"));
        verify(verificationTokenRepository).delete(token);
    }

    @Test
    void generateCodeReturnsSixDigits() {
        assertTrue(userService.generateCode().matches("\\d{6}"));
    }

    @Test
    void sendEmailUserUsesHtmlBody() {
        User user = new User("nadh", "encoded");
        user.setEmail("nadh@example.com");

        userService.sendEmailUser(user, "123456");

        verify(emailSender).sendEmail(eq("nadh@example.com"), contains("<h1>123456</h1>"));
    }
}
