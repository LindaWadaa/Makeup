package com.makeup.RestControllers;

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
import com.makeup.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200",
        "http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.username() == null || request.password() == null
                || request.username().isBlank() || request.password().isBlank()) {
            return ResponseEntity.badRequest().body("username et password sont obligatoires");
        }
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nom d'utilisateur deja utilise");
        }
        var role = roleRepository.findByRole("USER").orElseGet(() -> roleRepository.save(new com.makeup.entities.Role("USER")));
        User user = new User(request.username(), request.password());
        user.getRoles().add(role);
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur cree");
    }

    private record RegisterRequest(String username, String password) {
    }
}
