package socialnetwork.repository.db;

import socialnetwork.model.Friendship;
import socialnetwork.model.Tuple;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDBRepository extends AbstractFriendDBRepository<Tuple<String>, Friendship> {
    public FriendshipDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * finds a friendship between 2 certain users
     *
     * @param usernames the usernames of the friendship we are looking for
     * @return the friendship if found, null otherwise
     */
    @Override
    public Friendship findOne(Tuple<String> usernames) {
        Friendship friendship = null;
        String sql = "SELECT * FROM friendships WHERE user1 = '" + usernames.getFirst() + "' AND user2 = '" + usernames.getSecond() + "'";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while ((resultSet.next())) {
                Timestamp ts = resultSet.getTimestamp("data");
                LocalDateTime data = LocalDateTime.ofInstant(ts.toInstant(), ZoneOffset.ofHours(0));
                friendship = new Friendship(usernames.getFirst(), usernames.getSecond(), data);
            }
            return friendship;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * finds all friendships
     *
     * @return all friendships
     */
    @Override
    public Iterable<Friendship> findAll() {
        String sql="SELECT * FROM friendships";
        return findAllFriendships(sql);
    }

    /**
     * saves a new friendship between 2 users
     *
     * @param friendship the friendship we want to save
     * @return null
     */
    @Override
    public Friendship save(Friendship friendship) {
        String sql = "INSERT INTO friendships (\"user1\", \"user2\", data) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, friendship.getId().getFirst());
            statement.setString(2, friendship.getId().getSecond());
            statement.setTimestamp(3, Timestamp.valueOf(friendship.getDate()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deletes an existing friendship
     *
     * @param usernames the usernames of the friendship we want to delete
     * @return null
     */
    @Override
    public Friendship delete(Tuple<String> usernames) {
        String sql = "DELETE FROM friendships WHERE \"user1\" = (?) and \"user2\" = (?)";

        return deleteQuery(usernames, sql);
    }

    /**
     * updates a friendship's date
     *
     * @param friendship the friendship we want to have after the update
     * @return null
     */
    @Override
    public Friendship update(Friendship friendship) {
        String sql = "UPDATE friendships SET data = (?) WHERE \"user1\" = (?) and \"user2\" = (?); ";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(friendship.getDate()));
            statement.setString(2, friendship.getId().getFirst());
            statement.setString(3, friendship.getId().getSecond());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deletes the friendships where one user appears
     *
     * @param username the user whose friendships we want to delete
     * @return null
     */
    public Friendship deleteOnesFriendships(String username) {
        String sql = "DELETE FROM friendships WHERE \"user1\" = (?) or \"user2\" = (?)";

        return deleteQuery(new Tuple<>(username, username), sql);
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        String sql = "SELECT * FROM friendships ORDER BY user1 LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findAllFriendships(sql).stream());
    }

    public Page<Friendship> findAllOfUser(Pageable pageable, String userName) {
        String sql = "SELECT * FROM friendships WHERE user1 LIKE'" + userName + "'OR user2 LIKE '" + userName + "' ORDER BY user1 LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findAllFriendships(sql).stream());
    }

    private Set<Friendship> findAllFriendships(String sql) {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username1 = resultSet.getString("user1");
                String username2 = resultSet.getString("user2");
                Timestamp ts = resultSet.getTimestamp("data");
                LocalDateTime data = LocalDateTime.ofInstant(ts.toInstant(), ZoneOffset.ofHours(0));
                Friendship friendship = new Friendship(username1, username2, data);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }
}