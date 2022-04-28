package pl.edu.agh.kuce.planner.goal.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
    @Query("SELECT g FROM Goal g where g.id = ?1 AND g.user = ?2")
    Optional<Goal> getGoalById(Integer id, User user);

    List<Goal> findByUserOOrderByPriorityDesc(User user);

    @Modifying
    @Query("DELETE FROM Goal g where g.id = ?1 AND g.user = ?2")
    void deleteGoal(Integer id, User user);
}
