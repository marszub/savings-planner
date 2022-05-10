package pl.edu.agh.kuce.planner.event.persistence;

import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.event.dto.CyclicEventDataInput;
import pl.edu.agh.kuce.planner.event.dto.EventData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cyclicEvents")
public class CyclicEvent {
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
    private Calendar begin;

    @Column(nullable = false)
    private Integer cycleBase;

    @Column(nullable = false)
    private Integer cycleLength;

    public CyclicEvent() { }

    public CyclicEvent(
            final User user,
            final String title,
            final Integer amount,
            final Calendar begin,
            final Integer cycleBase,
            final Integer cycleLength) {
        this.user = user;
        this.title = title;
        this.amount = amount;
        this.begin = begin;
        this.cycleBase = cycleBase;
        this.cycleLength = cycleLength;
    }

    public CyclicEvent(final CyclicEventDataInput cyclicEventData, final User user) {
        this(
                user,
                cyclicEventData.title(),
                cyclicEventData.amount(),
                Calendar.getInstance(),
                cyclicEventData.cycleBase(),
                cyclicEventData.cycleLength()
        );
        setBegin(cyclicEventData.begin());
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

    public List<EventData> getFromInterval(final Long start, final Long end) {
        final List<EventData> events = new LinkedList<>();
        final Calendar iterator = (Calendar) begin.clone();

        final Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(start * 1000);
        final Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(end * 1000);

        while (!iterator.after(endCalendar)) {
            if (!iterator.before(startCalendar)) {
                events.add(new EventData(id, title, amount, iterator.getTimeInMillis() / 1000));
            }
            iterator.add(cycleBase, cycleLength);
        }

        return events;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CyclicEvent event = (CyclicEvent) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
