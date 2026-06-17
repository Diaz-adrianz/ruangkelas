package id.adrianz.ruangkelas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.UserClass;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    boolean existsByCourseIdAndNameAndYearAndSemester(Long courseId, String name, String year, Class.Semester semester);

    @Query("SELECT uc.classe FROM UserClass uc WHERE uc.user.id = :userId AND uc.status = :status")
    List<Class> findClassesByUserId(@Param("userId") Long userId, @Param("status") UserClass.Status status);
}