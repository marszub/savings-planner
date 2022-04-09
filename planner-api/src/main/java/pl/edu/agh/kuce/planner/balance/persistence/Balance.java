package pl.edu.agh.kuce.planner.balance.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name="balances")
public class Balance {
    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer balance;

    public Balance(){}

    public Balance(Integer userId, Integer balance){
        this.userId = userId;
        this.balance = balance;
    }

    void setUserId(Integer userId){
        this.userId = userId;
    }

    void setBalance(Integer balance){
        this.balance = balance;
    }

    Integer getUserId(){
        return this.userId;
    }

    Integer getBalance(){
        return this.balance;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return Objects.equals(userId, balance.userId) && Objects.equals(this.balance, balance.balance);
    }

    @Override
    public int hashCode(){
        return Objects.hash(userId);
    }
}
