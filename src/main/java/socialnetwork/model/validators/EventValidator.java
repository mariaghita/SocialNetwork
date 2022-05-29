package socialnetwork.model.validators;

import socialnetwork.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventValidator implements Validator<Event> {
    @Override
    public void validate(Event entity) throws ValidationException {
        String errors = "";
        if(entity.getName().isEmpty())
            errors += "Event has to have a name!\n";
        if(entity.getName().length() > 100)
            errors += "Event name too long!\n";
        if(entity.getDate() == null)
            errors += "Event has to have a date!\n";
        if(entity.getDate().plusDays(1).isBefore(LocalDateTime.now()))
            errors += "The Event is already over!\n";
        if(errors.length() > 0)
            throw new ValidationException(errors);
    }
}
