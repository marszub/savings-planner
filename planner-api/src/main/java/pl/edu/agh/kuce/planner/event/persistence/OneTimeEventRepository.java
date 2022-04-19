package pl.edu.agh.kuce.planner.event.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;

public interface OneTimeEventRepository extends JpaRepository<OneTimeEvent, Integer> {

    @Query("SELECT e FROM OneTimeEvent e WHERE e.user = ?1")
    List<OneTimeEvent> findByUser(User user);
}
