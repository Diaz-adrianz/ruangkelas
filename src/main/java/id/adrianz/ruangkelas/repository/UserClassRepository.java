package id.adrianz.ruangkelas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.UserClass;

@Repository
public interface UserClassRepository extends JpaRepository<UserClass, Long> {
    List<UserClass> findByClasseId(Long classeId);
}