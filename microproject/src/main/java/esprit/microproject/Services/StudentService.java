package esprit.microproject.Services;

import esprit.microproject.Entities.Student;
import java.util.List;

public interface StudentService {
    Student addStudent(Student student);
    List<Student> getAllStudents();

    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
}
