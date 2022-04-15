package pl.edu.agh.kuce.planner.balance.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "balances")
public class Balance {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer balance;

    public Balance() { }

    public Balance(final User user, final Integer balance) {
        this.user = user;
        this.balance = balance;
    }

    void setUser(final User user) {
        this.user = user;
    }

    void setBalance(final Integer balance) {
        this.balance = balance;
    }

    User getUser() {
        return this.user;
    }

    Integer getBalance() {
        return this.balance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Balance balance = (Balance) o;
        return Objects.equals(this.user, balance.user) && Objects.equals(this.balance, balance.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
