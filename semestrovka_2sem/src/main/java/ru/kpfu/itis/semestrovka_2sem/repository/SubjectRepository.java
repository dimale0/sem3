package ru.kpfu.itis.semestrovka_2sem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Найти предмет по точному имени
    Optional<Subject> findByName(String name);

    // Поиск без учёта регистра, чтобы избежать дублей
    Optional<Subject> findByNameIgnoreCase(String name);
}

