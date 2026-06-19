package id.adrianz.ruangkelas.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.DocumentService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

private final DocumentService documentService;
private final ClassService classService;

// =====================
// LIST DOCUMENT
// =====================

@GetMapping("/class/{classId}")
public String listDocuments(
        @PathVariable Long classId,
        Model model) {

    model.addAttribute(
            "documents",
            documentService.getDocumentsByClass(classId));

    model.addAttribute("classId", classId);

    return "pages/Class/Detail";
}

// =====================
// UPLOAD DOCUMENT
// =====================

@PostMapping("/upload")
public String upload(
        @RequestParam Long classId,
        @RequestParam String title,
        @RequestParam MultipartFile file,
        RedirectAttributes redirectAttributes) {

    try {
        documentService.save(classId, title, file);
        redirectAttributes.addFlashAttribute(
                "success",
                "Dokumen berhasil diupload");
    } catch (IOException e) {
        redirectAttributes.addFlashAttribute(
                "error",
                e.getMessage());
    }

    String classCode =
        classService.getById(classId).getClassCode();

    return "redirect:/class/" + classCode;
}

// =====================
// DELETE DOCUMENT
// =====================

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            @RequestParam Long classId,
            RedirectAttributes redirectAttributes) {

        try {
            documentService.delete(id);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Dokumen berhasil dihapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        String classCode =
            classService.getById(classId).getClassCode();

        return "redirect:/class/" + classCode;
    }

    @GetMapping("/download/{id}")
        public ResponseEntity<Resource> download(
        @PathVariable Long id) throws Exception {

        var document = documentService.getById(id);

        System.out.println("FILE PATH = " + document.getFilePath());

        Path path = Paths.get(document.getFilePath());

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                document.getFileName() +
                                "\""
                )
                .body(resource);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(
        @PathVariable Long id) throws Exception {

            var document = documentService.getById(id);

            Path path = Paths.get(document.getFilePath());

            Resource resource = new UrlResource(path.toUri());

            String contentType = Files.probeContentType(path);

            return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" +
                            document.getFileName() +
                            "\""
            )
            .header(
                    HttpHeaders.CONTENT_TYPE,
                    "application/pdf"
            )
            .body(resource);
        }
}
