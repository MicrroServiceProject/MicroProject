package esprit.microproject;

import esprit.microproject.Entities.Product;

import esprit.microproject.Repositories.ProductRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient

public class MicroProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroProjectApplication.class, args);
    }



    }


