package pl.edu.agh.kuce.planner.balance.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.kuce.planner.auth.persistence.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubBalanceRepository extends JpaRepository<SubBalance, Integer> {
    List<SubBalance> findByUser(User user);

    Optional<SubBalance> findByIdAndUser(Integer id, User user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE SubBalance sb SET sb.subBalance = :subBalance WHERE sb.user = :user AND sb.id = :id")
    void updateSubBalanceByUserAndId(
            @Param("user") User user,
            @Param("id") Integer id,
            @Param("subBalance") Integer subBalance);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM SubBalance sb WHERE sb.user = :user")
    void deleteByUser(@Param("user") User user);
}
