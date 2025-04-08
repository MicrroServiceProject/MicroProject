package esprit.microproject.Services;

import esprit.microproject.Entities.Material;

import java.util.List;

public interface MaterialService {
    Material addMaterialToCourse(Material material, Long courseId);
    List<Material> getAllMaterials();


}
