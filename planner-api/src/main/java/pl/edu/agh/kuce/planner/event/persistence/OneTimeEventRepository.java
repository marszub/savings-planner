package pl.edu.agh.kuce.planner.event.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface OneTimeEventRepository extends JpaRepository<OneTimeEvent, Integer> {

    @Query("SELECT o FROM OneTimeEvent o WHERE o.userId = ?1")
    Collection<OneTimeEvent> findByUserId(Integer userId);
}
