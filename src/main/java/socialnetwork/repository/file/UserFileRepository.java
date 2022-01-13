package socialnetwork.repository.file;

import socialnetwork.model.User;
import socialnetwork.model.validators.Validator;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<String,User>{

    public UserFileRepository(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    /**
     * creates a user out of its attributes
     * @param attributes user's fields' values
     * @return a user with the attributes given
     */
    @Override
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1),attributes.get(2), attributes.get(3));
        user.setId(attributes.get(0));
        return user;
    }

    /**
     * creates string out of a user, to write it in file
     * @param entity the user
     * @return user as string
     */
    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
