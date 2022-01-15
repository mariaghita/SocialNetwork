package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.BCrypt.MyCrypt;
import socialnetwork.Main;
import socialnetwork.model.User;
import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.UserService;

import java.io.IOException;

public class LoginController {
    UserService userService;

    public LoginController() {
    }

    @FXML
    private TextField userName;

    @FXML
    private PasswordField passWord;

    @FXML
    private Label wrong_login;

    @FXML
    private Button login_button;


    public void setService() {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
    }

    public void userLogin(ActionEvent event) throws IOException {
        String u = validateLogin();
        if(u != null)
            switchToUser(event, u);
    }

    private String validateLogin() { //nu merge parola e bulangiu
        String un = null;
        if(userName.getText().isEmpty() || passWord.getText().isEmpty()) {
            wrong_login.setText("Please enter all your credentials!");
            return null;
        } else {
            try {
                User u = userService.getOne(userName.getText());
                un = userName.getText();
                if (MyCrypt.verifyHash(passWord.getText(), u.getPassword()) == false){
                    wrong_login.setText("Invalid password!");
                    return null;
                }
                wrong_login.setText("Successful login!");
            } catch (Exception e) {
                wrong_login.setText("Invalid username!");
            }
        }
        return un;
    }

    private void switchToUser(ActionEvent event, String username) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("user-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        UserController userController = newMenu.getController();
        userController.initialize1(username);

        stage.setScene(newMenuScene);
        stage.show();
    }
}
