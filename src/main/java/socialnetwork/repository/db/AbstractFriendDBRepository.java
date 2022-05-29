package socialnetwork.repository.db;

import socialnetwork.model.Entity;
import socialnetwork.model.Tuple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractFriendDBRepository<ID, E extends Entity<ID>> extends AbstractPageDBRepository<ID,E> {
    public AbstractFriendDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     *executes a delete query containing 2 usernames
     * @param usernames the usernames for which we execute the query
     * @param query the query we want to execute
     * @return null
     */
    protected E deleteQuery(Tuple<String> usernames, String query){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, usernames.getFirst());
            statement.setString(2, usernames.getSecond());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
