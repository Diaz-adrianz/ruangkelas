package id.adrianz.ruangkelas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate; // Import untuk tanggal
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class kelas;

    @Column(name = "nama_matkul", nullable = false)
    private String namaMatkul;

    // Field baru untuk menyimpan tanggal spesifik (Tanggal, Bulan, Tahun)
    @Column(name = "tanggal_kuliah", nullable = false)
    private LocalDate tanggalKuliah;

    @Column(name = "hari", nullable = false)
    private String hari; 

    @Column(name = "jam_mulai", nullable = false)
    private String jamMulai;

    @Column(name = "jam_selesai", nullable = false)
    private String jamSelesai;

    @Column(name = "ruangan", nullable = false)
    private String ruangan;

    @Column(name = "nama_dosen", nullable = false)
    private String namaDosen;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}