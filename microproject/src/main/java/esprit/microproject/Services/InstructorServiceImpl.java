package esprit.microproject.Services;

import esprit.microproject.Entities.Instructor;
import esprit.microproject.Repositories.InstructorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class InstructorServiceImpl implements InstructorService {

    @Autowired
    private InstructorRepository instructorRepo;

    @Override
    public Instructor addInstructor(Instructor instructor) {
        return instructorRepo.save(instructor);
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepo.findAll();
    }

    @Override
    public Instructor updateInstructor(Long id, Instructor instructor) {
        Instructor existing = instructorRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Instructor with ID " + id + " not found"));

        existing.setName(instructor.getName());
        existing.setEmail(instructor.getEmail());
        existing.setUserId(instructor.getUserId());

        return instructorRepo.save(existing);
    }

    @Override
    public void deleteInstructor(Long id) {
        if (!instructorRepo.existsById(id)) {
            throw new EntityNotFoundException(
                "Instructor with ID " + id + " not found");
        }
        instructorRepo.deleteById(id);
    }
}
