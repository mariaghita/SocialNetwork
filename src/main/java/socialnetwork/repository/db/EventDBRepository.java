package socialnetwork.repository.db;

import socialnetwork.model.Event;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

public class EventDBRepository extends AbstractDBRepository<Long, Event> {
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
                int duration = resultSet.getInt("duration");
                String location = resultSet.getString("location");

                event = new Event(name, date, host, duration, location);
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
        Set<Event> events = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Timestamp result_date = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(result_date.toInstant(), ZoneOffset.ofHours(0));
                String host = resultSet.getString("host");
                int duration = resultSet.getInt("duration");
                String location = resultSet.getString("location");

                Event event = new Event(name, date, host, duration, location);
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
    public Event save(Event entity) {
        return null;
    }

    @Override
    public Event delete(Long aLong) {
        return null;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }
}
