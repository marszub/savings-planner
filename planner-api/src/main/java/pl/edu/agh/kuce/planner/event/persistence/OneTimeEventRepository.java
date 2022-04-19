package pl.edu.agh.kuce.planner.event.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;

public interface OneTimeEventRepository extends JpaRepository<OneTimeEvent, Integer> {

    List<OneTimeEvent> findByUser(User user);
}
