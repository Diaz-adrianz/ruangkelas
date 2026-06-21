package id.adrianz.ruangkelas.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.model.Document;
import id.adrianz.ruangkelas.repository.ClassRepository;
import id.adrianz.ruangkelas.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ClassRepository classRepository;

    @Value("${app.upload.dir}") // ✅ di dalam class
    private String uploadDir;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Document getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Dokumen dengan ID " + id + " tidak ditemukan."));
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsByClass(Long classId) {
        return documentRepository.findByClazz_IdOrderByCreatedAtDesc(classId);
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Transactional
    public Document save(Long classId, String title, MultipartFile file) throws IOException {
        Class clazz = classRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Class dengan ID " + classId + " tidak ditemukan."));

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        List<String> allowedTypes = List.of(
                "pdf",
                "ppt",
                "pptx",
                "doc",
                "docx",
                "xls",
                "xlsx");

        if (!allowedTypes.contains(extension)) {
            throw new IllegalArgumentException(
                    "Format file tidak didukung.");
        }

        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document document = Document.builder()
                .clazz(clazz)
                .title(title)
                .fileType(getExtension(originalFilename))
                .fileName(uniqueFileName)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .build();

        return documentRepository.save(document);
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Transactional
    public void delete(Long id) throws IOException {
        Document document = getById(id);

        Path filePath = Paths.get(document.getFilePath());
        Files.deleteIfExists(filePath);

        documentRepository.delete(document);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(
                filename.lastIndexOf(".") + 1).toLowerCase();
    }
}