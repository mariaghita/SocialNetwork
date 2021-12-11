package socialnetwork.model.validators;

import socialnetwork.model.FriendRequest;

public class FriendRequestValidator implements Validator<FriendRequest> {
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        String errors = "";
        if(entity.getId().getSecond().equals(entity.getId().getFirst()))
            errors += "The friend request is not created between 2 distinct users!\n";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }
}
