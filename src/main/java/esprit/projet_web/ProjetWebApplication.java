package esprit.projet_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // Ou @EnableEurekaClient
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // Active la configuration et l'injection des clients Feign
@EnableDiscoveryClient // Permet à ce service de s'enregistrer sur Eureka et de découvrir d'autres services
public class ProjetWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetWebApplication.class, args);
	}

}