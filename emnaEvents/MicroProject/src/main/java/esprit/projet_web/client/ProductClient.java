// Créez un package client : src/main/java/esprit/projet_web/client/
package esprit.projet_web.client;

import esprit.projet_web.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam; // Si vous implémentez la récupération par lots
import java.util.List;

// Le 'name' doit correspondre à spring.application.name de products-service ('MicroProject')
// Feign + Eureka trouveront l'URL. Sinon, utilisez l'attribut 'url'.
// Le préfixe /api est dans le @RequestMapping du ProductController, donc pas besoin ici.
@FeignClient(name = "MicroProject", path = "/api/products") // Spécifiez le path de base du contrôleur distant
public interface ProductClient {

    // Correspond à GET /api/products/{id} dans ProductController
    @GetMapping("/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);

    /*
     * OPTIONNEL mais recommandé pour Scénario 1 (meilleure perf) :
     * Ajouter un endpoint dans ProductController pour récupérer par liste d'IDs
     * Ex: GET /api/products?ids=1,5,10
     */
    // @GetMapping
    // List<ProductDTO> getProductsByIds(@RequestParam("ids") List<Long> ids);
    // Vous devrez ajouter la méthode correspondante dans ProductController/Service
}