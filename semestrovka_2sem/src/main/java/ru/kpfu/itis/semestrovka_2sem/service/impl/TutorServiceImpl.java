package ru.kpfu.itis.semestrovka_2sem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.semestrovka_2sem.dto.TutorCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;
import ru.kpfu.itis.semestrovka_2sem.model.Tutor;
import ru.kpfu.itis.semestrovka_2sem.model.User;
import ru.kpfu.itis.semestrovka_2sem.model.enums.UserRole;
import ru.kpfu.itis.semestrovka_2sem.repository.SubjectRepository;
import ru.kpfu.itis.semestrovka_2sem.repository.TutorRepository;
import ru.kpfu.itis.semestrovka_2sem.repository.UserRepository;
import ru.kpfu.itis.semestrovka_2sem.service.TutorService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorServiceImpl implements TutorService {

    private final TutorRepository tutorRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public Tutor create(TutorCreateDto dto) {
        // 1) Загрузим и проверим User
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User с ID " + dto.getUserId() + " не найден"));
        if (user.getRole() != UserRole.TUTOR) {
            throw new IllegalArgumentException("У пользователя нет роли TUTOR");
        }
        if (tutorRepository.findByUser(user).isPresent()) {
            throw new IllegalArgumentException("Профиль Tutor для этого пользователя уже существует");
        }

        // 2) Собираем список предметов: выбранные + возможно новый
        Set<Subject> subjects = dto.getSubjectIds() == null ?
                new java.util.HashSet<>() :
                dto.getSubjectIds().stream()
                        .map(id -> subjectRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Subject с ID " + id + " не найден")))
                        .collect(Collectors.toSet());

        if (dto.getNewSubjectName() != null && !dto.getNewSubjectName().isBlank()) {
            String name = dto.getNewSubjectName().trim();
            Subject extra = subjectRepository.findByNameIgnoreCase(name)

                    .orElseGet(() -> subjectRepository.save(Subject.builder().name(name).build()));
            subjects.add(extra);
        }

        if (subjects.isEmpty()) {
            throw new IllegalArgumentException("Нужно выбрать хотя бы один предмет");
        }

        // 3) Собираем сущность Tutor
        Tutor tutor = new Tutor();
        tutor.setUser(user);
        tutor.setExperience(dto.getExperience());
        tutor.setDescription(dto.getDescription());
        tutor.setSubjects(subjects);

        return tutorRepository.save(tutor);
    }

    @Override
    public Optional<Tutor> findById(Long id) {
        return tutorRepository.findById(id);
    }

    @Override
    public List<Tutor> findAll() {
        return tutorRepository.findAll();
    }

    @Override
    @Transactional
    public Tutor update(Long tutorId, TutorCreateDto dto) {
        Tutor existing = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new EntityNotFoundException("Tutor с ID " + tutorId + " не найден"));

        if (!existing.getUser().getId().equals(dto.getUserId())) {
            throw new IllegalArgumentException("Нельзя менять User у существующего профиля");
        }

        // Обновляем поля
        existing.setExperience(dto.getExperience());
        existing.setDescription(dto.getDescription());

        // Перезагружаем Subject + возможно добавляем новый
        Set<Subject> newSubjects = dto.getSubjectIds() == null ?
                new java.util.HashSet<>() :
                dto.getSubjectIds().stream()
                        .map(id -> subjectRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Subject с ID " + id + " не найден")))
                        .collect(Collectors.toSet());

        if (dto.getNewSubjectName() != null && !dto.getNewSubjectName().isBlank()) {
            String name = dto.getNewSubjectName().trim();
            Subject extra = subjectRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> subjectRepository.save(Subject.builder().name(name).build()));
            newSubjects.add(extra);
        }

        if (newSubjects.isEmpty()) {
            throw new IllegalArgumentException("Нужно выбрать хотя бы один предмет");
        }
        existing.setSubjects(newSubjects);

        return tutorRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!tutorRepository.existsById(id)) {
            throw new EntityNotFoundException("Tutor с ID " + id + " не найден");
        }
        tutorRepository.deleteById(id);
    }

    @Override
    public Optional<Tutor> findByUser(User user) {
        return tutorRepository.findByUser(user);
    }
}
