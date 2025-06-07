package ru.kpfu.itis.semestrovka_2sem.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tutors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // One-to-One: User ↔ Tutor
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    private User user;

    @Column(name = "experience", nullable = false)
    private Integer experience = 0;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // One-to-Many: Tutor → TutorRequest
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<TutorRequest> requests;

    // Many-to-Many: Tutor ↔ Subject через таблицу tutors_subjects
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tutors_subjects",
            joinColumns = @JoinColumn(name = "tutor_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )

    @ToString.Exclude
    private Set<Subject> subjects;
}
