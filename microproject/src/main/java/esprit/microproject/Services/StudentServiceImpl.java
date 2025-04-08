package esprit.microproject.Services;

import esprit.microproject.Entities.Student;
import esprit.microproject.Repositories.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepo;

    @Override
    public Student addStudent(Student student) {
        return studentRepo.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Student existing = studentRepo.findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException("Student with ID " + id + " not found"));

        // Copy over the fields you allow to be updated:
        existing.setUserId(student.getUserId());
        existing.setName(student.getName());
        // …any other updatable fields (but no email)…

        return studentRepo.save(existing);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepo.existsById(id)) {
            throw new EntityNotFoundException("Student with ID " + id + " not found");
        }
        studentRepo.deleteById(id);
    }
}
