package ru.kpfu.itis.semestrovka_2sem.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(
        name = "subjects",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Many-to-Many: Subject ↔ Tutor
    @ManyToMany(mappedBy = "subjects", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Tutor> tutors;
}

