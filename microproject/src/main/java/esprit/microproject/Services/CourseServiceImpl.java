package esprit.microproject.Services;

import esprit.microproject.Entities.Course;
import esprit.microproject.Entities.Material;
import esprit.microproject.Repositories.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepo;

    @Override
    public Course addCourse(Course course) {
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepo.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course updatedCourse) {
        Course existing = courseRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        existing.setName(updatedCourse.getName());
        existing.setDescription(updatedCourse.getDescription());
        existing.setInstructorId(updatedCourse.getInstructorId());
        existing.setStudentIds(updatedCourse.getStudentIds());
        existing.setUpdatedAt(LocalDateTime.now());
        return courseRepo.save(existing);
    }

    @Override
    public void deleteCourse(Long id) {
        if (!courseRepo.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }
        courseRepo.deleteById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    @Override
    public void assignInstructorToCourse(Long courseId, Long instructorId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        course.setInstructorId(instructorId);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepo.save(course);
    }

    @Override
    public void assignInstructorToMultipleCourses(Long instructorId, List<Long> courseIds) {
        List<Course> courses = courseRepo.findAllById(courseIds);
        if (courses.size() != courseIds.size()) {
            throw new EntityNotFoundException("One or more courses not found in " + courseIds);
        }
        LocalDateTime now = LocalDateTime.now();
        for (Course c : courses) {
            c.setInstructorId(instructorId);
            c.setUpdatedAt(now);
        }
        courseRepo.saveAll(courses);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        return courseRepo.searchCourses(keyword);
    }

    @Override
    public Material addMaterialToCourse(Long courseId, Material material) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        material.setCourse(course);
        material.setUploadedAt(LocalDateTime.now());
        course.getMaterials().add(material);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepo.save(course);
        return material;
    }

    @Override
    public Material updateMaterial(Long courseId, Long materialId, Material updatedMaterial) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        for (Material m : course.getMaterials()) {
            if (m.getId().equals(materialId)) {
                m.setTitle(updatedMaterial.getTitle());
                m.setUrl(updatedMaterial.getUrl());
                m.setUploadedAt(LocalDateTime.now());
                course.setUpdatedAt(LocalDateTime.now());
                courseRepo.save(course);
                return m;
            }
        }
        throw new EntityNotFoundException("Material not found with id: " + materialId);
    }

    @Override
    public void removeMaterialFromCourse(Long courseId, Long materialId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        Iterator<Material> iterator = course.getMaterials().iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(materialId)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        if (!removed) {
            throw new EntityNotFoundException("Material not found with id: " + materialId);
        }
        course.setUpdatedAt(LocalDateTime.now());
        courseRepo.save(course);
    }
}
