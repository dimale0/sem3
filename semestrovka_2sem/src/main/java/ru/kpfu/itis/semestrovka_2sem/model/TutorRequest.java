package ru.kpfu.itis.semestrovka_2sem.model;


import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "tutor_requests",
        indexes = {
                @Index(columnList = "tutor_id", name = "idx_tutor_requests_tutor_id"),
                @Index(columnList = "subject_id", name = "idx_tutor_requests_subject_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TutorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Many-to-One: Tutor → TutorRequest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    @ToString.Exclude
    private Tutor tutor;

    // Many-to-One: Subject → TutorRequest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @ToString.Exclude
    private Subject subject;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "duration", nullable = false)
    private Integer duration; // значения 45, 60, 90 проверяются в коде/DDL

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // One-to-Many: TutorRequest → StudentResponse
    @OneToMany(mappedBy = "tutorRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<StudentResponse> responses;
}
