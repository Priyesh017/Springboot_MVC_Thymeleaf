package in.guvi.task.springbootmvc.controller;

import in.guvi.task.springbootmvc.dto.FeedbackRequestDto;
import in.guvi.task.springbootmvc.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * MVC Controller handling all web requests related to the Feedback feature.
 *
 * <p>Follows the MVC pattern: this controller acts as the "C" (Controller), delegating
 * all business logic to {@link FeedbackService} and returning Thymeleaf view names
 * for the "V" (View) layer to render.
 *
 * <p>All endpoints are prefixed with {@code /feedback} via {@code @RequestMapping}.
 *
 * <p>Dependency injection is achieved via {@code @RequiredArgsConstructor} (Lombok),
 * which generates a constructor for the {@code final} {@link FeedbackService} field.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    /** Service layer handling all feedback-related business logic and database operations. */
    private final FeedbackService feedbackService;

    /**
     * GET /feedback
     *
     * <p>Displays the blank feedback submission form.
     * An empty {@link FeedbackRequestDto} is added to the model so Thymeleaf can bind
     * form fields to the DTO using {@code th:object} and {@code th:field} attributes.
     *
     * @param model Spring's {@link Model} used to pass data to the view
     * @return the Thymeleaf view name for the feedback submission form
     */
    @GetMapping
    public String formPage(Model model) {
        // Bind an empty DTO to the form so Thymeleaf can render input fields
        model.addAttribute("feedback", new FeedbackRequestDto());
        return "feedback/feedbackFormPage";
    }

    /**
     * POST /feedback/saveFeedback
     *
     * <p>Processes a submitted feedback form.
     * {@code @Valid} triggers Bean Validation on the incoming {@link FeedbackRequestDto}.
     * If any constraint (@NotNull, @NotBlank, etc.) fails, {@link BindingResult} captures
     * the errors and the user is shown the form again with error messages — no redirect occurs.
     * On success, the feedback is persisted and the user is forwarded to a success confirmation page.
     *
     * @param requestDto    the submitted form data bound to a {@link FeedbackRequestDto}
     * @param bindingResult contains validation errors (if any) from the {@code @Valid} check
     * @return the form view (on validation failure) or the success view (on success)
     */
    @PostMapping("/saveFeedback")
    public String saveFeedback(@Valid @ModelAttribute("feedback") FeedbackRequestDto requestDto,
                               BindingResult bindingResult) {
        // If validation failed, re-render the form with error messages displayed
        if (bindingResult.hasErrors()) {
            return "feedback/feedbackFormPage";
        }
        // Delegate to service to map DTO → entity and persist to the database
        feedbackService.saveFeedback(requestDto);
        return "feedback/successPage";
    }

    /**
     * GET /feedback/display
     *
     * <p>Fetches all stored feedback records from the database and passes them
     * to the view as a list of {@link in.guvi.task.springbootmvc.dto.FeedbackResponseDto} objects.
     *
     * @param model Spring's {@link Model} used to pass data to the view
     * @return the Thymeleaf view name for the feedback list/display page
     */
    @GetMapping("/display")
    public String displayFeedbacks(Model model) {
        // Fetch all feedbacks via service and expose the list to the template
        model.addAttribute("feedbacks", feedbackService.displayAllFeedbacks());
        return "feedback/displayFeedbacksPage";
    }

    /**
     * GET /feedback/edit/{id}
     *
     * <p>Displays the pre-populated update form for an existing feedback entry.
     * The feedback ID is extracted from the URL path and used to fetch the current
     * field values, which are bound to the form for the user to edit.
     *
     * @param id    the unique database ID of the feedback to edit (from URL path variable)
     * @param model Spring's {@link Model} used to pass data to the view
     * @return the Thymeleaf view name for the feedback edit/update form
     */
    @GetMapping("/edit/{id}")
    public String editFeedbackPage(@PathVariable Long id, Model model) {
        // Pre-populate the form with the current values fetched from the database
        model.addAttribute("feedback", feedbackService.getFeedbackByIdForUpdate(id));
        // Pass the ID separately so Thymeleaf can build the form's POST action URL
        model.addAttribute("feedbackId", id);
        return "feedback/updateFeedbackPage";
    }

    /**
     * POST /feedback/edit/{id}
     *
     * <p>Processes the submitted update form for an existing feedback entry.
     * Validates the incoming data; on failure, re-renders the update form with
     * the ID still in context so the form action URL remains correct.
     * On success, the existing record is updated in the database and the user
     * is redirected to the feedback list page (PRG – Post/Redirect/Get pattern).
     *
     * @param id            the unique database ID of the feedback to update
     * @param requestDto    the updated form data bound to a {@link FeedbackRequestDto}
     * @param bindingResult contains validation errors (if any)
     * @param model         Spring's {@link Model} used to pass data back on validation failure
     * @return redirect to display page (on success) or the update form (on failure)
     */
    @PostMapping("/edit/{id}")
    public String updateFeedback(@PathVariable Long id,
                                 @Valid @ModelAttribute("feedback") FeedbackRequestDto requestDto,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            // Keep the ID in model so the form's POST URL (th:action) can still be built correctly
            model.addAttribute("feedbackId", id);
            return "feedback/updateFeedbackPage";
        }
        // Delegate update logic to service: fetch existing record, apply changes, save
        feedbackService.updateFeedback(id, requestDto);
        // PRG pattern: redirect prevents form re-submission on browser refresh
        return "redirect:/feedback/display";
    }

    /**
     * GET /feedback/delete/{id}
     *
     * <p>Deletes a feedback record by its ID and redirects the user back to the list page.
     * A GET-based delete is used here for simplicity (HTML forms don't natively support DELETE).
     * After deletion, the PRG pattern is applied via a redirect to avoid duplicate delete on refresh.
     *
     * @param id the unique database ID of the feedback record to delete
     * @return redirect to the feedback display page
     */
    @GetMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return "redirect:/feedback/display";
    }
}
