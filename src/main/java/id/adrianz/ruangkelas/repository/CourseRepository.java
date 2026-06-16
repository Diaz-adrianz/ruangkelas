package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
