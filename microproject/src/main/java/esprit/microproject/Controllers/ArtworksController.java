package esprit.microproject.Controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
@RequestMapping("/artworks")
public class ArtworksController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://openaccess-api.clevelandart.org/api/artworks/";
    @GetMapping
    public ResponseEntity<String> getArtworks(@RequestParam(value = "q", required = false) String query) {
        String requestUrl = apiUrl;
        if (query != null && !query.isEmpty()) {
            requestUrl += "?q=" + query;
        }
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}
