package id.adrianz.ruangkelas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Course;
import id.adrianz.ruangkelas.model.User;
import id.adrianz.ruangkelas.model.UserClass;
import id.adrianz.ruangkelas.repository.ClassRepository;
import id.adrianz.ruangkelas.repository.CourseRepository;
import id.adrianz.ruangkelas.repository.UserClassRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final CourseRepository courseRepository;
    private final UserClassRepository userClassRepository;

    public List<Class> getAll() {
        return classRepository.findAll();
    }

    public Class getById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kelas tidak ditemukan"));
    }

    public Class create(String name, String year, Class.Semester semester, Long courseId, User creator) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Matkul tidak ditemukan"));

        Class newClass = Class.builder()
                .name(name)
                .year(year)
                .semester(semester)
                .course(course)
                .build();

        Class saved = classRepository.save(newClass);

        UserClass userClass = UserClass.builder()
                .classe(saved)
                .user(creator)
                .role(UserClass.Role.ADMIN)
                .joinedAt(LocalDateTime.now())
                .build();

        userClassRepository.save(userClass);

        return saved;
    }

    public Class update(Long id, String name, String year, Class.Semester semester, Long courseId) {
        Class existing = getById(id);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Matkul tidak ditemukan"));

        existing.setName(name);
        existing.setYear(year);
        existing.setSemester(semester);
        existing.setCourse(course);

        return classRepository.save(existing);
    }

    public void delete(Long id) {
    userClassRepository.deleteAll(userClassRepository.findByClasseId(id));
    classRepository.deleteById(id);
}

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}