package socialnetwork.repository.db;

import socialnetwork.model.Message;
import socialnetwork.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDBRepository extends AbstractDBRepository<Long, Message> {

    public MessageDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * finds a message by its id
     * @param aLong - id of the message requested
     * @return - the message requested
     */
    @Override
    public Message findOne(Long aLong) {
        Message message = null;
        String sql = "SELECT * FROM messages WHERE id = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String from = resultSet.getString("from");
                String text = resultSet.getString("text");
                Timestamp result_date = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(result_date.toInstant(), ZoneOffset.ofHours(0));
                Long idOfOriginalMessage = resultSet.getLong("originalMessage");


                List<String> recipients = getRecepientsOfAMessage(aLong);
                message = new Message(from,recipients,idOfOriginalMessage,text);
                message.setDate(date);
                return message;

            }
            return message;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gets the usernames of a message with a certain id
     * @param messageID - the id of the message
     * @return - list of usernames of the users which received the message with the id provided
     */
    private List<String> getRecepientsOfAMessage(Long messageID){

        List<String> recipients = new ArrayList<String>();
        String sql = "SELECT * FROM users u," +
                "message_recipient mr WHERE mr.message=? and u.username = mr.recipient";
        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, messageID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                recipients.add(username);
            }
            return recipients;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * gets all messages saved in the database
     * @return iterable of messages
     */
    @Override
    public Iterable<Message> findAll() {

        Set<Message> messageSet = new HashSet<>();
        String sql = "SELECT * FROM messages";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String from = resultSet.getString("from");
                String text = resultSet.getString("text");
                Timestamp result_date = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(result_date.toInstant(), ZoneOffset.ofHours(0));
                Long idOfOriginalMessage = resultSet.getLong("originalMessage");

                List<String> recipients = getRecepientsOfAMessage(id);
                Message message = new Message(from,recipients,idOfOriginalMessage,text);
                message.setDate(date);
                message.setId(id);
                message.setOrginalMessage(idOfOriginalMessage);
                messageSet.add(message);


            }
            return messageSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageSet;
    }

    /**
     * saves a new message and all the recipients of the message
     * @param entity - the messages to be saved
     * @return null
     */
    @Override
    public Message save(Message entity) {
        String sql = "INSERT INTO messages (date, \"from\", \"originalMessage\", text ) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setString(2, entity.getFrom());
            if(entity.getOrginalMessage() == null)
                statement.setNull(3, Types.NULL);
            else
                statement.setLong(3, entity.getOrginalMessage());
            statement.setString(4,entity.getText());
            //statement.setLong(5,entity.getId());
            statement.executeUpdate();
            for(String user: entity.getTo())
                saveOneRecipient(getNewestIdInserted(), user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * function to get the id of the last inserted message
     * @return the id of the last message - type long
     */
    private Long getNewestIdInserted(){
        String sql = "select id from messages order by id desc limit 1";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            Long id = resultSet.getLong("id");
            return id;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    /**
     * saves one recipient of a message with a certain username
     * @param messageID - the id of the message which is sent to the user
     * @param user - username of the user which receives the message
     * @return - null
     */
    private Message saveOneRecipient(Long messageID, String user){
        String sql = "INSERT INTO message_recipient(message, recipient) VALUES (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, messageID);
            statement.setString(2, user);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
