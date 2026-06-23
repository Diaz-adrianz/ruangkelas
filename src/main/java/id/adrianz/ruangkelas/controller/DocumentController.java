package id.adrianz.ruangkelas.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.dto.CreateDocumentDto;
import id.adrianz.ruangkelas.model.Class;
import id.adrianz.ruangkelas.service.ClassService;
import id.adrianz.ruangkelas.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final ClassService classService;

    // =====================
    // UPLOAD DOCUMENT
    // =====================

    @PostMapping("/upload")
    public String upload(
                @Valid @ModelAttribute("createDocumentDto") CreateDocumentDto request,
                BindingResult result,
                Model model,
                RedirectAttributes redirectAttributes) {

        Class classs = classService.getById(request.getClassId());

        if (result.hasErrors()) {
                model.addAttribute("classs", classs);
                model.addAttribute("errors", result.getFieldErrors());
                return "pages/Document/Create";
        }

        try {
            documentService.save(request.getClassId(), request.getTitle(), request.getFile());
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Dokumen berhasil diupload");
        } catch (IOException | IllegalArgumentException e) {
            model.addAttribute("classs", classs);
            model.addAttribute("error", e.getMessage());
            return "pages/Document/Create";
        } 

        return "redirect:/class/" + classs.getClassCode() + "#documents";
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

        String classCode = classService.getById(classId).getClassCode();

        return "redirect:/class/" + classCode + "#documents"; 
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Long id) throws Exception {

        var document = documentService.getById(id);

        Path path = Paths.get(document.getFilePath());

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                document.getFileName() +
                                "\"")
                .body(resource);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(
            @PathVariable Long id) throws Exception {

        var document = documentService.getById(id);

        Path path = Paths.get(document.getFilePath());

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                document.getFileName() +
                                "\"")
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "application/pdf")
                .body(resource);
    }
}
