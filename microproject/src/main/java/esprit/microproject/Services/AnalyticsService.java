package esprit.microproject.Services;

import esprit.microproject.DTOs.CourseEnrollmentDTO;
import java.util.List;

public interface AnalyticsService {
    List<CourseEnrollmentDTO> getEnrollmentCountByCourse();
}