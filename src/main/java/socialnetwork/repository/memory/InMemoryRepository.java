package socialnetwork.repository.memory;

import socialnetwork.model.Entity;
import socialnetwork.model.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    /**
     * finds an entity with a certain id
     * @param id the id of the entity we want to find
     * @return the entity if it exists
     */
    @Override
    public E findOne(ID id){
        if (id == null)
            throw new IllegalArgumentException("Identifier must be not null!");
        return entities.get(id);
    }

    /**
     * finds all entities in a repository
     * @return all entities
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     * saves a new entity
     * @param entity the entity we want to save
     * @return the entity if it didn't get saved now, null if it did
     */
    @Override
    public E save(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must be not null!");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity;
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /**
     * deletes an entity with a certain id
     * @param id the id of the entity we want to delete
     * @return the entity we deleted
     */
    @Override
    public E delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Identifier must be not null!");
        E entity = entities.get(id);
        entities.remove(id);
        return entity;
    }

    /**
     * updates an entity
     * @param entity the entity we want to appear after the update, has the same id as the old one
     * @return the entity/null
     */
    @Override
    public E update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;
    }

}