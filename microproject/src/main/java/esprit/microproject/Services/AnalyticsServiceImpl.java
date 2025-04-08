package esprit.microproject.Services;

import esprit.microproject.DTOs.CourseEnrollmentDTO;
import esprit.microproject.Entities.Course;
import esprit.microproject.Repositories.CourseRepository;
import esprit.microproject.Repositories.EnrollmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {
    @Autowired
    private EnrollmentRepository enrollmentRepo;
    @Autowired
    private CourseRepository courseRepo;

    @Override
    public List<CourseEnrollmentDTO> getEnrollmentCountByCourse() {
        List<Course> courses = courseRepo.findAll();
        return courses.stream()
                .map(course -> {
                    Long count = enrollmentRepo.countByCourse(course);
                    return new CourseEnrollmentDTO(course.getId(), course.getName(), count);
                })
                .collect(Collectors.toList());
    }
}