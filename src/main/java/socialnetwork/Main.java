package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import socialnetwork.controller.LoginController;
import socialnetwork.repository.db.GroupDBRepository;

import java.io.IOException;

public class Main extends Application {

    /*
    UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
    FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
    MessageDBRepository messageDBRepository = new MessageDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
    GroupDBRepository groupDBRepository = new GroupDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");

    UserService userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository);
    FriendRequestService friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, friendRequestDBRepository, messageDBRepository);
    */

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader menu = new FXMLLoader(Main.class.getResource("login-view.fxml"));

        Scene scene = new Scene(menu.load(), 750, 500);
        stage.setTitle("SunLit");
        stage.setScene(scene);

        LoginController loginController = menu.getController();
        loginController.setService();

        stage.show();
    }

    public static void main(String[] args) {launch();}
}