package socialnetwork.service;

import socialnetwork.model.Event;
import socialnetwork.model.EventDTO;
import socialnetwork.model.EventNotification;
import socialnetwork.model.validators.EventValidator;
import socialnetwork.model.validators.ValidationException;
import socialnetwork.model.validators.Validator;
import socialnetwork.repository.db.EventDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.*;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventService implements Observable<EventEvent> {
    private final UserDBRepository userDBRepository;
    private final EventDBRepository eventDBRepository;

    private int page = 0;
    private int size = 5;

    public EventService(UserDBRepository userDBRepository, EventDBRepository eventDBRepository) {
        this.userDBRepository = userDBRepository;
        this.eventDBRepository = eventDBRepository;
    }

    public void addEvent(Event event) {
        Validator<Event> validator = new EventValidator();
        validator.validate(event);

        eventDBRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventDBRepository.delete(id);
    }

    public void subscribeToEvent(Long id, String username) {
        validateEN(id, username);
        if(eventDBRepository.findEventNotification(id, username) != null)
            throw new ValidationException("The user is already subscribed to this event!\n");

        eventDBRepository.saveEventNotification(id, username);
        notifyObservers(new EventEvent(EventEventType.SUBSCRIBED, eventDBRepository.findEventNotification(id, username)));
    }

    public void unsubscribeFromEvent(Long id, String username) {
        validateEN(id, username);
        if(eventDBRepository.findEventNotification(id, username) == null)
            throw new ValidationException("The user is not subscribed to this event anyway!\n");

        EventNotification EN = eventDBRepository.findEventNotification(id, username);

        eventDBRepository.deleteEventNotification(id, username);
        notifyObservers(new EventEvent(EventEventType.UNSUBSCRIBED, EN));
    }

    public void addEventFromUser(Event event, String username) {
        addEvent(event);
        subscribeToEvent(eventDBRepository.findOnesId(event), username);
    }

    public void deleteEventFromUser(Long id, String username) {
        unsubscribeFromEvent(id, username);
        deleteEvent(id);
    }

    public void addOnNotification(Long id, String username) {
        validateEN(id, username);
        if(eventDBRepository.findEventNotification(id, username) == null)
            throw new ValidationException("The user is not subscribed to this event!\n");
        if(eventDBRepository.findEventNotification(id, username).getStatus().equals("on"))
            throw new ValidationException("The user already received notifications from this event!\n");

        String not = "on";

        eventDBRepository.updateNotification(id, username, not);
        notifyObservers(new EventEvent(EventEventType.NOTIFICATIONSON, eventDBRepository.findEventNotification(id, username)));
    }

    public void addOffNotification(Long id, String username) {
        validateEN(id, username);
        if(eventDBRepository.findEventNotification(id, username) == null)
            throw new ValidationException("The user is not subscribed to this event!\n");
        if(eventDBRepository.findEventNotification(id, username).getStatus().equals("off"))
            throw new ValidationException("The user already doesn't receive notifications from this event!\n");

        String not = "off";

        eventDBRepository.updateNotification(id, username, not);
        notifyObservers(new EventEvent(EventEventType.NOTIFICATIONSOFF, eventDBRepository.findEventNotification(id, username)));
    }

    public Iterable<EventDTO> getEventsWithOn(String username) {
        if(username == null)
            throw new ValidationException("Invalid username!\n");

        if(userDBRepository.findOne(username) == null)
            throw new ValidationException("A user with this username doesn't exist!\n");

        String on = "on";
        Iterable<EventNotification> en = StreamSupport.stream(eventDBRepository.findUsersEvents(username).spliterator(), false)
                .filter(e -> Objects.equals(e.getStatus(), on))
                .collect(Collectors.toList());

        List<EventDTO> result = new ArrayList<EventDTO>();
        en.forEach(x -> {
            Event event = eventDBRepository.findOne(x.getEventid());
            EventDTO eventDTO = new EventDTO(event);
            result.add(eventDTO);
        });

        return StreamSupport.stream(result.spliterator(), false)
                .sorted(Comparator.comparingLong(EventDTO::getNr_days_remaining))
                .filter(e -> e.getNr_days_remaining() >= 0)
                .collect(Collectors.toList());
    }

    public Iterable<EventDTO> getEventsWithOff(String username) {
        if(username == null)
            throw new ValidationException("Invalid username!\n");

        if(userDBRepository.findOne(username) == null)
            throw new ValidationException("A user with this username doesn't exist!\n");

        String off = "off";
        Iterable<EventNotification> en = StreamSupport.stream(eventDBRepository.findUsersEvents(username).spliterator(), false)
                .filter(e -> Objects.equals(e.getStatus(), off))
                .collect(Collectors.toList());

        List<EventDTO> result = new ArrayList<EventDTO>();
        en.forEach(x -> {
            Event event = eventDBRepository.findOne(x.getEventid());
            EventDTO eventDTO = new EventDTO(event);
            result.add(eventDTO);
        });

        return StreamSupport.stream(result.spliterator(), false)
                .sorted(Comparator.comparingLong(EventDTO::getNr_days_remaining))
                .filter(e -> e.getNr_days_remaining() >= 0)
                .collect(Collectors.toList());
    }

    private void validateEN(Long id, String username) {
        if(username == null)
            throw new ValidationException("Invalid username!\n");

        if(userDBRepository.findOne(username) == null)
            throw new ValidationException("A user with this username doesn't exist!\n");

        if(id == null)
            throw new ValidationException("Invalid event id!\n");

        if(eventDBRepository.findOne(id) == null)
            throw new ValidationException("An event with this id doesn't exist!\n");
    }

    public Long findOnesId(Event event){
        return eventDBRepository.findOnesId(event);
    }

    public Event getOne(Long id) {
        return eventDBRepository.findOne(id);
    }

    public Iterable<Event>getAll() {
        return eventDBRepository.findAll();
    }

    public List<EventDTO>getAllDTO() {
        return StreamSupport.stream(eventDBRepository.findAll().spliterator(), false)
                .map(e -> new EventDTO(e))
                .collect(Collectors.toList());
    }

    private List<Observer<EventEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<EventEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EventEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EventEvent e) {
        observers.forEach(x -> x.update(e));
    }

    public int getPage() {
        return page;
    }

    public List<EventDTO> getEventsOnPage(int page) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Event> eventPage = eventDBRepository.findAll(pageable);
        return eventPage.getContent()
                .map(e -> new EventDTO(e))
                .collect(Collectors.toList());
    }

    public List<EventDTO> getNextPageEvents() {
        this.page++;
        return getEventsOnPage(this.page);
    }

    public List<EventDTO> getNotificationsOnPage(int page, String username) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Event> eventPage = eventDBRepository.findAllNotifications(pageable, username);
        return eventPage.getContent()
                .map(e -> new EventDTO(e))
                .collect(Collectors.toList());
    }

    public List<EventDTO> getNextPageNotifications(String username) {
        this.page++;
        return getNotificationsOnPage(this.page, username);
    }

    public List<EventDTO> getOnesEventsOnPage(int page, String username) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Event> eventPage = eventDBRepository.findOnesSubscriptions(pageable, username);
        return eventPage.getContent()
                .map(e -> new EventDTO(e))
                .collect(Collectors.toList());
    }
}
