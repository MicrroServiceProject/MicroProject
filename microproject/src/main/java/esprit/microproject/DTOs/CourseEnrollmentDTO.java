package esprit.microproject.DTOs;

public class CourseEnrollmentDTO {
    private Long courseId;
    private String courseName;
    private Long enrollmentCount;

    // Constructor
    public CourseEnrollmentDTO(Long courseId, String courseName, Long enrollmentCount) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.enrollmentCount = enrollmentCount;
    }

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(Long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }
}