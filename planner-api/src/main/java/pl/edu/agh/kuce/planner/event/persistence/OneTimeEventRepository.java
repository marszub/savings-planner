package pl.edu.agh.kuce.planner.event.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;
import java.util.Optional;

public interface OneTimeEventRepository extends JpaRepository<OneTimeEvent, Integer> {

    List<OneTimeEvent> findByUser(User user);

    @Query("SELECT event FROM OneTimeEvent event WHERE event.id = ?1 AND event.user = ?2")
    Optional<OneTimeEvent> getEventById(Integer id, User user);

    @Modifying
    @Query("UPDATE OneTimeEvent AS event SET event.title = ?1 WHERE event.id = ?2 AND event.user = ?3")
    void updateTitle(String title, Integer id, User user);

    @Modifying
    @Query("UPDATE OneTimeEvent AS event SET event.amount = ?1 WHERE event.id = ?2 AND event.user = ?3")
    void updateAmount(Integer amount, Integer id, User user);

    @Modifying
    @Query("UPDATE OneTimeEvent AS event SET event.timestamp = ?1 WHERE event.id = ?2 AND event.user = ?3")
    void updateTimestamp(Long timestamp, Integer id, User user);

    @Modifying
    @Query("DELETE FROM OneTimeEvent event WHERE event.id = ?1 AND event.user = ?2")
    void deleteEvent(Integer id, User user);
}
