package id.adrianz.ruangkelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.adrianz.ruangkelas.model.SubTask;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
    // Di sini kamu bisa menambah custom query Method jika nanti diperlukan
}