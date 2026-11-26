package com.makeup;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import com.makeup.entities.Makeup;
import com.makeup.entities.Marque;
import com.makeup.entities.Role;
import com.makeup.entities.User;
import com.makeup.repos.RoleRepository;
import com.makeup.repos.UserRepository;
import com.makeup.service.UserService;
@SpringBootApplication
public class MakeupApplication implements CommandLineRunner {

    private final RepositoryRestConfiguration repositoryRestConfiguration;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public MakeupApplication(RepositoryRestConfiguration repositoryRestConfiguration,
            UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.repositoryRestConfiguration = repositoryRestConfiguration;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MakeupApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    @Override
    public void run(String ... args) throws Exception {
        repositoryRestConfiguration.exposeIdsFor(Makeup.class, Marque.class);
        createDefaultUsers();
    }

    private void createDefaultUsers() {
        Role userRole = roleRepository.findByRole("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));
        Role adminRole = roleRepository.findByRole("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        createUserIfMissing("admin", "123", userRole, adminRole);
        createUserIfMissing("linda", "123", userRole);
    }

    private void createUserIfMissing(String username, String password, Role... roles) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = new User(username, password);
        for (Role role : roles) {
            user.getRoles().add(role);
        }
        userService.saveUser(user);
    }

}