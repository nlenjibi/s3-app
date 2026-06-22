package com.bem12.app.photo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/gallery";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("photos", photoService.listAll());
        return "gallery";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload-photo";
    }

    @PostMapping("/photos")
    public String upload(@RequestParam MultipartFile file,
                         @RequestParam(defaultValue = "") String description,
                         RedirectAttributes redirectAttributes) {
        try {
            photoService.upload(file, description);
            redirectAttributes.addFlashAttribute("success", "Photo uploaded successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/upload";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed. Please try again.");
            return "redirect:/upload";
        }
        return "redirect:/gallery";
    }
}
