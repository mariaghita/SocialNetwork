package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import socialnetwork.model.Group;
import socialnetwork.model.Message;
import socialnetwork.model.MessageDTO;
import socialnetwork.model.UserDTO;
import socialnetwork.repository.db.*;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.MessageEvent;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagesController extends UserController implements Observer<MessageEvent> {
    MessageService messageService;
    UserService userService;
    List<Label> messages;
    Group selected;

    @FXML
    VBox chatBox;


    List<UserDTO> allUsers;
    List<Group> allGroups;
    ObservableList<UserDTO> model = FXCollections.observableArrayList();
    ObservableList<Group> groupModel = FXCollections.observableArrayList();

    @FXML
    TableView<UserDTO> usersTableView;

    @FXML
    TableColumn<UserDTO,String> tableColumnName;

    @FXML
    TableColumn<Group, String> tableGroupColumnName;

    @FXML
    TableColumn<UserDTO,String> tableColumnName1;

    @FXML
    TextField searchBar;

    @FXML
    Label chatUser;

    @FXML
    TextField textMessage;

    @FXML
    ScrollPane scroller;

    @FXML
    Button sendButton;

    @FXML
    Label noChatSelected;

    @FXML
    TableView<Group> groupsTableView;

    @FXML
    Button switchButton;

    @FXML
    Button createGroupButton;

    @FXML
    Button createButton;

    private AtomicBoolean friendsBool = new AtomicBoolean();

    private String friendUsername;

     @FXML
     TableView<UserDTO> createGroupTableView;

     @FXML
     TableColumn<UserDTO,String> tableColumnUsername;

    public MessagesController(){super();}

    @FXML
    private void initialize(){
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usersTableView.getStylesheets().add("/css/tableviews.css");
        usersTableView.setItems(model);

        this.scroller.getStylesheets().add("/css/ScrollPane.css");
        chatBox.setStyle("-fx-background-color: transparent");

        this.friendsBool.set(true);
        this.groupsTableView.setVisible(false);
        this.switchButton.setText("Groups");
        this.createGroupButton.setVisible(false);
        this.createGroupTableView.setVisible(false);
        this.chatUser.setFont(new Font("Script MT Bold", 30));
        this.noChatSelected.setFont(new Font("Script MT Bold", 40));
        this.textMessage.getStylesheets().add("/css/textfields.css");
        textMessage.setStyle("-fx-padding: 10 0 0 0");
        this.createButton.setVisible(false);
        makeGroupsInvisible();
        searchBar.getStylesheets().add("/css/textfields.css");
        searchBar.setStyle("-fx-padding: 0.166667em;");
        newChat.getStylesheets().add("/css/Button.css");
        switchButton.getStylesheets().add("/css/Button.css");
        createGroupButton.getStylesheets().add("/css/Button.css");
        sendButton.getStylesheets().add("/css/Button.css");
        searchBar.setVisible(true);
        searchBar.textProperty().addListener(o -> handleFilter());
        searchBar2.textProperty().addListener(o-> handleFilter2());
        searchBar2.getStylesheets().add("/css/textfields.css");
        searchBar2.setStyle("-fx-padding: 0.166667em;");
        createButton.getStylesheets().add("/css/Button.css");
    }
    @FXML
    Button newChat;
    @FXML
    TextField searchBar2;

    private void initModel(){
        searchBar.clear();
        makeChatBoxInvisible();
        model.setAll(allUsers);
        searchBar2.setVisible(false);
        searchBar.setVisible(true);

    }

    private void setList(){
        allUsers = StreamSupport.stream(userService.getAll().spliterator(), false)
                .filter(e -> !Objects.equals(e.getId(), currentUsername))
                .map(e -> new UserDTO(e.getId(), userService.getOne(e.getId()).getFirstName(), userService.getOne(e.getId()).getLastName(), null))
                .collect(Collectors.toList());

    }

    @FXML
    private void initializeGroup(){
        tableGroupColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupsTableView.getStylesheets().add("/css/tableviews.css");
        groupsTableView.setItems(groupModel);
        this.chatUser.setFont(new Font("Script MT Bold", 20));
        groupVisible.set(false);
        searchBar.setVisible(false);
        searchBar2.setVisible(true);

    }

    List<String> users = new ArrayList<String>();
    final String[] usersToAdd = {""};

    @FXML
    private void initializeCreateGroup(){
        tableColumnName1.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        createGroupTableView.getStylesheets().add("/css/tableviews.css");
        this.noChatSelected.setVisible(false);
        this.chatUser.setFont(new Font("Script MT Bold",16));
        createGroupTableView.setItems(model);

    }

    @FXML
    private void initCreateGroupModel(){
        makeChatBoxInvisible();

    }

    private void makeCreateGroupVisible(){
        this.createGroupTableView.setVisible(true);
        this.createButton.setVisible(true);
        initializeCreateGroup();
        setList();
        initCreateGroupModel();
        this.textMessage.setVisible(true);

    }

    private void makeCreateGroupInvisible(){
        this.createGroupTableView.setVisible(false);
        this.textMessage.setVisible(false);
        this.createButton.setVisible(false);

    }

    private void initGroupModel(){
        searchBar.clear();
        makeChatBoxInvisible();
        groupModel.setAll(allGroups);
    }

    private void setGroupList(){
        allGroups = messageService.getGroupsOfAUser(currentUsername);
    }


    public void setServices(String username){
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        MessageDBRepository messageDBRepository = new MessageDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        GroupDBRepository groupDBRepository = new GroupDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        this.messageService = new MessageService(userDBRepository,friendshipDBRepository,friendRequestDBRepository,messageDBRepository,groupDBRepository);

        messageService.addObserver(this);

        this.currentUsername = username;


        setList();
        initModel();
    }



    private void handleFilter() {
        searchPredicate(searchBar, model, allUsers);
    }
    private void handleFilter2(){searchGroupPredicate(searchBar,groupModel,allGroups);}
    private void makeChatBoxInvisible(){
        this.noChatSelected.setVisible(true);
        this.chatBox.setVisible(false);
        this.scroller.setVisible(false);
        this.textMessage.setVisible(false);
        this.sendButton.setVisible(false);
    }
    private void makeChatBoxVisible(){
        this.noChatSelected.setVisible(false);
        this.chatBox.setVisible(true);
        this.scroller.setVisible(true);
        this.textMessage.setVisible(true);
        this.sendButton.setVisible(true);
    }

    static void searchPredicate(TextField searchBar, ObservableList<UserDTO> model, List<UserDTO> allUsers) {
        Predicate<UserDTO> p1 = e -> e.getFullName().toLowerCase().contains(searchBar.getText().toLowerCase());
        Predicate<UserDTO> p2 = e -> e.getFullName().toLowerCase().startsWith(searchBar.getText().toLowerCase());
        Predicate<UserDTO> p3 = e -> e.getFullNameRev().toLowerCase().contains(searchBar.getText().toLowerCase());
        Predicate<UserDTO> p4 = e -> e.getFullNameRev().toLowerCase().startsWith(searchBar.getText().toLowerCase());

        model.setAll(allUsers
                .stream()
                .filter(p1.or(p2).or(p3).or(p4))
                .collect(Collectors.toList()));
    }

    static void searchGroupPredicate(TextField searchBar, ObservableList<Group> model, List<Group> allGroups){
        Predicate<Group> p1 = e-> e.getName().toLowerCase().contains(searchBar.getText().toLowerCase());
        Predicate<Group> p2 = e-> e.getName().toLowerCase().startsWith(searchBar.getText().toLowerCase());

        model.setAll(allGroups
                .stream()
                .filter(p1.or(p2))
                .collect(Collectors.toList()));
    }

    @Override
    public void update(MessageEvent messageEvent) {
        if(friendsBool.get())
            showConversation();
        else
            showGroupConversation();
    }


    public void showConversation(){
        makeChatBoxVisible();
        chatBox.getChildren().clear();
        UserDTO selected = usersTableView.getSelectionModel().getSelectedItem();
        this.chatUser.setText(selected.getFullName());
        this.chatUser.setAlignment(Pos.CENTER);
        List<MessageDTO> conversation = this.messageService.getConversationWithAUser(currentUsername,selected.getUserName());

        messages = new ArrayList<Label>();
        for(MessageDTO messageDTO: conversation){
            insertChatRows(messageDTO);

        }
        chatBox.setSpacing(5);
        scroller.setContent(chatBox);
        scroller.setFitToWidth(chatBox.isFillWidth());
        scroller.setVvalue(1.0);
        scroller.setHvalue(1.0);
        scroller.setPannable(true);

    }

    public void sendMessage(ActionEvent actionEvent){
        UserDTO selected = usersTableView.getSelectionModel().getSelectedItem();

        if(selected!=null){
            try{
                Message message = new Message(this.currentUsername,List.of(selected.getUserName()),null,textMessage.getText());
                this.messageService.sendMessage(message);
                //showConversation();
                this.textMessage.clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else
            MessageAlert.showErrorMessage(null,"No user selected.");
    }

    public void makeGroupsInvisible(){
        this.groupsTableView.setVisible(false);
        this.usersTableView.setVisible(true);
        this.createGroupButton.setVisible(false);
        this.searchBar2.setVisible(false);
        this.noChatSelected.setVisible(true);
        this.chatUser.setFont(new Font("Script MT Bold", 16));
    }

    private void makeGroupsVisible(){
        this.usersTableView.setVisible(false);
        this.groupsTableView.setVisible(true);
        this.createGroupButton.setVisible(true);
        this.searchBar2.setVisible(true);
        this.noChatSelected.setVisible(false);
        this.chatUser.setFont(new Font(12));
        initializeGroup();
        setGroupList();
        initGroupModel();
    }

    public void switchGroups(ActionEvent actionEvent){
        makeGroupsVisible();
        this.switchButton.setText("Users");
        this.friendsBool.set(false);

    }

    public void chatButtonFunction(ActionEvent actionEvent){
        if(friendsBool.get())
            showConversation();
        else
            showGroupConversation();

    }

    public void showGroupConversation(){
        makeCreateGroupInvisible();
        selected = groupsTableView.getSelectionModel().getSelectedItem();
        this.chatBox.getChildren().clear();

        this.chatUser.setText("Group: " + selected.getName());
        this.chatUser.setAlignment(Pos.CENTER);
        List<MessageDTO> conversation = this.messageService.getGroupConversation(selected.getId());

        makeChatBoxVisible();

        messages = new ArrayList<Label>();

        for(MessageDTO messageDTO: conversation){

            if(!(messageDTO.getFrom().getId().equals(currentUsername))) {
                Label toAdd = new Label(messageDTO.getFrom().getFirstName()+ ": ");

                toAdd.setMaxWidth(Double.MAX_VALUE);
                toAdd.setAlignment(Pos.CENTER_LEFT);
                toAdd.setPadding(new Insets(0,1,-7,0));
                toAdd.setFont(new Font("Arial", 15));
                toAdd.setStyle("-fx-font-weight: bold");
                messages.add(toAdd);
                chatBox.getChildren().add(messages.get(messages.size()-1));
            }

            insertChatRows(messageDTO);

        }
        chatBox.setSpacing(5);
        scroller.setContent(chatBox);
        scroller.setFitToWidth(chatBox.isFillWidth());
        scroller.setVvalue(1.0);
        scroller.setHvalue(1.0);
        scroller.setPannable(true);

    }

    private void insertChatRows(MessageDTO messageDTO) {
        Label toAdd = new Label(messageDTO.getText());


        Double length = (double) messageDTO.getText().length();
        toAdd.setMaxWidth(Double.MAX_VALUE);
        toAdd.setFont(new Font("Arial", 16));
        if(messageDTO.getFrom().getId().equals(currentUsername)) {
            toAdd.setAlignment(Pos.CENTER_RIGHT);

            toAdd.getStylesheets().add("/css/LabelForMessages.css");

        }

        else {
            toAdd.setAlignment(Pos.CENTER_LEFT);
            toAdd.getStylesheets().add("/css/LabelForFriendMessages.css");

        }
        messages.add(toAdd);
        chatBox.getChildren().add(toAdd);
    }

    private void switchUsers(ActionEvent actionEvent){
        makeChatBoxInvisible();
        makeGroupsInvisible();
        makeCreateGroupInvisible();
        this.chatUser.setVisible(false);
        this.noChatSelected.setVisible(false);
        this.switchButton.setText("Groups");
        this.friendsBool.set(true);
        initModel();
    }

    public void switchButtonFunction(ActionEvent actionEvent){
        if(friendsBool.get())
            switchGroups(actionEvent);
        else
            switchUsers(actionEvent);
    }

    public void sendGroupMessage(ActionEvent actionEvent){

        if(this.selected != null){
            try{
                Message message = new Message(this.currentUsername,List.of(selected.getId().toString()),null,textMessage.getText());
                messageService.sendAGroupMessage(message);
                //showGroupConversation();
                this.textMessage.clear();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else
            System.out.println("Da");
    }

    public void sendButtonFunction(ActionEvent actionEvent){
        if(friendsBool.get())
            sendMessage(actionEvent);
        else
            sendGroupMessage(actionEvent);
    }

    public void switchCreateGroup(ActionEvent actionEvent){
        makeCreateGroupVisible();
        createGroupButton.setText(" - ");
        groupVisible.set(true);
        noChatSelected.setVisible(false);
    }
    AtomicBoolean groupVisible = new AtomicBoolean();


    private void switchCreateGroupReverse(ActionEvent actionEvent){
        makeCreateGroupInvisible();
        noChatSelected.setVisible(true);
        createGroupButton.setText("+");
        groupVisible.set(false);
        chatUser.setVisible(false);
    }

    public void switchCreateGroupButton(ActionEvent actionEvent){
        if(!(groupVisible.get()))
            switchCreateGroup(actionEvent);
        else
            switchCreateGroupReverse(actionEvent);

    }

    public void createNewGroup(ActionEvent actionEvent){
        if(!Objects.equals(this.textMessage.getText(), "") || usersToAdd[0].length() < 1){
            try{
                Group group = new Group(textMessage.getText());
                users.add(currentUsername);
                group.setUsers(this.users);
                this.messageService.createAGroup(group);
                this.textMessage.clear();
                this.chatUser.setText("");
                setGroupList();
                initGroupModel();
                users.clear();
                usersToAdd[0] = "";
                makeCreateGroupInvisible();

            }catch (Exception e){
               MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }else{
            MessageAlert.showErrorMessage(null, "Please provide a name or select users.");
        }
    }

    @FXML
    private void handleRowSelect(){
        UserDTO row = createGroupTableView.getSelectionModel().getSelectedItem();
        if(row == null) return;
        for(String user : this.users){
            if(row.getUserName().equals(user))
                return;
        }

        users.add(row.getUserName());
        System.out.println(users);
        usersToAdd[0] += (row.getFullName() + " ");
        this.chatUser.setText(Arrays.toString(usersToAdd));
    }


}
