package ru.kpfu.itis.semestrovka_2sem.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.semestrovka_2sem.model.TutorRequest;
import ru.kpfu.itis.semestrovka_2sem.model.Tutor;

import java.util.List;

@Repository
public interface TutorRequestRepository extends JpaRepository<TutorRequest, Long> {
    /** Все заявки данного репетитора. Загружаем связанные сущности для избежания LazyInitializationException. */
    @EntityGraph(attributePaths = {"tutor.user", "subject"})
    List<TutorRequest> findAllByTutor(Tutor tutor);

    /** Поиск заявок по предмету. */
    @EntityGraph(attributePaths = {"tutor.user", "subject"})
    List<TutorRequest> findAllBySubject_Name(String subjectName);

    // При необходимости: получить все заявки, у которых пока нет откликов
    @Query("""
        SELECT tr FROM TutorRequest tr
         WHERE tr.id NOT IN (
             SELECT sr.tutorRequest.id FROM StudentResponse sr
         )
    """)
    @EntityGraph(attributePaths = {"tutor.user", "subject"})
    List<TutorRequest> findAllWithoutResponses();

    /** Получить все заявки вместе с репетитором и предметом. */
    @EntityGraph(attributePaths = {"tutor.user", "subject"})
    @Query("select tr from TutorRequest tr")
    List<TutorRequest> findAllWithDetails();
}
