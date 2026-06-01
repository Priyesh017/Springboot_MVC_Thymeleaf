package in.guvi.task.springbootmvc.controller;

import in.guvi.task.springbootmvc.dto.FeedbackRequestDto;
import in.guvi.task.springbootmvc.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public String formPage(Model model) {
        model.addAttribute("feedback", new FeedbackRequestDto());
        return "feedback/feedbackFormPage";
    }

    @PostMapping("/saveFeedback")
    public String saveFeedback(@Valid @ModelAttribute("feedback") FeedbackRequestDto requestDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "feedback/feedbackFormPage";
        }
        feedbackService.saveFeedback(requestDto);
        return "feedback/successPage";
    }

    @GetMapping("/display")
    public String displayFeedbacks(Model model) {
        model.addAttribute("feedbacks", feedbackService.displayAllFeedbacks());
        return "feedback/displayFeedbacksPage";
    }

    @GetMapping("/edit/{id}")
    public String editFeedbackPage(@PathVariable Long id, Model model) {
        model.addAttribute("feedback", feedbackService.getFeedbackByIdForUpdate(id));
        model.addAttribute("feedbackId", id);
        return "feedback/updateFeedbackPage";
    }

    @PostMapping("/edit/{id}")
    public String updateFeedback(@PathVariable Long id, @Valid @ModelAttribute("feedback") FeedbackRequestDto requestDto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("feedbackId", id);
            return "feedback/updateFeedbackPage";
        }
        feedbackService.updateFeedback(id, requestDto);
        return "redirect:/feedback/display";
    }

    @GetMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return "redirect:/feedback/display";
    }
}
