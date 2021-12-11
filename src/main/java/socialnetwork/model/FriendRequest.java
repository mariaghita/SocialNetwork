package socialnetwork.model;

import java.util.Objects;

public class FriendRequest extends Entity<Tuple<String>> {

    public FriendRequest(String username1, String username2) {
        Tuple<String> t = new Tuple<>(username1, username2);
        this.setId(t);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FriendRequest that = (FriendRequest) obj;
        return Objects.equals(this.getId().getFirst(), that.getId().getFirst()) && Objects.equals(this.getId().getSecond(), that.getId().getSecond());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId().getFirst(), this.getId().getSecond());
    }
}
