package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.MessageDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        MessageDBRepository messageDBRepository = new MessageDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");

        UserService userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository);
        FriendRequestService friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, friendRequestDBRepository, messageDBRepository);
        launch();
    }
}