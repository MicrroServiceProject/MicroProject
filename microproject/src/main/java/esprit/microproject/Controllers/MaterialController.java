package esprit.microproject.Controllers;

import esprit.microproject.Entities.Material;
import esprit.microproject.Services.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    // Endpoint to add a new material to a course using the course ID in the URL path
    @PostMapping("/course/{courseId}")
    public Material addMaterialToCourse(@PathVariable Long courseId, @RequestBody Material material) {
        return materialService.addMaterialToCourse(material, courseId);
    }

    // Endpoint to retrieve all materials
    @GetMapping
    public List<Material> getAllMaterials() {
        return materialService.getAllMaterials();
    }
}
