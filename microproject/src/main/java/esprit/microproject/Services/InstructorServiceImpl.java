package esprit.microproject.Services;

import esprit.microproject.Entities.Instructor;
import esprit.microproject.Repositories.InstructorRepository;
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
}
