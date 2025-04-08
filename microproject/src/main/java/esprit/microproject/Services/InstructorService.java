package esprit.microproject.Services;

import esprit.microproject.Entities.Instructor;

import java.util.List;

public interface InstructorService {
    Instructor addInstructor(Instructor instructor);
    List<Instructor> getAllInstructors();
}
