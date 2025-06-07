package ru.kpfu.itis.semestrovka_2sem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;
import ru.kpfu.itis.semestrovka_2sem.repository.SubjectRepository;
import ru.kpfu.itis.semestrovka_2sem.service.SubjectService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public Subject create(Subject subject) {
        // 1) Проверяем, что name не null/пустая строка
        String name = subject.getName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название предмета не может быть пустым");
        }
        // 2) Проверяем, что нет дублирующего имени
        Optional<Subject> byName = subjectRepository.findByName(name.trim());
        if (byName.isPresent()) {
            throw new IllegalArgumentException("Предмет с именем \"" + name + "\" уже существует");
        }
        // 3) Сохраняем и возвращаем
        subject.setName(name.trim());
        return subjectRepository.save(subject);
    }

    @Override
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    @Override
    public Optional<Subject> findByName(String name) {
        return subjectRepository.findByName(name);
    }

    @Override
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    @Override
    @Transactional
    public Subject updateName(Long id, String newName) {
        // 1) Найдём существующий
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject с ID " + id + " не найден"));
        // 2) Проверим newName
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Новое название не может быть пустым");
        }
        newName = newName.trim();
        // 3) Если имя реально изменилось, проверим, что его нет в базе
        if (!existing.getName().equals(newName)) {
            String finalNewName = newName;
            subjectRepository.findByName(newName).ifPresent(s -> {
                throw new IllegalArgumentException("Другой предмет с именем \"" + finalNewName + "\" уже существует");
            });
            existing.setName(newName);
        }
        // 4) Сохраним изменения и вернём
        return subjectRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject с ID " + id + " не найден");
        }
        subjectRepository.deleteById(id);
    }
}
