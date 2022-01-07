package socialnetwork.model.validators;

import socialnetwork.model.Group;

public class GroupValidator implements Validator<Group> {
    @Override
    public void validate(Group entity) throws ValidationException {
        String error = "";
        if(entity.getName().equals(""))
            error+="The name must contain at least 1 character!";
        if(entity.getUsers().size() < 2)
            error+="The group must have at least 2 users!";
        if(error.length() > 1)
            throw new ValidationException(error);
    }
}
