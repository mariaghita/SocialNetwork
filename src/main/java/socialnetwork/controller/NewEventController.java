package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import socialnetwork.model.Event;
import socialnetwork.model.EventDTO;
import socialnetwork.repository.db.EventDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.EventService;
import socialnetwork.utils.events.EventEvent;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;

public class NewEventController extends UserController implements Observer<EventEvent> {
    private EventService eventService;
    ObservableList<EventDTO> model = FXCollections.observableArrayList();

    public NewEventController() {
        super();
    }

    @FXML
    private TextField eventName;

    @FXML
    private TextField eventLocation;

    @FXML
    private DatePicker eventDate;

    @FXML
    private TextField eventDescription;

    @FXML
    private Label wrongEvent;

    @FXML
    private Label wrongName;

    @FXML
    private Label wrongLocation;

    @FXML
    private Label wrongDate;

    public void setServices() {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        EventDBRepository eventDBRepository = new EventDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.eventService = new EventService(userDBRepository, eventDBRepository);

        eventService.addObserver(this);
    }

    public void doCreateEvent(ActionEvent event) {
        Event e = validateEvent();
            if(e != null)
                eventService.addEventFromUser(e, currentUsername);
    }

    private Event validateEvent() {
        Event event = null;

        refreshLabels();

        if(eventName.getText().isEmpty() || eventDate.getValue().atStartOfDay()== null || eventLocation.getText().isEmpty()) {
            wrongEvent.setText("Please enter all required data!");
            return null;
        }
        event = new Event(eventName.getText(), eventDate.getValue().atStartOfDay(), currentUsername, eventDescription.getText(), eventLocation.getText());
        if(eventService.findOnesId(event) != null){
            wrongEvent.setText("You're already organizing this event!");
            return null;
        }
        if(eventDate.getValue().plusDays(1).atStartOfDay().isBefore(LocalDateTime.now())) {
            wrongDate.setText("Invalid date!");
            return null;
        }
        return event;
    }

    private void refreshLabels(){
        wrongEvent.setText("");
        wrongName.setText("");
        wrongLocation.setText("");
        wrongDate.setText("");
    }

    @Override
    public void update(EventEvent eventEvent) {
        refreshLabels();
    }
}
