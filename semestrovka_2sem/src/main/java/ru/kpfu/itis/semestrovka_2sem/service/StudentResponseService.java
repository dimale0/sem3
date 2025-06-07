package ru.kpfu.itis.semestrovka_2sem.service;

import ru.kpfu.itis.semestrovka_2sem.dto.StudentResponseCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.StudentResponse;

import java.util.List;
import java.util.Optional;

public interface StudentResponseService {

    /**
     * Создаёт новый StudentResponse на основе входного DTO.
     * Возвращает созданную сущность (с id и createdAt).
     */
    StudentResponse create(StudentResponseCreateDto createDto);

    /**
     * Находит StudentResponse по ID.
     */
    Optional<StudentResponse> findById(Long id);

    /**
     * Возвращает все StudentResponse (список сущностей).
     */
    List<StudentResponse> findAll();

    /**
     * Возвращает все отклики конкретного студента (по studentId).
     */
    List<StudentResponse> findAllByStudentId(Long studentId);

    /**
     * Возвращает все отклики к заданному TutorRequest (по requestId).
     */
    List<StudentResponse> findAllByRequestId(Long requestId);

    /**
     * Обновляет существующий StudentResponse:
     * id передаем параметром, новые данные (message) – в DTO.
     * studentId и requestId менять нельзя.
     * Возвращает обновлённую сущность.
     */
    StudentResponse update(Long id, StudentResponseCreateDto updateDto);

    /**
     * Удаляет StudentResponse по ID.
     */
    void deleteById(Long id);
}
