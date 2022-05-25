package pl.edu.agh.kuce.planner.goal.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;
import java.util.Optional;

public interface SubGoalRepository extends JpaRepository<SubGoal, Integer> {
    @Query("SELECT g FROM SubGoal g where g.id = ?1 AND g.goal.user = ?2")
    Optional<SubGoal> getSubGoalById(Integer id, User user);

    @Query("SELECT g FROM SubGoal g where g.goal = ?1 AND g.goal.user = ?2")
    List<SubGoal> getSubGoals(Goal goal, User user);

    @Modifying
    @Query("DELETE FROM SubGoal g where g.id = ?1 AND g.goal = ?2")
    void deleteSubGoal(Integer id, Goal goal);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE SubGoal g SET g.completed = TRUE WHERE g.goal = :goal AND g.id = :subGoalId")
    void completeSubGoal(@Param("subGoalId") Integer id, @Param("goal") Goal goal);
}
