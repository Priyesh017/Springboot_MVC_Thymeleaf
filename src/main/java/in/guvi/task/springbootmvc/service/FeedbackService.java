package in.guvi.task.springbootmvc.service;

import in.guvi.task.springbootmvc.dto.FeedbackRequestDto;
import in.guvi.task.springbootmvc.dto.FeedbackResponseDto;
import in.guvi.task.springbootmvc.model.Feedback;
import in.guvi.task.springbootmvc.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(FeedbackRequestDto dto) {
        Feedback feedback = Feedback.builder()
                .name(dto.getName())
                .bookName(dto.getBookName())
                .feedback(dto.getFeedback())
                .build();

        return feedbackRepository.save(feedback);
    }

    public List<FeedbackResponseDto> displayAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(f -> FeedbackResponseDto.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .bookName(f.getBookName())
                        .feedback(f.getFeedback())
                        .build())
                .collect(Collectors.toList());
    }

    public FeedbackRequestDto getFeedbackByIdForUpdate(Long id) {
        Feedback f = feedbackRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        return FeedbackRequestDto.builder().name(f.getName()).bookName(f.getBookName()).feedback(f.getFeedback()).build();
    }

    public void updateFeedback(Long id, FeedbackRequestDto dto) {
        Feedback existing = feedbackRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        existing.setName(dto.getName());
        existing.setBookName(dto.getBookName());
        existing.setFeedback(dto.getFeedback());
        feedbackRepository.save(existing);
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
