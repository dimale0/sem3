package ru.kpfu.itis.semestrovka_2sem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.semestrovka_2sem.model.Tutor;
import ru.kpfu.itis.semestrovka_2sem.model.User;

import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
    // Найти профиль репетитора по связанному User
    Optional<Tutor> findByUser(User user);
}

