package pl.edu.agh.kuce.planner.goal.persistence;

import pl.edu.agh.kuce.planner.goal.dto.SubGoalInputData;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "subGoals")
public class SubGoal {

    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false)
    private Integer id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(nullable = false)
    private String title;

    public SubGoal() {
    }

    public SubGoal(final Goal goal, final String title) {
        this.goal = goal;
        this.title = title;
    }

    public SubGoal(final Goal goal, final SubGoalInputData data) {
        this(
                goal,
                data.title()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(final Goal goal) {
        this.goal = goal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubGoal subGoal = (SubGoal) o;
        return Objects.equals(id, subGoal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
