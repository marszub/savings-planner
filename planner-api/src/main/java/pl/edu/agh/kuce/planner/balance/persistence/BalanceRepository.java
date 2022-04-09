package pl.edu.agh.kuce.planner.balance.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface BalanceRepository extends JpaRepository<Balance, Integer> {

    @Query("SELECT b FROM Balance b WHERE b.userId = ?1")
    Collection<Balance> findByUserId(Integer userId);
}
