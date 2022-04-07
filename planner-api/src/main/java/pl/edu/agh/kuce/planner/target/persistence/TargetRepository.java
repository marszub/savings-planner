package pl.edu.agh.kuce.planner.target.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface TargetRepository extends  JpaRepository<Target, Integer>{
    @Query("SELECT t FROM Target t WHERE t.user.nick = ?1")
    Collection<Target> findByUsersNick(String nick);
}
