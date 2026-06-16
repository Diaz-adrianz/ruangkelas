package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.UserClass;

@Repository
public interface UserClassRepository extends JpaRepository<UserClass, Long> {
}