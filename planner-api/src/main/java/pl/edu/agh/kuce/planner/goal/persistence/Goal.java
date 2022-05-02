package pl.edu.agh.kuce.planner.goal.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.goal.dto.GoalInputData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table(name = "goals", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "priority" }) })
public class Goal {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer amount;

    @Column(name="priority", nullable = false)
    private Integer priority;

    public Goal() { }

    public Goal(final User user, final String title, final Integer amount, final Integer priority) {
        this.user = user;
        this.title = title;
        this.amount = amount;
        this.priority = priority;
    }

    public Goal(final User user, final GoalInputData data) {
        this(
                user,
                data.title(),
                data.amount(),
                data.priority()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(final Integer amount) {
        this.amount = amount;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Goal target = (Goal) o;
        return Objects.equals(id, target.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
