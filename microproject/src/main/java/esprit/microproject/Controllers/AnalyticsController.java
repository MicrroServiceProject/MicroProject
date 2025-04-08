package esprit.microproject.Controllers;

import esprit.microproject.DTOs.CourseEnrollmentDTO;
import esprit.microproject.Services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/enrollments")
    public List<CourseEnrollmentDTO> getEnrollmentCounts() {
        return analyticsService.getEnrollmentCountByCourse();
    }
}