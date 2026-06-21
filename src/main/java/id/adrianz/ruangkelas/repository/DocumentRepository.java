package id.adrianz.ruangkelas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Ambil semua dokumen berdasarkan Class object
    List<Document> findByClazz(Class clazz);

    // Ambil semua dokumen berdasarkan class ID langsung (lebih efisien)
    List<Document> findByClazz_IdOrderByCreatedAtDesc(Long classId);

    // Cek apakah fileName sudah ada (untuk validasi duplikasi file)
    boolean existsByFileName(String fileName);

    // Hapus semua dokumen dalam satu class (berguna saat class dihapus)
    void deleteByClazz_Id(Long classId);
}