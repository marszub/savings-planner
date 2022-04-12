package pl.edu.agh.kuce.planner.target.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;

public interface TargetRepository extends  JpaRepository<Target, Integer> {
    List<Target> findByUser(User user);
}
