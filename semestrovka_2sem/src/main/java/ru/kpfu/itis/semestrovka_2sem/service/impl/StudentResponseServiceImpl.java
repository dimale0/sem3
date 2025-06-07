package ru.kpfu.itis.semestrovka_2sem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.semestrovka_2sem.dto.StudentResponseCreateDto;
import ru.kpfu.itis.semestrovka_2sem.model.TutorRequest;
import ru.kpfu.itis.semestrovka_2sem.model.User;
import ru.kpfu.itis.semestrovka_2sem.model.StudentResponse;
import ru.kpfu.itis.semestrovka_2sem.repository.StudentResponseRepository;
import ru.kpfu.itis.semestrovka_2sem.repository.TutorRequestRepository;
import ru.kpfu.itis.semestrovka_2sem.repository.UserRepository;
import ru.kpfu.itis.semestrovka_2sem.service.StudentResponseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentResponseServiceImpl implements StudentResponseService {

    private final StudentResponseRepository studentResponseRepository;
    private final UserRepository userRepository;
    private final TutorRequestRepository tutorRequestRepository;

    @Override
    @Transactional
    public StudentResponse create(StudentResponseCreateDto createDto) {
        // 1) Проверка studentId
        if (createDto.getStudentId() == null) {
            throw new IllegalArgumentException("studentId обязателен");
        }
        User student = userRepository.findById(createDto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User (студент) с ID " + createDto.getStudentId() + " не найден"));

        // 2) Проверка requestId
        if (createDto.getRequestId() == null) {
            throw new IllegalArgumentException("requestId обязателен");
        }
        TutorRequest tutorRequest = tutorRequestRepository.findById(createDto.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "TutorRequest с ID " + createDto.getRequestId() + " не найден"));

        // 3) Проверка message
        String msg = createDto.getMessage();
        if (msg == null || msg.isBlank()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }
        if (msg.length() > 1000) {
            throw new IllegalArgumentException("Сообщение максимум 1000 символов");
        }

        // 4) Собираем и сохраняем сущность
        StudentResponse entity = StudentResponse.builder()
                .student(student)
                .tutorRequest(tutorRequest)
                .message(msg.trim())
                .createdAt(LocalDateTime.now())
                .build();

        return studentResponseRepository.save(entity);
    }

    @Override
    public Optional<StudentResponse> findById(Long id) {
        return studentResponseRepository.findById(id);
    }

    @Override
    public List<StudentResponse> findAll() {
        return studentResponseRepository.findAll();
    }

    @Override
    public List<StudentResponse> findAllByStudentId(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User (студент) с ID " + studentId + " не найден"));
        return studentResponseRepository.findAllByStudent(student);
    }

    @Override
    public List<StudentResponse> findAllByRequestId(Long requestId) {
        TutorRequest tutorRequest = tutorRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "TutorRequest с ID " + requestId + " не найден"));
        return studentResponseRepository.findAllByTutorRequest(tutorRequest);
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, StudentResponseCreateDto updateDto) {
        StudentResponse existing = studentResponseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "StudentResponse с ID " + id + " не найден"));

        // 1) studentId и requestId менять нельзя
        if (!existing.getStudent().getId().equals(updateDto.getStudentId())) {
            throw new IllegalArgumentException("Нельзя менять studentId");
        }
        if (!existing.getTutorRequest().getId().equals(updateDto.getRequestId())) {
            throw new IllegalArgumentException("Нельзя менять requestId");
        }

        // 2) Проверка нового message
        String msg = updateDto.getMessage();
        if (msg == null || msg.isBlank()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }
        if (msg.length() > 1000) {
            throw new IllegalArgumentException("Сообщение максимум 1000 символов");
        }
        existing.setMessage(msg.trim());

        return studentResponseRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!studentResponseRepository.existsById(id)) {
            throw new EntityNotFoundException("StudentResponse с ID " + id + " не найден");
        }
        studentResponseRepository.deleteById(id);
    }
}
