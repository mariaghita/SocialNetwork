package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import socialnetwork.Main;

import java.io.IOException;

public class UserController {
    protected String currentUsername;
    protected double xOffset = 0, yOffset = 0;

    public UserController() {
    }

    @FXML
    protected Label currentUser;

    @FXML
    private Button manageFriends;

    @FXML
    private Button manageFriendRequests;

    @FXML
    private Button logout;

    @FXML
    private Button exit;

    protected void initialize0(Scene scene, Stage stage) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }

    protected void initialize1(String username) {
        this.currentUsername = username;
        currentUser.setText("Logged in as : " + username);
;    }

    public void userLogout(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        LoginController loginController = newMenu.getController();
        loginController.init(newMenuScene, stage);
        loginController.setService();

        stage.setScene(newMenuScene);
        stage.show();
    }

    public void switchManageFriends(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("friends-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FriendsController friendsController = newMenu.getController();
        friendsController.initialize0(newMenuScene, stage);
        friendsController.initialize1(currentUsername);
        friendsController.setServices();

        stage.setScene(newMenuScene);
        stage.show();
    }

    public static void switchFriendRequest(ActionEvent event, String Username) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("friendrequests-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FriendRequestsController friendRequestsController = newMenu.getController();
        friendRequestsController.initialize0(newMenuScene, stage);
        friendRequestsController.initialize1(Username);
        friendRequestsController.setServices();

        stage.setScene(newMenuScene);
        stage.show();
    }

    public void switchManageFriendRequests(ActionEvent event) throws IOException {
        switchFriendRequest(event, currentUsername);
    }

    public void switchManageDiscover(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("discover-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        DiscoverController discoverController = newMenu.getController();
        discoverController.initialize0(newMenuScene, stage);
        discoverController.initialize1(currentUsername);
        discoverController.setServices(currentUsername);

        stage.setScene(newMenuScene);
        stage.show();
    }

    public void switchManageEvents(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("events-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        EventsController eventsController = newMenu.getController();
        eventsController.initialize0(newMenuScene, stage);
        eventsController.initialize1(currentUsername);
        eventsController.setServices();

        stage.setScene(newMenuScene);
        stage.show();
    }

    public void switchManageMessages(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("messages-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        MessagesController messagesController = newMenu.getController();
        messagesController.initialize0(newMenuScene, stage);
        messagesController.initialize1(currentUsername);
        messagesController.setServices(currentUsername);

        stage.setScene(newMenuScene);
        stage.show();
    }

    public void switchManageReports(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("reports-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ReportsController reportsController = newMenu.getController();
        reportsController.initialize0(newMenuScene, stage);
        reportsController.initialize1(currentUsername);
        reportsController.setServices();

        stage.setScene(newMenuScene);
        stage.show();
    }

    public void switchManageNotifications(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("notifications-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);
        //newMenuScene.getStylesheets().addAll(Main.class.getResource("buttons.css").toExternalForm());

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        NotificationsController notificationsController = newMenu.getController();
        notificationsController.initialize0(newMenuScene, stage);
        notificationsController.initialize1(currentUsername);
        notificationsController.setServices();


        stage.setScene(newMenuScene);
        stage.show();
    }

    public void exitApp(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
