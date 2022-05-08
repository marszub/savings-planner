package pl.edu.agh.kuce.planner.balance.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "subBalances")
public class SubBalance {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "balance_user")
    private User user;

    @Column(nullable = false)
    private Integer subBalance;

    public SubBalance() { }

    public SubBalance(final User user, final Integer subBalance) {
        this.user = user;
        this.subBalance = subBalance;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public void setSubBalance(final Integer subBalance) {
        this.subBalance = subBalance;
    }

    public User getUser() {
        return this.user;
    }

    public Integer getSubBalance() {
        return this.subBalance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubBalance subBalance = (SubBalance) o;
        return Objects.equals(this.user, subBalance.user) && Objects.equals(this.subBalance, subBalance.subBalance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, subBalance);
    }
}
