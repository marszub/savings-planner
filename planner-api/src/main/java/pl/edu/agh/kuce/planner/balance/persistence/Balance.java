package pl.edu.agh.kuce.planner.balance.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="balances")
public class Balance {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer balance;

    public Balance(){}

    public Balance(User user, Integer balance){
        this.user = user;
        this.balance = balance;
    }

    void setUser(User user){
        this.user = user;
    }

    void setBalance(Integer balance){
        this.balance = balance;
    }

    User getUser(){
        return this.user;
    }

    Integer getBalance(){
        return this.balance;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return Objects.equals(this.user, balance.user) && Objects.equals(this.balance, balance.balance);
    }

    @Override
    public int hashCode(){
        return Objects.hash(user);
    }
}
