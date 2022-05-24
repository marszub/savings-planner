package pl.edu.agh.kuce.planner.event.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.EventDataInput;
import pl.edu.agh.kuce.planner.event.dto.EventTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "cyclicEvents")
public class CyclicEvent extends Event {
    @Column(nullable = false)
    private Calendar begin;

    @Column(nullable = false)
    private Integer cycleBase;

    @Column(nullable = false)
    private Integer cycleLength;

    @Column(nullable = false)
    private Integer cycleCount;

    public CyclicEvent() { }

    public CyclicEvent(
            final User user,
            final String title,
            final Integer amount,
            final Calendar begin,
            final Integer cycleBase,
            final Integer cycleLength,
            final Integer cycleCount) {
        super(user, title, amount);
        this.begin = begin;
        this.cycleBase = cycleBase;
        this.cycleLength = cycleLength;
        this.cycleCount = cycleCount;
    }

    public CyclicEvent(final EventDataInput eventDataInput, final User user) {
        this(
                user,
                eventDataInput.title(),
                eventDataInput.amount(),
                Calendar.getInstance(),
                eventDataInput.cycleBase(),
                eventDataInput.cycleLength(),
                eventDataInput.cycleCount()
        );
        setBegin(eventDataInput.begin());
    }

    public Long getBegin() {
        return begin.getTimeInMillis() / 1000;
    }

    public void setBegin(final Long timestamp) {
        this.begin.setTimeInMillis(timestamp * 1000);
    }

    public Integer getCycleBase() {
        return cycleBase;
    }

    public void setCycleBase(final Integer cycleBase) {
        this.cycleBase = cycleBase;
    }

    public Integer getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(final Integer cycleLength) {
        this.cycleLength = cycleLength;
    }

    public Integer getCycleCount() {
        return cycleCount;
    }

    public void setCycleCount(final Integer cycleCount) {
        this.cycleCount = cycleCount;
    }

    public List<EventTimestamp> getFollowingN(final Long start, final Integer distinctEventsNum) {
        final List<EventTimestamp> events = new LinkedList<>();
        final Calendar iterator = (Calendar) begin.clone();

        final Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(start * 1000);

        while (!iterator.after(startCalendar)) {
            iterator.add(cycleBase, cycleLength);
        }
        for (int i = 0; i < distinctEventsNum; i++) {
            events.add(new EventTimestamp(getId(), getTitle(), getAmount(), iterator.getTimeInMillis() / 1000));
            iterator.add(cycleBase, cycleLength);
        }
        return events;
    }
}
