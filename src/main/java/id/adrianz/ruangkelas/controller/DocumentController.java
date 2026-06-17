package id.adrianz.ruangkelas.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.adrianz.ruangkelas.service.DocumentService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

private final DocumentService documentService;

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

    return "redirect:/class/" + classId;
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

    return "redirect:/class/" + classId;
}

}
