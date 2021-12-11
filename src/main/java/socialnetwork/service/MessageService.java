package socialnetwork.service;

import socialnetwork.model.Message;
import socialnetwork.model.MessageDTO;
import socialnetwork.model.User;
import socialnetwork.model.validators.MessageValidator;
import socialnetwork.model.validators.ValidationException;
import socialnetwork.model.validators.Validator;
import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.MessageDBRepository;
import socialnetwork.repository.db.UserDBRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MessageService {
    private final UserDBRepository userDBRepository;
    private final FriendshipDBRepository friendshipDBRepository;
    private final FriendRequestDBRepository friendRequestDBRepository;
    private final MessageDBRepository messageDBRepository;

    /**
     * constructor
     * @param userDBRepository - user repository
     * @param friendshipDBRepository - friendship repository
     * @param friendRequestDBRepository - friend request repository
     * @param messageDBRepository - message repository
     */
    public MessageService(UserDBRepository userDBRepository, FriendshipDBRepository friendshipDBRepository, FriendRequestDBRepository friendRequestDBRepository, MessageDBRepository messageDBRepository) {
        this.userDBRepository = userDBRepository;
        this.friendshipDBRepository = friendshipDBRepository;
        this.friendRequestDBRepository = friendRequestDBRepository;
        this.messageDBRepository = messageDBRepository;
    }

    /**
     * function to send a message or to reply a message, depending on the id of the original
     * @param senderUsername - username of the sender of the message
     * @param recieversUsername - list of usernames of the receivers of the message
     * @param text - the text of the message
     * @param idOfMessage - the id of the new message
     * @param idOfOriginal - id of the message the user wants to reply to. if null -> new message
     */
    public void sendMessage(String senderUsername, List<String> recieversUsername, String text, Long idOfMessage, Long idOfOriginal){

        if(messageDBRepository.findOne(idOfMessage)!=null)
            throw new ValidationException("Id of message already in use.");

        for(String username : recieversUsername)
            if(userDBRepository.findOne(username) == null)
                throw new ValidationException("One of the users does not exists.");




        LocalDateTime date = LocalDateTime.now();
        Message message = new Message(senderUsername,recieversUsername,null,text);
        message.setId(idOfMessage);
        message.setDate(date);

        Validator<Message> validator = new MessageValidator();
        validator.validate(message);
        if(idOfOriginal != null)
            if(messageDBRepository.findOne(idOfOriginal) == null)
                throw new ValidationException("The message you want to reply to does not exist!");
            else {
                if(!receivedMessage(idOfOriginal, recieversUsername)) //verifies if the message the user is replying to have been received
                    throw new ValidationException("You cannot send a message to a user who has not received it!");
                message.setOrginalMessage(idOfOriginal);
            }
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
     * verifies if a message has been received by the users is being sent to
     * @param idMessage
     * @return true if all the users have received the message/ false if not
     */
    private boolean receivedMessage(Long idMessage, List<String> toUser ){
        Message message = messageDBRepository.findOne(idMessage);
        for(String username: toUser)
            if(!message.getTo().contains(username))
                return false;

        return true;
    }


}
