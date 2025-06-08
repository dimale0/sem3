package ru.kpfu.itis.semestrovka_2sem.service;

import ru.kpfu.itis.semestrovka_2sem.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectService {

    /**
     * Создаёт новый предмет.
     * @param subject сущность Subject с заполненным полем name
     * @return сохранённый Subject (с присвоенным ID)
     * @throws IllegalArgumentException, если name пуст или такой subject уже есть
     */
    Subject create(Subject subject);

    /**
     * Находит Subject по его ID.
     * @param id идентификатор
     * @return Optional.empty(), если не найден
     */
    Optional<Subject> findById(Long id);

    /**
     * Находит Subject по точному совпадению name.
     * @param name уникальное название предмета
     * @return Optional.empty(), если не найден
     */
    Optional<Subject> findByName(String name);

    /**
     * Поиск предмета без учёта регистра.
     */
    Optional<Subject> findByNameIgnoreCase(String name);

    /**
     * Возвращает список всех предметов.
     * @return может быть пустым списком
     */
    List<Subject> findAll();

    /**
     * Изменяет название существующего Subject.
     * @param id     ID предмета, который хотим обновить
     * @param newName новое название (не пустое, не дублирует другой subject)
     * @return обновлённый Subject
     * @throws EntityNotFoundException    если предмет с данным ID не найден
     * @throws IllegalArgumentException   если newName пуст или уже используется
     */
    Subject updateName(Long id, String newName);

    /**
     * Удаляет Subject по ID.
     * @param id идентификатор
     * @throws EntityNotFoundException, если предмета с таким ID нет
     */
    void deleteById(Long id);
}
