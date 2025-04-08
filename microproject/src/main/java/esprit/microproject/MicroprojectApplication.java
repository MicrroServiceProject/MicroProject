package esprit.microproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroprojectApplication.class, args);
    }

}
