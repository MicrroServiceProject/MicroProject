package esprit.microproject.Services;

import esprit.microproject.Entities.Instructor;

import java.util.List;

public interface InstructorService {
    Instructor addInstructor(Instructor instructor);
    List<Instructor> getAllInstructors();
        Instructor updateInstructor(Long id, Instructor instructor);
    
        void deleteInstructor(Long id);
}
