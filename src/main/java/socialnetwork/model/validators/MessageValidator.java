package socialnetwork.model.validators;

import socialnetwork.model.Message;
import socialnetwork.model.User;

public class MessageValidator implements Validator<Message> {
    /**
     * validates a message
     * @param entity - message to be validated
     * @throws ValidationException
     */
    @Override
    public void validate(Message entity) throws ValidationException {
        String error = "";
        if(entity.getFrom() == null)
            error += "Invalid user";
        if(entity.getText().equals(""))
            error += "The message cannot be empty";
        if(error.length() > 1)
            throw new ValidationException(error);

    }
}
