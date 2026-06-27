package id.adrianz.ruangkelas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.UserClass;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

    boolean existsByCourseIdAndNameAndYearAndSemester(
            Long courseId,
            String name,
            String year,
            Class.Semester semester);

    @Query("SELECT uc.classe FROM UserClass uc WHERE uc.user.id = :userId AND uc.status = :status")
    List<Class> findClassesByUserId(
            @Param("userId") Long userId,
            @Param("status") UserClass.Status status);

    @Query("""
            SELECT c
            FROM Class c
            LEFT JOIN FETCH c.course
            WHERE c.id = :id
            """)
    Optional<Class> findByIdWithCourse(@Param("id") Long id);

    // Tambahkan di sini
    @Query("""
            SELECT c
            FROM Class c
            LEFT JOIN FETCH c.course
            WHERE c.classCode = :classCode
            """)
    Optional<Class> findByClassCode(@Param("classCode") String classCode);

    boolean existsByClassCode(String classCode);
}
