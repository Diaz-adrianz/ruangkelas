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

    public List<Class> getAllForUser(Long userId) {
        return classRepository.findClassesByUserId(userId, UserClass.Status.ACCEPTED);
    }

    public Class getById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kelas tidak ditemukan"));
    }

    public void ensureAdmin(Long classId, Long userId) {
        UserClass userClass = userClassRepository.findByUserIdAndClasseId(userId, classId)
                .orElseThrow(() -> new RuntimeException("Kamu bukan anggota kelas ini"));

        if (userClass.getRole() != UserClass.Role.ADMIN) {
            throw new RuntimeException("Hanya admin yang bisa melakukan aksi ini");
        }
    }

    public Class create(String name, String year, Class.Semester semester, Long courseId, User creator) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Matkul tidak ditemukan"));

        if (classRepository.existsByCourseIdAndNameAndYearAndSemester(courseId, name, year, semester)) {
            throw new RuntimeException("Kelas sudah ada");
        }

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
                .status(UserClass.Status.ACCEPTED)
                .joinedAt(LocalDateTime.now())
                .build();

        userClassRepository.save(userClass);

        return saved;
    }

    public Class update(Long id, String name, String year, Class.Semester semester, Long courseId, Long userId) {
        ensureAdmin(id, userId);

        Class existing = getById(id);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Matkul tidak ditemukan"));

        boolean changed = !existing.getCourse().getId().equals(courseId)
                || !existing.getName().equals(name)
                || !existing.getYear().equals(year)
                || existing.getSemester() != semester;

        if (changed && classRepository.existsByCourseIdAndNameAndYearAndSemester(courseId, name, year, semester)) {
            throw new RuntimeException("Kelas sudah ada");
        }

        existing.setName(name);
        existing.setYear(year);
        existing.setSemester(semester);
        existing.setCourse(course);

        return classRepository.save(existing);
    }

    public void delete(Long id, Long userId) {
        ensureAdmin(id, userId);
        userClassRepository.deleteAll(userClassRepository.findByClasseId(id));
        classRepository.deleteById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void join(Long classId, User user) {
        Class kelas = getById(classId);

        if (userClassRepository.existsByUserIdAndClasseId(user.getId(), classId)) {
            throw new RuntimeException("User sudah bergabung di kelas " + kelas.getName());
        }

        UserClass userClass = UserClass.builder()
                .classe(kelas)
                .user(user)
                .role(UserClass.Role.MEMBER)
                .status(UserClass.Status.PENDING)
                .joinedAt(LocalDateTime.now())
                .build();
        userClassRepository.save(userClass);
    }

    public List<UserClass> getMembers(Long classId) {
        return userClassRepository.findByClasseIdAndStatus(classId, UserClass.Status.ACCEPTED);
    }

    public List<UserClass> getPendingRequests(Long classId) {
        return userClassRepository.findByClasseIdAndStatus(classId, UserClass.Status.PENDING);
    }

    public void approve(Long userClassId) {
        UserClass userClass = userClassRepository.findById(userClassId)
                .orElseThrow(() -> new RuntimeException("Permintaan tidak ditemukan"));
        userClass.setStatus(UserClass.Status.ACCEPTED);
        userClassRepository.save(userClass);
    }

    public void reject(Long userClassId) {
        userClassRepository.deleteById(userClassId);
    }

    public void kick(Long userClassId) {
        userClassRepository.deleteById(userClassId);
    }

    public void promote(Long userClassId) {
        UserClass userClass = userClassRepository.findById(userClassId)
                .orElseThrow(() -> new RuntimeException("Anggota tidak ditemukan"));
        userClass.setRole(UserClass.Role.ADMIN);
        userClassRepository.save(userClass);
    }

    public void demote(Long userClassId) {
        UserClass userClass = userClassRepository.findById(userClassId)
                .orElseThrow(() -> new RuntimeException("Anggota tidak ditemukan"));
        userClass.setRole(UserClass.Role.MEMBER);
        userClassRepository.save(userClass);
    }
}