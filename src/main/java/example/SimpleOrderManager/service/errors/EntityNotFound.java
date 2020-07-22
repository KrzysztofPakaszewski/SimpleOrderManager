package example.SimpleOrderManager.service.errors;

public class EntityNotFound extends RuntimeException{

    public EntityNotFound(String entity, Long id){
        super("Entity " + entity + " with id: " + id + " not found");
    }
}
