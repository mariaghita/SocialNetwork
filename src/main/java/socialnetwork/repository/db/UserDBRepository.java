package socialnetwork.repository.db;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import socialnetwork.model.*;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;

public class UserDBRepository extends AbstractPageDBRepository<String, User> {
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
                    String passWord = resultSet.getString("password");

                    user = new User(firstName, lastName, passWord);
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
        String sql = "SELECT * FROM users";

        return findMore(sql);
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

    @Override
    public Page<User> findAll(Pageable pageable) {
        String sql = "SELECT * FROM users ORDER BY username LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findMore(sql).stream());
    }

    public Page<User> findAllExceptCurrent(Pageable pageable, String Username) {
        String sql = "SELECT * FROM users where username NOT LIKE '" + Username + "' ORDER BY username LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findMore(sql).stream());
    }

    public Page<User> findAllExceptCurrentWithString(Pageable pageable, String Username, String str) {
        String sql = "SELECT * FROM users WHERE username NOT LIKE '" + Username +"' AND (LOWER(CONCAT_WS(' ', \"firstName\", \"lastName\")) LIKE '%" + str.toLowerCase() + "%' OR LOWER(CONCAT_WS(' ', \"lastName\", \"firstName\")) LIKE '%" + str.toLowerCase() + "%') ORDER BY username LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findMore(sql).stream());
    }

    private Set<User> findMore(String sql) {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String passWord = resultSet.getString("password");

                User user = new User(firstName, lastName, passWord);
                user.setId(username);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;

    }
}
