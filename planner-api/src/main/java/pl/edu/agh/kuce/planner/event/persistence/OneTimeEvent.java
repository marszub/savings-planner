package pl.edu.agh.kuce.planner.event.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "oneTimeEvents")
public class OneTimeEvent {
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

    @Column(nullable = false)
    private Instant timestamp;

    public OneTimeEvent() { }

    public OneTimeEvent(final User user, final String title, final Integer amount, final Instant timestamp) {
        this.user = user;
        this.title = title;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public OneTimeEvent(final OneTimeEventDataInput oneTimeEventData, final User user) {
        this(
                user,
                oneTimeEventData.title(),
                oneTimeEventData.amount(),
                Instant.ofEpochSecond(oneTimeEventData.timestamp())
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

    public Long getTimestamp() {
        return timestamp.getEpochSecond();
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = Instant.ofEpochSecond(timestamp);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OneTimeEvent event = (OneTimeEvent) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
