package socialnetwork.repository.db;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import socialnetwork.model.*;

public class UserDBRepository extends AbstractDBRepository<String, User> {
    public UserDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     *finds a User with a certain username
     * @param userName the username we are searching for
     * @return the User with that username, if it exists
     */
    @Override
    public User findOne(String userName) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");

                    user = new User(firstName, lastName);
                    user.setId(userName);
                }
                return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *finds all users from the repository
     * @return all users
     */
    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");

                User user = new User(firstName, lastName);
                user.setId(username);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     *saves a new User in the repository
     * @param user the User we want to save
     * @return null
     */
    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, \"firstName\", \"lastName\" ) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getId());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *deletes a User with a certain userName
     * @param userName the username we are looking for
     * @return null
     */
    @Override
    public User delete(String userName) {
        String sql = "DELETE FROM users WHERE username = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, userName);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *updates the first and last name of an existing User
     * @param user the User we want to have in db at the end
     * @return null
     */
    @Override
    public User update(User user) {
        String sql = "UPDATE users SET \"firstName\" = (?), \"lastName\" = (?) WHERE username = (?); ";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
