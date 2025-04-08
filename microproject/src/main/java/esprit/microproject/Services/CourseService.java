package esprit.microproject.Services;

import esprit.microproject.Entities.Course;

import java.util.List;

public interface CourseService {
    Course addCourse(Course course);
    Course updateCourse(Long id, Course updatedCourse);
    void deleteCourse(Long id);
    List<Course> getAllCourses();
    Course getCourseById(Long id);

    void assignInstructorToCourse(Long courseId, Long instructorId);

    void assignInstructorToMultipleCourses(Long instructorId, List<Long> courseIds);

    List<Course> searchCourses(String keyword);
}
