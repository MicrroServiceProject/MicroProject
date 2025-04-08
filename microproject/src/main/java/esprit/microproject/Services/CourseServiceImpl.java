package esprit.microproject.Services;

import esprit.microproject.Entities.Course;
import esprit.microproject.Entities.Instructor;
import esprit.microproject.Repositories.CourseRepository;
import esprit.microproject.Repositories.InstructorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private InstructorRepository instructorRepo;

    @Override
    public Course addCourse(Course course) {
        return courseRepo.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course updatedCourse) {
        Course existing = courseRepo.findById(id).orElseThrow();
        existing.setName(updatedCourse.getName());
        existing.setDescription(updatedCourse.getDescription());
        return courseRepo.save(existing);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepo.deleteById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepo.findById(id).orElse(null);
    }

    @Override
    public void assignInstructorToCourse(Long courseId, Long instructorId) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        Instructor instructor = instructorRepo.findById(instructorId).orElseThrow();
        course.setInstructor(instructor);
        courseRepo.save(course);
    }

    @Override
    public void assignInstructorToMultipleCourses(Long instructorId, List<Long> courseIds) {
        // Fetch the instructor by ID
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found with id: " + instructorId));

        // Fetch all courses by their IDs
        List<Course> courses = courseRepo.findAllById(courseIds);
        if (courses.size() != courseIds.size()) {
            throw new EntityNotFoundException("One or more courses not found");
        }

        // Assign the instructor to each course
        for (Course course : courses) {
            course.setInstructor(instructor);
        }

        // Save all updated courses
        courseRepo.saveAll(courses);
    }
    @Override
    public List<Course> searchCourses(String keyword) {
        return courseRepo.searchCourses(keyword);
    }
}
