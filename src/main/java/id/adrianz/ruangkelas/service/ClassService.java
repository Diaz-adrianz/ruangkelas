package id.adrianz.ruangkelas.service;
 
import java.security.SecureRandom;
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
 
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // tanpa karakter ambigu (O/0, I/1)
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
 
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
 
    public Class getByCode(String classCode) {
        return classRepository.findByClassCode(classCode)
                .orElseThrow(() -> new RuntimeException("Kode kelas tidak ditemukan"));
    }
 
    public void ensureAdmin(Long classId, Long userId) {
        UserClass userClass = userClassRepository.findByUserIdAndClasseId(userId, classId)
                .orElseThrow(() -> new RuntimeException("Kamu bukan anggota kelas ini"));
 
        if (userClass.getRole() != UserClass.Role.ADMIN) {
            throw new RuntimeException("Hanya admin yang bisa melakukan aksi ini");
        }
    }
 
    // #24: versi boolean dari ensureAdmin, dipakai untuk kontrol tampilan di view
    public boolean isAdmin(Long classId, Long userId) {
        return userClassRepository.findByUserIdAndClasseId(userId, classId)
                .map(uc -> uc.getRole() == UserClass.Role.ADMIN)
                .orElse(false);
    }
 
    private String generateUniqueClassCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (classRepository.existsByClassCode(code));
        return code;
    }
 
    public Class create(String name, String year, Class.Semester semester, Long courseId, String lecturerName, User creator) {
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
                .lecturerName(lecturerName)
                .classCode(generateUniqueClassCode())
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
 
    public Class update(String classCode, String name, String year, Class.Semester semester, Long courseId, String lecturerName, Long userId) {
        Class existing = getByCode(classCode);
        ensureAdmin(existing.getId(), userId);
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
        existing.setLecturerName(lecturerName);
 
        return classRepository.save(existing);
    }
 
    public void delete(String classCode, Long userId) {
        Class existing = getByCode(classCode);
        ensureAdmin(existing.getId(), userId);
        userClassRepository.deleteAll(userClassRepository.findByClasseId(existing.getId()));
        classRepository.deleteById(existing.getId());
    }
 
    // #26: cek apakah user boleh leave (untuk kontrol tampilan tombol di view)
    public boolean canLeave(Long classId, Long userId) {
        UserClass userClass = userClassRepository.findByUserIdAndClasseId(userId, classId)
                .orElse(null);

        if (userClass == null) {
            return false;
        }

        if (userClass.getRole() != UserClass.Role.ADMIN) {
            return true;
        }

        int totalMembers = userClassRepository.findByClasseIdAndStatus(classId, UserClass.Status.ACCEPTED).size();
        int adminCount = userClassRepository.countAdminByClasseId(classId);

        if (totalMembers <= 1) {
            return false;
        }

        return adminCount > 1;
    }

    public void leaveClass(Long classId, Long userId) {
        UserClass userClass = userClassRepository.findByUserIdAndClasseId(userId, classId)
                .orElseThrow(() -> new RuntimeException("Kamu bukan anggota kelas ini"));

        if (userClass.getRole() == UserClass.Role.ADMIN) {
            int totalMembers = userClassRepository.findByClasseIdAndStatus(classId, UserClass.Status.ACCEPTED).size();
            int adminCount = userClassRepository.countAdminByClasseId(classId);

            if (adminCount <= 1 && totalMembers > 1) {
                throw new RuntimeException("Kamu admin satu-satunya. Promote anggota lain jadi admin dulu sebelum keluar");
            }

            if (totalMembers <= 1) {
                throw new RuntimeException("Kamu satu-satunya anggota kelas ini. Hapus kelas jika ingin keluar");
            }
        }

        userClassRepository.delete(userClass);
    }
 
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
 
    // #25: gabung kelas pakai kode, bukan pilih dari daftar
    public void joinByCode(String classCode, User user) {
        Class kelas = getByCode(classCode);

        var existing = userClassRepository.findByUserIdAndClasseId(user.getId(), kelas.getId());

        if (existing.isPresent()) {
            UserClass.Status status = existing.get().getStatus();
            if (status == UserClass.Status.REJECTED) {
                // sudah pernah ditolak sebelumnya, hapus dulu lalu izinkan join ulang
                userClassRepository.delete(existing.get());
            } else {
                throw new RuntimeException("Kamu sudah bergabung di kelas " + kelas.getName());
            }
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
        UserClass userClass = userClassRepository.findById(userClassId)
                .orElseThrow(() -> new RuntimeException("Permintaan tidak ditemukan"));
        userClass.setStatus(UserClass.Status.REJECTED);
        userClassRepository.save(userClass);
    }

    // #27: cek apakah user punya notifikasi penolakan yang belum dilihat untuk kelas tertentu
    public List<UserClass> getRejectedForUser(Long userId) {
        return userClassRepository.findByUserId(userId).stream()
                .filter(uc -> uc.getStatus() == UserClass.Status.REJECTED)
                .toList();
    }

    // #27: tandai notifikasi penolakan sudah dilihat (hapus record-nya)
    public void dismissRejection(Long userClassId, Long userId) {
        UserClass userClass = userClassRepository.findById(userClassId)
                .orElseThrow(() -> new RuntimeException("Notifikasi tidak ditemukan"));

        if (!userClass.getUser().getId().equals(userId)) {
            throw new RuntimeException("Kamu tidak punya akses ke notifikasi ini");
        }

        userClassRepository.delete(userClass);
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