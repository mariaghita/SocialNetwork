package socialnetwork.repository.db;

import socialnetwork.model.Event;
import socialnetwork.model.EventNotification;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

public class EventDBRepository extends AbstractPageDBRepository<Long, Event> {
    public EventDBRepository (String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Event findOne(Long eventid) {
        Event event = null;
        String sql = "SELECT * FROM events WHERE id = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, eventid);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                Timestamp result_date = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(result_date.toInstant(), ZoneOffset.ofHours(0));
                String host = resultSet.getString("host");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");

                event = new Event(name, date, host, description, location);
                event.setId(eventid);
            }
            return event;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        String sql = "SELECT * FROM events";

        return findMore(sql);
    }

    @Override
    public Event save(Event event) {
        String sql = "INSERT INTO events (name, date, host, description, location) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getName());
            statement.setTimestamp(2, Timestamp.valueOf(event.getDate()));
            statement.setString(3, event.getHost());
            statement.setString(4, event.getDescription());
            statement.setString(5, event.getLocation());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event delete(Long eventid) {
        String sql = "DELETE FROM events WHERE id = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, eventid);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event update(Event event) {
        return null;
    }

    public Long findOnesId(Event event) {
        Long id = null;
        String sql = "SELECT id FROM events WHERE name = (?) and date = (?) and host = (?) and  description = (?) and location = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, event.getName());
            statement.setTimestamp(2, Timestamp.valueOf(event.getDate()));
            statement.setString(3, event.getHost());
            statement.setString(4, event.getDescription());
            statement.setString(5, event.getLocation());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getLong("id");
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EventNotification findEventNotification(Long eventid, String userName){
        EventNotification notification = null;
        String sql = "SELECT * FROM event_participation WHERE event = (?) and username = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, eventid);
            statement.setString(2, userName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String n = resultSet.getString("notifications");
                notification = new EventNotification(eventid, userName, n);
            }
            return notification;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Iterable<EventNotification> findUsersEvents(String userName){
        Set<EventNotification> notifications = new HashSet<>();
        String sql = "SELECT * FROM event_participation WHERE username = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String n = resultSet.getString("notifications");
                Long eventid = resultSet.getLong("event");
                EventNotification notification = new EventNotification(eventid, userName, n);
                notifications.add(notification);
            }
            return notifications;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EventNotification saveEventNotification(Long eventid, String userName){
        String sql = "INSERT INTO event_participation (event, username, notifications) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String not = "on";
            statement.setLong(1, eventid);
            statement.setString(2, userName);
            statement.setString(3, not);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EventNotification deleteEventNotification(Long eventid, String userName){
        String sql = "DELETE FROM event_participation WHERE event = (?) and username = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, eventid);
            statement.setString(2, userName);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EventNotification updateNotification(Long eventid, String userName, String status) {
        String sql = "UPDATE event_participation SET notifications = (?)  WHERE username = (?) and event = (?); ";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setString(2, userName);
            statement.setLong(3, eventid);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<Event> findMore(String sql) {
        Set<Event> events = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Timestamp result_date = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(result_date.toInstant(), ZoneOffset.ofHours(0));
                String host = resultSet.getString("host");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");

                Event event = new Event(name, date, host, description, location);
                event.setId(id);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        String sql = "SELECT * FROM events ORDER BY id LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findMore(sql).stream());
    }

    public Page<Event> findAllAvailable(Pageable pageable) {
        String sql = "SELECT * FROM events WHERE e.date>now() ORDER BY id LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findMore(sql).stream());
    }


    public Page<Event> findAllNotifications(Pageable pageable, String userName) {
        String sql = "SELECT * FROM events e inner join event_participation ep on e.id = ep.event where ep.username like '" + userName + "' and e.date>now() and ep.notifications like 'on' ORDER BY e.date LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findMore(sql).stream());
    }

    public Page<Event> findOnesSubscriptions(Pageable pageable, String userName) {
        String sql = "SELECT * FROM events e inner join event_participation ep on e.id = ep.event where ep.username like '" + userName + "' ORDER BY e.date LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        return new PageImplementation<>(pageable, findMore(sql).stream());
    }
}
