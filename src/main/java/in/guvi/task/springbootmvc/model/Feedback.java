package in.guvi.task.springbootmvc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "Feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "ReaderName")
    private String name;
    @Column(name = "BookName")
    private String bookName;
    @Column(name = "Feedback")
    private String feedback;
}
