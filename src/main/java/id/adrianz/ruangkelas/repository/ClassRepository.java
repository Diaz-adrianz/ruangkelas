package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
}