package ru.kpfu.itis.semestrovka_2sem.service;

import ru.kpfu.itis.semestrovka_2sem.dto.TutorCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.Tutor;
import ru.kpfu.itis.semestrovka_2sem.model.User;

import java.util.List;
import java.util.Optional;

public interface TutorService {

    Tutor create(TutorCreateDto dto);

    Optional<Tutor> findById(Long id);

    List<Tutor> findAll();

    Tutor update(Long tutorId, TutorCreateDto dto);

    void deleteById(Long id);

    /**
     * Возвращает профиль репетитора по связанному пользователю.
     */
    Optional<Tutor> findByUser(User user);
}
