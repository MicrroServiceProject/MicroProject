package esprit.microproject.Controllers;

import esprit.microproject.Entities.Course;
import esprit.microproject.Entities.Material;
import esprit.microproject.Services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public Course addCourse(@RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    @PutMapping("/{courseId}/instructor/{instructorId}")
    public void assignInstructorToCourse(@PathVariable Long courseId,
                                         @PathVariable Long instructorId) {
        courseService.assignInstructorToCourse(courseId, instructorId);
    }

    @PutMapping("/instructor/{instructorId}/assign")
    public void assignInstructorToMultipleCourses(@PathVariable Long instructorId,
                                                  @RequestBody List<Long> courseIds) {
        courseService.assignInstructorToMultipleCourses(instructorId, courseIds);
    }

    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }

    @PostMapping("/{courseId}/materials")
    public ResponseEntity<Material> addMaterial(@PathVariable Long courseId,
                                                @RequestBody Material material) {
        Material created = courseService.addMaterialToCourse(courseId, material);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{courseId}/materials/{materialId}")
    public ResponseEntity<Material> updateMaterial(@PathVariable Long courseId,
                                                   @PathVariable Long materialId,
                                                   @RequestBody Material material) {
        Material updated = courseService.updateMaterial(courseId, materialId, material);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{courseId}/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long courseId,
                                               @PathVariable Long materialId) {
        courseService.removeMaterialFromCourse(courseId, materialId);
        return ResponseEntity.noContent().build();
    }
}
