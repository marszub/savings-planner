package pl.edu.agh.kuce.planner.event.persistence;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name="oneTimeEvents")
public class OneTimeEvent {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Timestamp timestamp;

    public OneTimeEvent() {}

    public OneTimeEvent(Integer userId, String title, Double amount, Timestamp timestamp) {
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent event = (pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
