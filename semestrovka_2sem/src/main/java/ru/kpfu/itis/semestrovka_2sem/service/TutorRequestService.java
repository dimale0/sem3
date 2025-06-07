package ru.kpfu.itis.semestrovka_2sem.service;

import ru.kpfu.itis.semestrovka_2sem.dto.TutorRequestCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.TutorRequest;

import java.util.List;
import java.util.Optional;

public interface TutorRequestService {

    /**
     * Создаёт новый TutorRequest на основе входного DTO.
     * Возвращает созданную сущность (с уже заполненным id и createdAt).
     */
    TutorRequest create(TutorRequestCreateDto createDto);

    /**
     * Находит TutorRequest по ID.
     */
    Optional<TutorRequest> findById(Long id);

    /**
     * Возвращает все TutorRequest (список сущностей).
     */
    List<TutorRequest> findAll();

    /**
     * Возвращает все TutorRequest данного Tutor (по tutorId).
     */
    List<TutorRequest> findAllByTutorId(Long tutorId);

    /**
     * Обновляет существующий TutorRequest:
     *   - id берем из параметра,
     *   - новые поля (price, duration, description, subjectId) в createDto,
     *   - tutorId менять запрещено (проверяем).
     *
     * Возвращает обновлённую сущность.
     */
    TutorRequest update(Long id, TutorRequestCreateDto updateDto);

    /**
     * Удаляет TutorRequest по ID.
     */
    void deleteById(Long id);

    /**
     * Возвращает все TutorRequest, на которые пока нет откликов студентов.
     */
    List<TutorRequest> findAllWithoutResponses();

    /**
     * Возвращает все TutorRequest, в которых предмет имеет указанное имя.
     */
    List<TutorRequest> findAllBySubjectName(String subjectName);
}
