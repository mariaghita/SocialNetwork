package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.model.EventDTO;
import socialnetwork.repository.db.EventDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.EventService;
import socialnetwork.utils.events.EventEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MyEventsController extends UserController implements Observer<EventEvent> {
    EventService eventService;
    ObservableList<EventDTO> model = FXCollections.observableArrayList();

    public MyEventsController() {
        super();
    }


    @FXML
    TableView<EventDTO> tableViewEvents;

    @FXML
    TableColumn<EventDTO, String> tableColumnName;

    @FXML
    TableColumn<EventDTO, String> tableColumnDate;

    @FXML
    TableColumn<EventDTO, String> tableColumnDays;

    @FXML
    protected Pagination paginationEvents;

    @FXML
    protected Label status;

    public void setServices() {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        EventDBRepository eventDBRepository = new EventDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.eventService = new EventService(userDBRepository, eventDBRepository);

        eventService.addObserver(this);
        paginationEvents.setPageFactory(this::createPage);
    }

    @FXML
    public void initialize() {
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("beginDate"));
        tableColumnDays.setCellValueFactory(new PropertyValueFactory<>("description"));
        paginationEvents.setPageFactory(this::createPage);
        tableViewEvents.setItems(model);
    }

    private Node createPage(Integer page) {
        int fromIndex = page * 5;
        int toIndex = Math.min(fromIndex + 5, model.size());
        tableViewEvents.setItems(FXCollections.observableArrayList(initModel(page)));
        return tableViewEvents;
    }

    private List<EventDTO> initModel(int page) {
        List<EventDTO> EventDTOS = eventService.getOnesEventsOnPage(page, currentUsername);
        model.setAll(EventDTOS);
        return EventDTOS;
    }

    @Override
    public void update(EventEvent eventEvent) {
        paginationEvents.setPageFactory(this::createPage);
    }

    public void eventUnsubscribe(ActionEvent event) {
        String message = "";
        EventDTO selected = tableViewEvents.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                eventService.unsubscribeFromEvent(selected.getId(), currentUsername);
                message = "Successfully unsubscribed from " + selected.getName() + "!";
            } catch (Exception e) {
                message = "You were not subscribed to this event!";
            }
        } else
            message = "You didn't select any event that you want to unsubscribe from!";
        status.setText(message);
    }

    public void eventOff(ActionEvent event) {
        String message = "";
        EventDTO selected = tableViewEvents.getSelectionModel().getSelectedItem();
        if(selected != null) {
            int len = StreamSupport.stream(eventService.getEventsWithOn(currentUsername).spliterator(), false)
                    .filter(x -> x.equals(selected))
                    .collect(Collectors.toList()).size();
            if(len != 0) {
                EventDTO e = StreamSupport.stream(eventService.getEventsWithOn(currentUsername).spliterator(), false)
                        .filter(x -> x.equals(selected))
                        .collect(Collectors.toList()).get(0);
                eventService.addOffNotification(e.getId(), currentUsername);
                message = "Successfully turned off notification for " + e.getName() + "!";
            } else {
                message = "Your notifications for this event were already turned off!";
            }
        } else
            message = "You didn't select any event that you want to turn off notifications for!";
        status.setText(message);
    }

    public void eventOn(ActionEvent event) {
        String message = "";
        EventDTO selected = tableViewEvents.getSelectionModel().getSelectedItem();
        if(selected != null) {
            int len = StreamSupport.stream(eventService.getEventsWithOff(currentUsername).spliterator(), false)
                    .filter(x -> x.equals(selected))
                    .collect(Collectors.toList()).size();
            if(len != 0) {
                EventDTO e = StreamSupport.stream(eventService.getEventsWithOff(currentUsername).spliterator(), false)
                        .filter(x -> x.equals(selected))
                        .collect(Collectors.toList()).get(0);
                eventService.addOnNotification(e.getId(), currentUsername);
                message = "Successfully turned on notification for " + selected.getName() + "!";
            } else {
                message = "Your notifications for this event were already turned on!";
            }
        } else
            message = "You didn't select any event that you want to turn on notifications for!";
        status.setText(message);
    }
}
