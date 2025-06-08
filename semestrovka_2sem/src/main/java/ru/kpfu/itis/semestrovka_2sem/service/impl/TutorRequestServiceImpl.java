package ru.kpfu.itis.semestrovka_2sem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.semestrovka_2sem.dto.TutorRequestCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.Subject;
import ru.kpfu.itis.semestrovka_2sem.model.Tutor;
import ru.kpfu.itis.semestrovka_2sem.model.TutorRequest;
import ru.kpfu.itis.semestrovka_2sem.repository.SubjectRepository;
import ru.kpfu.itis.semestrovka_2sem.repository.TutorRepository;
import ru.kpfu.itis.semestrovka_2sem.repository.TutorRequestRepository;
import ru.kpfu.itis.semestrovka_2sem.service.TutorRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorRequestServiceImpl implements TutorRequestService {

    private static final List<Integer> ALLOWED_DURATIONS = List.of(45, 60, 90);

    private final TutorRequestRepository tutorRequestRepository;
    private final TutorRepository tutorRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public TutorRequest create(TutorRequestCreateDto createDto) {
        // 1) Проверка Tutor
        if (createDto.getTutorId() == null) {
            throw new IllegalArgumentException("TutorId не задан");
        }
        Tutor tutor = tutorRepository.findById(createDto.getTutorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tutor с ID " + createDto.getTutorId() + " не найден"));

        // 2) Определяем Subject: либо по ID, либо по имени
        Subject subject;
        if (createDto.getSubjectId() != null) {
            subject = subjectRepository.findById(createDto.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Subject с ID " + createDto.getSubjectId() + " не найден"));
        } else {
            String subjName = createDto.getSubjectName();
            if (subjName == null || subjName.isBlank()) {
                throw new IllegalArgumentException("Название предмета не задано");
            }
            subjName = subjName.trim();
            final String finalSubjName = subjName;
            subject = subjectRepository.findByNameIgnoreCase(finalSubjName)
                    .orElseGet(() -> subjectRepository.save(Subject.builder().name(finalSubjName).build()));
        }

        // 3) Проверка price > 0
        if (createDto.getPrice() == null || createDto.getPrice() <= 0) {
            throw new IllegalArgumentException("Price должен быть > 0");
        }

        // 4) Проверка duration ∈ {45,60,90}
        if (createDto.getDuration() == null
                || !ALLOWED_DURATIONS.contains(createDto.getDuration())) {
            throw new IllegalArgumentException(
                    "Duration должен быть одним из " + ALLOWED_DURATIONS);
        }

        // 5) Собираем сущность TutorRequest
        TutorRequest entity = TutorRequest.builder()
                .tutor(tutor)
                .subject(subject)
                .price(createDto.getPrice())
                .duration(createDto.getDuration())
                .description(createDto.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        return tutorRequestRepository.save(entity);
    }

    @Override
    public Optional<TutorRequest> findById(Long id) {
        return tutorRequestRepository.findById(id);
    }

    @Override
    public List<TutorRequest> findAll() {
        return tutorRequestRepository.findAllWithDetails();
    }

    @Override
    public List<TutorRequest> findAllByTutorId(Long tutorId) {
        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tutor с ID " + tutorId + " не найден"));
        return tutorRequestRepository.findAllByTutor(tutor);
    }

    @Override
    public List<TutorRequest> findAllWithoutResponses() {
        return tutorRequestRepository.findAllWithoutResponses();
    }

    @Override
    public List<TutorRequest> findAllBySubjectName(String subjectName) {
        return tutorRequestRepository.findAllBySubject_Name(subjectName);
    }

    @Override
    @Transactional
    public TutorRequest update(Long id, TutorRequestCreateDto updateDto) {
        TutorRequest existing = tutorRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "TutorRequest с ID " + id + " не найден"));

        // Проверяем, что tutorId не меняется
        if (updateDto.getTutorId() == null) {
            throw new IllegalArgumentException("TutorId обязателен при обновлении");
        }
        if (!existing.getTutor().getId().equals(updateDto.getTutorId())) {
            throw new IllegalArgumentException("Нельзя менять TutorId для существующего запроса");
        }

        // Проверяем и, если нужно, меняем Subject
        Subject newSubject = null;
        if (updateDto.getSubjectId() != null) {
            newSubject = subjectRepository.findById(updateDto.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Subject с ID " + updateDto.getSubjectId() + " не найден"));
        } else if (updateDto.getSubjectName() != null && !updateDto.getSubjectName().isBlank()) {
            String subjName = updateDto.getSubjectName().trim();
            final String finalSubjName = subjName;
            newSubject = subjectRepository.findByNameIgnoreCase(finalSubjName)

                    .orElseGet(() -> subjectRepository.save(Subject.builder().name(finalSubjName).build()));
        }
        if (newSubject != null && !existing.getSubject().getId().equals(newSubject.getId())) {
            existing.setSubject(newSubject);
        }

        // Проверка нового price
        if (updateDto.getPrice() == null || updateDto.getPrice() <= 0) {
            throw new IllegalArgumentException("Price должен быть > 0");
        }
        existing.setPrice(updateDto.getPrice());

        // Проверка нового duration
        if (updateDto.getDuration() == null
                || !ALLOWED_DURATIONS.contains(updateDto.getDuration())) {
            throw new IllegalArgumentException(
                    "Duration должен быть одним из " + ALLOWED_DURATIONS);
        }
        existing.setDuration(updateDto.getDuration());

        // Обновляем описание
        existing.setDescription(updateDto.getDescription());

        return tutorRequestRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!tutorRequestRepository.existsById(id)) {
            throw new EntityNotFoundException("TutorRequest с ID " + id + " не найден");
        }
        tutorRequestRepository.deleteById(id);
    }
}
