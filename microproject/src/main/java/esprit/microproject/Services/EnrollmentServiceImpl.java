package esprit.microproject.Services;

import esprit.microproject.Entities.Course;
import esprit.microproject.Entities.Enrollment;
import esprit.microproject.Entities.Student;
import esprit.microproject.Repositories.CourseRepository;
import esprit.microproject.Repositories.EnrollmentRepository;
import esprit.microproject.Repositories.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Override
    public Enrollment addEnrollment(Long studentId, Long courseId) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        Student student = studentRepo.findById(studentId).orElseThrow();

        Enrollment e = new Enrollment();
        e.setCourse(course);
        e.setStudent(student);
        e.setEnrollmentDate(LocalDateTime.now());

        return enrollmentRepo.save(e);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepo.findAll();
    }
}
