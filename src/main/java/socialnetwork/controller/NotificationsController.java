package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import socialnetwork.model.EventDTO;
import socialnetwork.repository.db.EventDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.EventService;
import socialnetwork.utils.events.EventEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationsController extends UserController implements Observer<EventEvent> {
    private EventService eventService;
    ObservableList<EventDTO> model = FXCollections.observableArrayList();

    public NotificationsController() {
        super();
    }

    @FXML
    private ListView<String> listViewNotifications;

    @FXML
    private Pagination notificationPagination;

    @FXML
    private Label status;

    @FXML
    public void initialize() {
        listViewNotifications.getItems().addAll();
        notificationPagination.setPageFactory(this::createPage);
    }

    private Node createPage(int page) {
        int fromIndex = page * 5;
        int toIndex = Math.min(fromIndex + 5, model.size());
        listViewNotifications.setItems(FXCollections.observableArrayList(initModel(page)));
        return listViewNotifications;
    }

    public void setServices() {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        EventDBRepository eventDBRepository = new EventDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.eventService = new EventService(userDBRepository, eventDBRepository);

        eventService.addObserver(this);
        //initModel();
    }

    private List<String> initModel(Integer page) {
        return StreamSupport.stream(eventService.getNotificationsOnPage(page, currentUsername).spliterator(), false)
                .map(e -> e.getInformation())
                .collect(Collectors.toList());
    }

    public void eventOff(ActionEvent event) {
        String message = "";
        String selected = listViewNotifications.getSelectionModel().getSelectedItem();
        if(selected != null) {
            int len = StreamSupport.stream(eventService.getEventsWithOn(currentUsername).spliterator(), false)
                    .filter(x -> x.getInformation().equals(selected))
                    .collect(Collectors.toList()).size();
            if(len != 0) {
                EventDTO e = StreamSupport.stream(eventService.getEventsWithOn(currentUsername).spliterator(), false)
                        .filter(x -> x.getInformation().equals(selected))
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

    @Override
    public void update(EventEvent eventEvent) {
        notificationPagination.setPageFactory(this::createPage);
    }
}
