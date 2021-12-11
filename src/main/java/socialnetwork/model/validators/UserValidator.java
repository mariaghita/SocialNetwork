package socialnetwork.model.validators;

import socialnetwork.model.User;

import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";
        if(entity.getFirstName().length() < 3 || entity.getFirstName().length() > 15)
            errors += "The first name doesn't contain between 3 and 15 letters!\n";
        if(entity.getLastName().length() < 3 || entity.getLastName().length() > 15)
            errors += "The last name doesn't contain between 3 and 15 letters!\n";
        if(entity.getId().length() < 3 && entity.getLastName().length() > 15)
            errors += "The username doesn't contain between 3 and 15 letters!\n";
        if(!entity.getFirstName().chars().allMatch(Character::isLetter))
            errors += "The first name contains other characters than letters!\n";
        if(!entity.getLastName().chars().allMatch(Character::isLetter))
            errors += "The last name contains other characters than letters!\n";
        if(!Pattern.matches("[a-zA-z0-9._]+", entity.getId()))
            errors += "The username contains special characters that shouldn't be there!\n";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }
}
