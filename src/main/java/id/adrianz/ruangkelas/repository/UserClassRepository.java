package id.adrianz.ruangkelas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.UserClass;

@Repository
public interface UserClassRepository extends JpaRepository<UserClass, Long> {
    List<UserClass> findByClasseId(Long classeId);
    List<UserClass> findByClasseIdAndStatus(Long classeId, UserClass.Status status);
    boolean existsByUserIdAndClasseId(Long userId, Long classeId);
    List<UserClass> findByUserId(Long userId);
    Optional<UserClass> findByUserIdAndClasseId(Long userId, Long classeId);
}