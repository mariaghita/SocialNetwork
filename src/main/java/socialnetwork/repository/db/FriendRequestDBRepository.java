package socialnetwork.repository.db;

import socialnetwork.model.FriendRequest;
import socialnetwork.model.Tuple;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

public class FriendRequestDBRepository extends AbstractFriendDBRepository<Tuple<String>, FriendRequest> {
    public FriendRequestDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     *finds a friend request from a user to another
     * @param usernames the usernames
     * @return the friend request if found, null otherwise
     */
    @Override
    public FriendRequest findOne(Tuple<String> usernames) {
        FriendRequest friendRequest = null;
        String sql = "SELECT * FROM friendrequests WHERE \"from\" = '" + usernames.getFirst() + "' AND \"to\" = '" + usernames.getSecond() + "'";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();

            while ((resultSet.next())) {
                friendRequest = new FriendRequest(usernames.getFirst(), usernames.getSecond());
            }
            return friendRequest;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *finds all friend requests from a user
     * @param userName from
     * @return the friend requests if found, null otherwise
     */
    public Iterable<FriendRequest> findFrom(String userName) {
        String sql = "SELECT * FROM friendrequests WHERE \"from\" = '" + userName + "'";

        return findMore(sql);
    }

    /**
     *finds all friend requests to a user
     * @param userName to
     * @return the friend requests if found, null otherwise
     */
    public Iterable<FriendRequest> findTo(String userName) {
        String sql = "SELECT * FROM friendrequests WHERE \"to\" = '" + userName + "'";

        return findMore(sql);
    }

    /**
     *finds all friend requests
     * @return all friend requests
     */
    @Override
    public Iterable<FriendRequest> findAll() {
        String sql = "SELECT * FROM friendrequests";

        return findMore(sql);
    }

    /**
     *saves a new friend request from a user to another
     * @param friendRequest the friend request we want to save
     * @return null
     */
    @Override
    public FriendRequest save(FriendRequest friendRequest) {
        String sql = "INSERT INTO friendrequests (\"from\", \"to\", date ) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, friendRequest.getId().getFirst());
            statement.setString(2, friendRequest.getId().getSecond());
            statement.setTimestamp(3, Timestamp.valueOf(friendRequest.getLocalDateTime()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *deletes an existing friend request
     * @param usernames the usernames of the friend request we want to delete
     * @return null
     */
    @Override
    public FriendRequest delete(Tuple<String> usernames) {
        String sql = "DELETE FROM friendrequests WHERE \"from\" = (?) and \"to\" = (?)";

        return deleteQuery(usernames, sql);
    }

    @Override
    public FriendRequest update(FriendRequest friendRequest) {
        return null;
    }

    /**
     *deletes a friend request from or to a certain user
     * @param username the username for which the action is executed
     * @return null
     */
    public FriendRequest deleteOnesFriendRequests(String username){
        String sql = "DELETE FROM friendrequests WHERE \"from\" = (?) or \"to\" = (?)";

        return deleteQuery(new Tuple<>(username, username), sql);
    }

    /**
     *
     * @param query the query we want to execute
     * @return the friend requests if they exist, null otherwise
     */
    private Iterable<FriendRequest> findMore(String query){
        Set<FriendRequest> friendRequests = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, this.username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String from = resultSet.getString("from");
                String to = resultSet.getString("to");
                Timestamp result_date = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(result_date.toInstant(), ZoneOffset.ofHours(0));
                FriendRequest friendRequest = new FriendRequest(from, to);
                friendRequest.setLocalDateTime(date);
                friendRequests.add(friendRequest);
            }
            return friendRequests;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
}
