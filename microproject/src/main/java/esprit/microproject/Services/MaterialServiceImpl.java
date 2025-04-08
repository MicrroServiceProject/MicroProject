package esprit.microproject.Services;

import esprit.microproject.Entities.Course;
import esprit.microproject.Entities.Material;
import esprit.microproject.Repositories.CourseRepository;
import esprit.microproject.Repositories.MaterialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Override
    public Material addMaterialToCourse(Material material, Long courseId) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        material.setCourse(course);
        return materialRepo.save(material);
    }

    @Override
    public List<Material> getAllMaterials() {
        return materialRepo.findAll();
    }
}
