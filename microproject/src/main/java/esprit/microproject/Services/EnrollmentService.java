package esprit.microproject.Services;

import esprit.microproject.Entities.Enrollment;

import java.util.List;

public interface EnrollmentService {
    Enrollment addEnrollment(Long studentId, Long courseId);
    List<Enrollment> getAllEnrollments();
}
