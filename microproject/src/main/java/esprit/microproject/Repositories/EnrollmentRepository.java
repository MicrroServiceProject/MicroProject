package esprit.microproject.Repositories;

import esprit.microproject.Entities.Course;
import esprit.microproject.Entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Long countByCourse(Course course);


}
