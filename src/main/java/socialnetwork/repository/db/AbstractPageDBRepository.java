package socialnetwork.repository.db;

import socialnetwork.model.Entity;
import socialnetwork.repository.paging.PagingRepository;

public abstract class AbstractPageDBRepository <ID, E extends Entity<ID>>  implements PagingRepository<ID, E> {
    protected final String url;
    protected final String username;
    protected final String password;

    public AbstractPageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
