package pl.edu.agh.kuce.planner.balance.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {
    List<Balance> findByUser(User user);

    @Modifying
    @Query("UPDATE Balance b SET b.balance = :balance WHERE b.user = :user")
    void updateBalance(@Param("user") User user, @Param("balance") Integer balance);
}
