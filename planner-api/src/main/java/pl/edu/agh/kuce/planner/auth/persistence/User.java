package pl.edu.agh.kuce.planner.auth.persistence;

import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.target.persistence.Target;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nick;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<OneTimeEvent> oneTimeEventList;

    @OneToMany(mappedBy = "user")
    private List<Target> targetList;

    public User() {}

    public User(String nick, String email, String passwordHash) {
        this.nick = nick;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
