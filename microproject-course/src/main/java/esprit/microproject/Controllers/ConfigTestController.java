package esprit.microproject.Controllers; // Adaptez si votre package de controllers est différent

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config-test") // Endpoint de test
public class ConfigTestController {

    // Injecte la valeur depuis la clé 'welcome.message' trouvée dans
    // MicroProject.properties (servi par le Config Server)
    @Value("${welcome.message}")
    private String specificMessage;

    // Injecte la valeur depuis la clé 'welcome.messageGlobal' trouvée dans
    // config/application.properties (servi par le Config Server)
    @Value("${welcome.messageGlobal}")
    private String globalMessage;

    /**
     * Retourne les valeurs des propriétés injectées pour vérification.
     * @return ResponseEntity contenant une map des propriétés.
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> getConfigProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("specificMessage (from MicroProject.properties)", specificMessage);
        properties.put("globalMessage (from config/application.properties)", globalMessage);
        return ResponseEntity.ok(properties);
    }
}