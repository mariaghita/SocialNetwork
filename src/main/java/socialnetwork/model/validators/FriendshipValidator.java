package socialnetwork.model.validators;

import socialnetwork.model.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = "";
        if(entity.getId().getSecond().equals(entity.getId().getFirst()))
            errors += "The friendship is not created between 2 distinct users!\n";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }
}
