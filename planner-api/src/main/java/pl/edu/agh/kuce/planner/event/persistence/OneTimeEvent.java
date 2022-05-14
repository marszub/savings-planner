package pl.edu.agh.kuce.planner.event.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.OneTimeEventDataInput;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "oneTimeEvents")
public class OneTimeEvent extends Event {
    @Column(nullable = false)
    private Instant timestamp;

    public OneTimeEvent() { }

    public OneTimeEvent(final User user, final String title, final Integer amount, final Instant timestamp) {
        super(user, title, amount);
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

    public Long getTimestamp() {
        return timestamp.getEpochSecond();
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = Instant.ofEpochSecond(timestamp);
    }
}
