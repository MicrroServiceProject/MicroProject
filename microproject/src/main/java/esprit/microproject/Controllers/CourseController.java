package esprit.microproject.Controllers;

import esprit.microproject.Entities.Course;
import esprit.microproject.Services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // Retrieve all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // Retrieve a single course by id
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    // Create a new course
    @PostMapping
    public Course addCourse(@RequestBody Course course) {
        return courseService.addCourse(course);
    }

    // Update an existing course
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

    // Delete a course by id
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    // Assign an instructor to a course
    @PutMapping("/{courseId}/instructor/{instructorId}")
    public void assignInstructorToCourse(@PathVariable Long courseId, @PathVariable Long instructorId) {
        courseService.assignInstructorToCourse(courseId, instructorId);
    }

    // Assign an instructor to multiple courses
    @PutMapping("/instructor/{instructorId}/assign")
    public void assignInstructorToMultipleCourses(@PathVariable Long instructorId, @RequestBody List<Long> courseIds) {
        courseService.assignInstructorToMultipleCourses(instructorId, courseIds);
    }

    // Search courses by keyword
    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }
}