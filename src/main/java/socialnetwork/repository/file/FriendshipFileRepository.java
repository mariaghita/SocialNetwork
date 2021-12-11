package socialnetwork.repository.file;

import socialnetwork.model.Friendship;
import socialnetwork.model.Tuple;
import socialnetwork.model.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFileRepository extends AbstractFileRepository<Tuple<String>,Friendship> {
    public FriendshipFileRepository(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    /**
     * creates a friendship out of its attributes
     * @param attributes friendship's fields' values
     * @return a friendship with the attributes given
     */
    @Override
    public Friendship extractEntity(List<String> attributes) {
        return new Friendship(attributes.get(0), attributes.get(1), LocalDateTime.parse(attributes.get(2)));
    }

    /**
     * creates string out of a friendship, to write it in file
     * @param entity the friendship
     * @return friendship as string
     */
    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getFirst() + ";" + entity.getId().getSecond() + ";" + entity.getDate();
    }
}
