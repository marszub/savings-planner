package pl.edu.agh.kuce.planner.goal.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;

public interface GoalRepository extends  JpaRepository<Goal, Integer>{
    List<Goal> findByUser(User user);
}
