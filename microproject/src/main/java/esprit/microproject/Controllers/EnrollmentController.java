package esprit.microproject.Controllers;

import esprit.microproject.Entities.Enrollment;
import esprit.microproject.Services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // Endpoint to create a new enrollment using studentId and courseId as request parameters
    @PostMapping
    public Enrollment addEnrollment(@RequestParam Long studentId, @RequestParam Long courseId) {
        return enrollmentService.addEnrollment(studentId, courseId);
    }

    // Endpoint to retrieve all enrollments
    @GetMapping
    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }
}
