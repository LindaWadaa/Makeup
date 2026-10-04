package com.makeup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import com.makeup.entities.Makeup;
@SpringBootApplication
public class MakeupApplication implements CommandLineRunner {

    @Autowired
    private RepositoryRestConfiguration repositoryRestConfiguration;


    public static void main(String[] args) {
        SpringApplication.run(MakeupApplication.class, args);
    }


    @Override
    public void run(String ... args) throws Exception {
    repositoryRestConfiguration.exposeIdsFor(Makeup.class); //pour voir id lors du test //pour voir id lors du test
}

}