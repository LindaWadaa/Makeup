package com.makeup.RestControllers;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.makeup.entities.User;
import com.makeup.repos.RoleRepository;
import com.makeup.repos.UserRepository;
import com.makeup.service.EmailService;
import com.makeup.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200",
        "http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, UserService userService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (isBlank(request.username()) || isBlank(request.password()) || isBlank(request.email())) {
            return ResponseEntity.badRequest().body("username, email et password sont obligatoires");
        }

        String email = request.email().trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.badRequest().body("Email invalide");
        }
        if (request.password().length() < 6) {
            return ResponseEntity.badRequest().body("Le mot de passe doit contenir au moins 6 caracteres");
        }
        if (userRepository.existsByUsername(request.username().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nom d'utilisateur deja utilise");
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email deja utilise");
        }

        var role = roleRepository.findByRole("USER")
                .orElseGet(() -> roleRepository.save(new com.makeup.entities.Role("USER")));
        User user = new User(request.username().trim(), request.password());
        user.setEmail(email);
        user.setEnabled(false);
        user.getRoles().add(role);

        String code = emailService.generateCode();
        user.setVerificationCode(code);
        userService.saveUser(user);

        emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Utilisateur cree. Un code de verification a ete envoye a " + email);
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyRequest request) {
        if (isBlank(request.email()) || isBlank(request.code())) {
            return ResponseEntity.badRequest().body("email et code sont obligatoires");
        }

        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Aucun utilisateur avec cet email");
        }
        if (user.isEnabled()) {
            return ResponseEntity.ok("Email deja verifie");
        }
        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.code().trim())) {
            return ResponseEntity.badRequest().body("Code de verification invalide");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.save(user);

        return ResponseEntity.ok("Email verifie. Vous pouvez maintenant vous connecter");
    }

    @PostMapping("/resendCode")
    public ResponseEntity<?> resendCode(@RequestBody ResendRequest request) {
        if (isBlank(request.email())) {
            return ResponseEntity.badRequest().body("email est obligatoire");
        }

        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Aucun utilisateur avec cet email");
        }
        if (user.isEnabled()) {
            return ResponseEntity.ok("Email deja verifie");
        }

        String code = emailService.generateCode();
        user.setVerificationCode(code);
        userRepository.save(user);
        emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code);

        return ResponseEntity.ok("Un nouveau code de verification a ete envoye");
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record RegisterRequest(String username, String email, String password) {
    }

    private record VerifyRequest(String email, String code) {
    }

    private record ResendRequest(String email) {
    }
}
