package socialnetwork.repository.file;

import socialnetwork.model.Entity;
import socialnetwork.model.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;

    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * loads data from file in the repository
     */
    private void loadData() {
        Path path = Paths.get(fileName);
        try{
            List<String> lines = Files.readAllLines(path);
            lines.forEach(line ->{
                E entity = extractEntity(Arrays.asList(line.split(";")));
                super.save(entity);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract E extractEntity(List<String> attributes);

    protected abstract String createEntityAsString(E entity);

    /**
     * saves an entity and adds it in file
     * @param entity what we want to save
     * @return the entity that we saved
     */
    @Override
    public E save(E entity){
        E result = super.save(entity);
        if(result == null)
            writeToFile(entity);
        return result;
    }

    /**
     * deletes an entity and updates file with the existing entities
     * @param id the id of the entity we want to delete
     * @return the entity we deleted
     */
    @Override
    public E delete(ID id){
        E result = super.delete(id);
        if(result != null){
            clearFile();
            for(E entity : super.findAll())
                writeToFile(entity);
        }
        return result;
    }

    /**
     * deletes the content from a file
     */
    protected void clearFile(){
        try(PrintWriter printWriter = new PrintWriter(fileName)) {
            printWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * adds an entity in our file
     * @param entity the entity we want to add
     */
    protected void writeToFile(E entity){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(createEntityAsString(entity));
            writer.newLine();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
