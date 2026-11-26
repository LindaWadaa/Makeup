package com.makeup.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makeup.entities.Role;
import com.makeup.entities.User;
import com.makeup.entities.VerificationToken;
import com.makeup.exception.EmailAlreadyExistsException;
import com.makeup.exception.ExpiredTokenException;
import com.makeup.exception.InvalidTokenException;
import com.makeup.exception.UsernameAlreadyExistsException;
import com.makeup.register.RegistationRequest;
import com.makeup.repos.RoleRepository;
import com.makeup.repos.UserRepository;
import com.makeup.repos.VerificationTokenRepository;
import com.makeup.util.EmailSender;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailSender emailSender;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository,
            EmailSender emailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailSender = emailSender;
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerUser(RegistationRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            throw new EmailAlreadyExistsException("email déjà existant!");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("nom utilisateur déjà existant!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEnabled(false);

        Role role = roleRepository.findByRole("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        newUser.setRoles(roles);

        try {
            newUser = userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("email déjà existant!");
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("nom utilisateur déjà existant!");
            }
            throw e;
        }

        String code = generateCode();
        VerificationToken token = new VerificationToken(code, newUser);
        verificationTokenRepository.save(token);
        sendEmailUser(newUser, token.getToken());

        return newUser;
    }

    @Override
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return Integer.toString(code);
    }

    @Override
    public void sendEmailUser(User user, String code) {
        String emailBody = "Bonjour " + "<h1>" + user.getUsername() + "</h1>"
                + " Votre code de validation est " + "<h1>" + code + "</h1>";
        emailSender.sendEmail(user.getEmail(), emailBody);
    }

    @Override
    @Transactional
    public User validateToken(String code) {
        VerificationToken token = verificationTokenRepository.findByToken(code);
        if (token == null) {
            throw new InvalidTokenException("Invalid Token");
        }

        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(token);
            throw new ExpiredTokenException("expired Token");
        }

        user.setEnabled(true);
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User addRoleToUser(String username, String roleName) {
        User user = findUserByUsername(username);
        Role role = roleRepository.findByRole(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable : " + roleName));
        if (user.getRoles().stream().noneMatch(existing -> existing.getRole().equals(roleName))) {
            user.getRoles().add(role);
        }
        return user;
    }

    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
