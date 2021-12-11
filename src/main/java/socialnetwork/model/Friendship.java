package socialnetwork.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Tuple<String>> {
    LocalDateTime date;

    public Friendship(String username1, String username2, LocalDateTime date) {
        Tuple<String> t = new Tuple<>(username1, username2);
        t.orderTuple();
        this.setId(t);
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Friendship that = (Friendship) obj;
        return Objects.equals(this.getId().getFirst(), that.getId().getFirst()) && Objects.equals(this.getId().getSecond(), that.getId().getSecond());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId().getFirst(), this.getId().getSecond());
    }
}
