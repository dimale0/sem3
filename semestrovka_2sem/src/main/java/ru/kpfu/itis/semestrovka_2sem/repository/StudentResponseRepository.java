package ru.kpfu.itis.semestrovka_2sem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.semestrovka_2sem.model.StudentResponse;
import ru.kpfu.itis.semestrovka_2sem.model.TutorRequest;
import ru.kpfu.itis.semestrovka_2sem.model.User;

import java.util.List;

@Repository
public interface StudentResponseRepository extends JpaRepository<StudentResponse, Long> {
    // Все отклики на данную заявку
    List<StudentResponse> findAllByTutorRequest(TutorRequest tutorRequest);

    // Все отклики конкретного студента
    List<StudentResponse> findAllByStudent(User student);

    // Пример подзапроса: последние отклики (группировка по студенту)
    @Query("""
        SELECT sr FROM StudentResponse sr
         WHERE sr.id IN (
             SELECT MAX(s2.id) FROM StudentResponse s2 GROUP BY s2.student
         )
    """)
    List<StudentResponse> findLatestResponsePerStudent();
}