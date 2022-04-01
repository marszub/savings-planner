package pl.edu.agh.kuce.planner.auth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.nick = ?1 OR u.email = ?1")
    Optional<User> findOneByNickOrEmail(String nickOrEmail);
}
