package id.adrianz.ruangkelas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.adrianz.ruangkelas.model.UserClass;

public interface UserClassRepository extends JpaRepository<UserClass, Long> {

    List<UserClass> findByClasseId(Long classeId);

    List<UserClass> findByClasseIdAndStatus(Long classeId, UserClass.Status status);

    boolean existsByUserIdAndClasseId(Long userId, Long classeId);

    List<UserClass> findByUserId(Long userId);

    Optional<UserClass> findByUserIdAndClasseId(Long userId, Long classeId);

    // 🔥 NEW: hitung admin di kelas
    @Query("""
        SELECT COUNT(uc)
        FROM UserClass uc
        WHERE uc.classe.id = :classeId
        AND uc.role = id.adrianz.ruangkelas.model.UserClass.Role.ADMIN
    """)
    int countAdminByClasseId(@Param("classeId") Long classeId);


}