package socialnetwork.repository.db;

import socialnetwork.model.Entity;
import socialnetwork.repository.Repository;

public abstract class AbstractDBRepository <ID, E extends Entity<ID>> implements Repository<ID,E> {
    protected final String url;
    protected final String username;
    protected final String password;

    public AbstractDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
