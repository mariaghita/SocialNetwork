package socialnetwork.repository.db;

import socialnetwork.model.Group;
import socialnetwork.model.Message;
import socialnetwork.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class GroupDBRepository extends AbstractDBRepository<Long, Group> {

    public GroupDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Group findOne(Long aLong) {
        Group group = null;
        String sql = "select * from groups where id = (?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            group = getGroupFromResultSet(resultSet);
            resultSet.next();
            return group;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Group getGroupFromResultSet(ResultSet resultSet){
        try{
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            Group group = new Group(name);
            // TODO sa faca si mesajele din db sa le puna in lista de mesaje
            group.setMessages(getMessagesOfAGroup(id));
            group.setUsers(getUsersOfAGroup(id));
            group.setId(id);
            return group;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    private List<Message> getMessagesOfAGroup(Long groupID){
        String sql = "SELECT * FROM group_messages WHERE \"groupRecipient\" = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, groupID);
            ResultSet resultSet = statement.executeQuery();

            List<Message> messages = new ArrayList<Message>();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String from = resultSet.getString("from");
                String text = resultSet.getString("text");
                Timestamp result_date = resultSet.getTimestamp("date");
                LocalDateTime date = LocalDateTime.ofInstant(result_date.toInstant(), ZoneOffset.ofHours(0));
                Long idOfOriginal = resultSet.getLong("originalMessage");
                Message message = new Message(from, List.of(""),idOfOriginal, text);
                message.setDate(date);
                message.setId(id);
                messages.add(message);
            }
            return messages;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private List<String> getUsersOfAGroup(Long groupID){
        List<String> users = new ArrayList<String>();
        String sql = "select * from users u," +
                "group_recipient gr where gr.id_group = ? and u.username = gr.username";
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,groupID);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                String username = resultSet.getString("username");
                users.add(username);
            }
            return users;

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public Iterable<Group> findAll() {
        Set<Group> groupSet = new HashSet<>();
        String sql = "select * from groups";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Group group = getGroupFromResultSet(resultSet);
                groupSet.add(group);
            }
            return groupSet;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return groupSet;
    }

    @Override
    public Group save(Group entity) {
        String sql = "insert into groups(name) values (?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1,entity.getName());
            statement.executeUpdate();

            entity.setId(getNewestIdInserted());
            //saveLastMessage(entity);
            entity.getUsers().forEach(user -> saveOneUser(entity.getId(),user));

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private Long getNewestIdInserted(){
        String sql = "select id from groups order by id desc limit 1";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()){
            resultSet.next();
            Long id = resultSet.getLong("id");
            return id;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * at the moment of saving the list of messages of one group has only ONE message - no need for saveLastMessage function :(((((( (cred) ba da nvm
     * @param groupID
     * @param user
     * @return
     */
    private Group saveOneUser(Long groupID, String user){
        String sql = "insert into group_recipient(id_group,username) values (?,?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setLong(1, groupID);
            preparedStatement.setString(2, user);
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    private void saveLastMessage(Group entity){
        String sql = "insert into group_messages(\"from\", text, date, \"originalMessage\", \"groupRecipient\") values (?,?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            List<Message> messages = entity.getMessages();
            Message message = messages.get(messages.size()-1);

            statement.setString(1,message.getFrom());
            statement.setString(2,message.getText());
            statement.setTimestamp(3, Timestamp.valueOf(message.getDate()));
            if(message.getOrginalMessage() == null)
                statement.setNull(4,Types.NULL);
            else
                statement.setLong(4,message.getOrginalMessage());
            statement.setLong(5,entity.getId());
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    public Group delete(Long aLong) {
        return null;
    }

    @Override
    public Group update(Group entity) {

        saveLastMessage(entity);
        return null;
    }
}