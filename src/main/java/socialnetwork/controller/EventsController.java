package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.Main;
import socialnetwork.model.EventDTO;
import socialnetwork.repository.db.EventDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.EventService;
import socialnetwork.utils.events.EventEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.List;

public class EventsController extends UserController implements Observer<EventEvent> {
    EventService eventService;
    ObservableList<EventDTO> model = FXCollections.observableArrayList();

    public EventsController() {
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
        List<EventDTO> EventDTOS = eventService.getEventsOnPage(page);
        model.setAll(EventDTOS);
        return EventDTOS;
    }

    public void setServices() {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        EventDBRepository eventDBRepository = new EventDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.eventService = new EventService(userDBRepository, eventDBRepository);

        eventService.addObserver(this);
        paginationEvents.setPageFactory(this::createPage);
        //initModel();
    }

    private void initModel() {
        List<EventDTO> eventList = eventService.getAllDTO();
        model.setAll(eventList);
    }

    public void eventSubscribe(ActionEvent event) {
        String message = "";
        EventDTO selected = tableViewEvents.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                eventService.subscribeToEvent(selected.getId(), currentUsername);
                message = "Successfully subscribed to " + selected.getName() + "!";
            } catch (Exception e) {
                message = "You are already subscribed to this event!";
            }
        } else
            message = "You didn't select any event that you want to subscribe to!";
        status.setText(message);
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

    public void switchNewEvent(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("newevent-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        NewEventController newEventController = newMenu.getController();
        newEventController.initialize1(currentUsername);
        newEventController.setServices();

        stage.setScene(newMenuScene);
        stage.show();
    }

    @Override
    public void update(EventEvent eventEvent) {
        paginationEvents.setPageFactory(this::createPage);
    }

    public void switchMyEvents(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("myEvents-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MyEventsController myEventsController = newMenu.getController();
        myEventsController.initialize0(newMenuScene, stage);
        myEventsController.initialize1(currentUsername);
        myEventsController.setServices();

        stage.setScene(newMenuScene);
        stage.show();
    }

}
