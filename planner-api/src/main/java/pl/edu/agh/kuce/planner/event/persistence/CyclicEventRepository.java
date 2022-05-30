package pl.edu.agh.kuce.planner.event.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;
import java.util.Optional;

public interface CyclicEventRepository extends JpaRepository<CyclicEvent, Integer> {

    List<CyclicEvent> findByUser(User user);

    Optional<CyclicEvent> findByIdAndUser(Integer id, User user);

    @Modifying
    @Query("DELETE FROM CyclicEvent event WHERE event.id = ?1 AND event.user = ?2")
    void deleteEvent(Integer id, User user);
}
