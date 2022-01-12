package socialnetwork.service;

import socialnetwork.model.Group;
import socialnetwork.model.Message;
import socialnetwork.model.MessageDTO;
import socialnetwork.model.User;
import socialnetwork.model.validators.GroupValidator;
import socialnetwork.model.validators.MessageValidator;
import socialnetwork.model.validators.ValidationException;
import socialnetwork.model.validators.Validator;
import socialnetwork.repository.db.*;
import socialnetwork.utils.events.MessageEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;

public class MessageService implements Observable<MessageEvent> {
    private final UserDBRepository userDBRepository;
    private final FriendshipDBRepository friendshipDBRepository;
    private final FriendRequestDBRepository friendRequestDBRepository;
    private final MessageDBRepository messageDBRepository;
    //private final GroupDBRepository groupDBRepository = new GroupDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
    private final GroupDBRepository groupDBRepository;
    /**
     * constructor
     * @param userDBRepository - user repository
     * @param friendshipDBRepository - friendship repository
     * @param friendRequestDBRepository - friend request repository
     * @param messageDBRepository - message repository
     */
    public MessageService(UserDBRepository userDBRepository, FriendshipDBRepository friendshipDBRepository, FriendRequestDBRepository friendRequestDBRepository, MessageDBRepository messageDBRepository, GroupDBRepository groupDBRepository) {
        this.userDBRepository = userDBRepository;
        this.friendshipDBRepository = friendshipDBRepository;
        this.friendRequestDBRepository = friendRequestDBRepository;
        this.messageDBRepository = messageDBRepository;
        this.groupDBRepository = groupDBRepository;
    }

    /**
     * function to send a message or to reply a message, depending on the id of the original
     * @param message - message to send
     */
    public void sendMessage(Message message){

        List<String> recieversUsername = message.getTo();
        Long idOfOriginal = message.getOrginalMessage();

        for(String username : recieversUsername)
            if(userDBRepository.findOne(username) == null)
                throw new ValidationException("One of the users does not exists.");

        if(message.getTo().contains(message.getFrom()))
            throw new ValidationException("You cannot send a message to yourself!");
        LocalDateTime date = LocalDateTime.now();
        message.setDate(date);

        Validator<Message> validator = new MessageValidator();
        validator.validate(message);

        if(idOfOriginal != null)
            message.setOrginalMessage(idOfOriginal);


        messageDBRepository.save(message);
    }

    /**
     * gets the conversation between 2 users sorted by date
     * @param firstUser - one of the users
     * @param secondUser - the other user
     * @return - list of messages sorted by date between the 2 users
     */
    public List<MessageDTO> getConversationWithAUser(String firstUser, String secondUser){
        Iterable<Message> messages = messageDBRepository.findAll();
        List<Message> result = new ArrayList<Message>();

        if(userDBRepository.findOne(secondUser) == null)
            throw new ValidationException("The user provided does not exist!");

        for(Message message : messages){
            if((message.getFrom().equals(firstUser) && message.getTo().contains(secondUser)) || (message.getFrom().equals(secondUser) && message.getTo().contains(firstUser))){
                if(message.getTo().size()==1)
                    result.add(message);
            }

        }
        result.sort(Comparator.comparing(Message::getDate));
        return convertMessages(result);
    }

    /**
     * converts a list of messages to messageDTO
     * @param list - the list to convert
     * @return - list of messageDTO
     */
    private List<MessageDTO> convertMessages(List<Message> list){
        List<MessageDTO> result = new ArrayList<>();
        list.forEach(x->{
            List<User> toUsers = new ArrayList<>();
            x.getTo().forEach(y->{
                toUsers.add(userDBRepository.findOne(y));
            });
            if (messageDBRepository.findOne(x.getOrginalMessage()) == null){
                MessageDTO messageDTO = new MessageDTO(userDBRepository.findOne(x.getFrom()), toUsers, x.getDate(), null, x.getText());
                messageDTO.setId(x.getId());
                result.add(messageDTO);
            }
            else{
                MessageDTO messageDTO = new MessageDTO(userDBRepository.findOne(x.getFrom()), toUsers, x.getDate(), result.get(result.size()-1), x.getText());
                messageDTO.setId(x.getId());
                result.add(messageDTO);
            }
        });
        return result;
    }


    /**
     * finds all messages containing a specific string received by a user
     * @param partOfMessage - the string used to search for messages
     * @param username - the username of the user who received the messages containing partOfMessage
     * @return list of messageDTOs with all the messages found
     */
    public List<MessageDTO> findMessages(String partOfMessage, String username){
        Iterable<Message> messages = messageDBRepository.findAll();
        List<Message> result = new ArrayList<Message>();
        for(Message message : messages){
            if((message.getTo().size() == 1) && message.getText().contains(partOfMessage) && (message.getTo().contains(username)))
                result.add(message);
        }
        result.sort(Comparator.comparing(Message::getDate));
        return convertMessagesWithoutOriginal(result);
    }

    /**
     * finds all messages that have more than 1 recipients  - used for replyAll function
     * @param username - the username of the user who is seeing the messages
     * @return list of messages which fulfill the criteria
     */
    public List<MessageDTO> findGroupMessages(String username, String pieceOfMessage){
        List<Group> groups = getGroupsOfAUser(username);
        //todo
        return null;
    }

    /**
     * function to convert messages to messageDTO without getting the original message - used for reply functions
     * @param list - list of messages to be converted
     * @return list of objects messageDTO
     */
    private List<MessageDTO> convertMessagesWithoutOriginal(List<Message> list){
        List<MessageDTO> result = new ArrayList<>();
        list.forEach(x-> {
            List<User> toUsers = new ArrayList<>();
            x.getTo().forEach(y -> {
                toUsers.add(userDBRepository.findOne(y));
            });
            MessageDTO messageDTO = new MessageDTO(userDBRepository.findOne(x.getFrom()), toUsers, x.getDate(), null, x.getText());
            messageDTO.setId(x.getId());
            result.add(messageDTO);
        });
        return result;

    }


    public void sendAGroupMessage(Message message) throws NumberFormatException{
        List<String> to = message.getTo();
        if(to.size() > 1)
            throw new ValidationException("Please provide a single group!.."); // de modificat in viitorul apropiat vbreau doar sa vad adaca merge

        Long group = Long.parseLong(to.get(0)); // hehe merge!!


        Group foundGroup = groupDBRepository.findOne(group);
        LocalDateTime date = LocalDateTime.now();
        message.setDate(date);
        foundGroup.getMessages().add(message);
        groupDBRepository.update(foundGroup);
    }

    public List<MessageDTO> getGroupConversation(Long groupID){
        Group group = groupDBRepository.findOne(groupID);
        if(group == null)
            throw new ValidationException("The group does not exist");

        return convertMessages(group.getMessages());
    }

    public List<Group> getGroupsOfAUser(String username){
        Iterable<Group> groups = groupDBRepository.findAll();
        List<Group> result = new ArrayList<Group>();
        for(Group group: groups){
            if(group.getUsers().contains(username))
                result.add(group);
        }
        return result;
    }

    public void createAGroup(Group group){
        Validator<Group> groupValidator = new GroupValidator();
        groupValidator.validate(group);
        for(String user : group.getUsers())
            if(userDBRepository.findOne(user) == null)
                throw new ValidationException("one of the users does not exist");
        groupDBRepository.save(group);
    }

    private List<Observer<MessageEvent>> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer<MessageEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
