package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import socialnetwork.model.MessageDTO;
import socialnetwork.model.UserDTO;
import socialnetwork.repository.db.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.utils.events.FriendRequestEvent;
import socialnetwork.utils.observer.Observer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReportsController extends UserController implements Observer<FriendRequestEvent> {

    MessageService messageService;
    FriendshipService friendshipService;

    List<UserDTO> friendsFromFirstReport;
    List<MessageDTO> messagesFromFirstReport;
    List<UserDTO> friendsOfUser;
    ObservableList<UserDTO> friendsModel = FXCollections.observableArrayList();
    ObservableList<MessageDTO> messagesModel = FXCollections.observableArrayList();
    ObservableList<UserDTO> friendsOfUserModel = FXCollections.observableArrayList();


    @FXML
    DatePicker beginDatePicker;
    @FXML
    DatePicker endDatePicker;

    @FXML
    TableView<UserDTO> friendsReport;

    @FXML
    TableView<MessageDTO> messagesReport;

    @FXML
    TableColumn<UserDTO, String> tableColumnName;

    @FXML
    TableColumn<UserDTO, String> tableColumnFriendsDate;

    @FXML
    TableColumn<MessageDTO, String> tableColumnFrom;

    @FXML
    TableColumn<MessageDTO, String> tableColumnText;

    @FXML
    TableColumn<MessageDTO, String> tableColumnMessagesDate;

    @FXML
    Button generatePdfButton;
    @FXML
    Button switchReportButton;
    @FXML
    Label newFriendsLabel;
    @FXML
    Label chooseFriendLabel;
    @FXML ComboBox<UserDTO> friendComboBox;

    @FXML
    Button generateReport;


    @Override
    public void update(FriendRequestEvent friendRequestEvent) {

    }

    public void initialize(){
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tableColumnFriendsDate.setCellValueFactory(new PropertyValueFactory<>("dateOfFriendship"));
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tableColumnText.setCellValueFactory(new PropertyValueFactory<>("text"));
        tableColumnMessagesDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.chooseFriendLabel.setVisible(false);
        this.friendComboBox.setVisible(false);
        this.generatePdfButton.setVisible(false);
        this.firstReport.set(true);
        generatePdfButton.getStylesheets().add("/css/Button.css");
        switchReportButton.getStylesheets().add("/css/Button.css");
        generateReport.getStylesheets().add("/css/Button.css");
        friendsReport.getStylesheets().add("/css/TableView.css");
        messagesReport.getStylesheets().add("/css/TableView.css");
        beginDatePicker.getStylesheets().add("/css/DatePicker.css");
        endDatePicker.getStylesheets().add("/css/DatePicker.css");
        friendsReport.setItems(friendsModel);
        messagesReport.setItems(messagesModel);
    }

    private void initFirstReportModel(){
        friendsModel.setAll(friendsFromFirstReport);
        messagesModel.setAll(messagesFromFirstReport);

    }
    private void initSecondReportModel(){
        setListOfFriends();
        friendsOfUserModel.setAll(friendsOfUser);
    }
    public void setServices() {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        GroupDBRepository groupDBRepository = new GroupDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        MessageDBRepository messageDBRepository = new MessageDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.messageService = new MessageService(userDBRepository, friendshipDBRepository,friendRequestDBRepository,messageDBRepository,groupDBRepository);
        this.friendshipService = new FriendshipService(userDBRepository,friendshipDBRepository);

    }

    public void generateFirstReport(ActionEvent actionEvent){

        if(beginDatePicker.getValue()!=null && endDatePicker.getValue()!=null){
            try{
                LocalDateTime startDate = this.beginDatePicker.getValue().atStartOfDay();
                LocalDateTime endDate = this.endDatePicker.getValue().atStartOfDay();
                this.friendsFromFirstReport = friendshipService.findFriendsCreatedByDate(startDate,endDate,currentUsername);
                this.messagesFromFirstReport = messageService.findMessagesFromDate(startDate,endDate,currentUsername);
                initFirstReportModel();
                this.generatePdfButton.setVisible(true);

            }catch (Exception e){
                //e.printStackTrace();
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }else{
            MessageAlert.showErrorMessage(null, "Select a date!");
        }

    }
    AtomicBoolean firstReport = new AtomicBoolean();

    private void makeFirstReportInvisible(){
        this.friendsReport.setVisible(false);
        this.newFriendsLabel.setVisible(false);
        this.switchReportButton.setText("Report 1");
        this.firstReport.set(false);
    }
    private void makeFirstReportVisible(){
        this.friendsReport.setVisible(true);
        this.newFriendsLabel.setVisible(true);
        this.switchReportButton.setText("Report 2");
        this.firstReport.set(true);

    }

    private void makeSecondReportVisible(){
        this.friendComboBox.setVisible(true);
        this.chooseFriendLabel.setVisible(true);


    }

    private void makeSecondReportInvisible(){
        this.friendComboBox.setVisible(false);
        this.chooseFriendLabel.setVisible(false);
    }

    public void initComboBox(){
        friendComboBox.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> param) {
                return new ListCell<UserDTO>() {
                    @Override
                    protected void updateItem(UserDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item == null || empty){
                            setGraphic(null);
                        }else{
                            setText(item.getFullName());
                        }
                    }
                };
            }


        });

        this.friendComboBox.setItems(friendsOfUserModel);
    }

    private void setListOfFriends(){
        friendsOfUser = this.friendshipService.getFriendList(currentUsername);
    }
    public void switchReports(ActionEvent actionEvent){
        if(firstReport.get()){
            makeFirstReportInvisible();
            initComboBox();
            initSecondReportModel();
            System.out.println(friendsOfUser);
            makeSecondReportVisible();
        }else{
            makeSecondReportInvisible();
            makeFirstReportVisible();
        }
    }

    public void switchGenerateButton(ActionEvent actionEvent){
        this.generatePdfButton.setVisible(false);
        if(firstReport.get())
            generateFirstReport(actionEvent);
        else
            generateSecondReport(actionEvent);
    }

    UserDTO selected;
    private void generateSecondReport(ActionEvent actionEvent){
        selected = this.friendComboBox.getValue();
        if(selected!=null){
            if(beginDatePicker.getValue()!=null && endDatePicker.getValue()!=null){
                LocalDateTime startDate = this.beginDatePicker.getValue().atStartOfDay();
                LocalDateTime endDate = this.endDatePicker.getValue().atStartOfDay();

                messagesFromFirstReport = this.messageService.findFriendMessagesFromDate(startDate,endDate,selected.getUserName(), currentUsername);
                messagesModel.setAll(messagesFromFirstReport);
                this.generatePdfButton.setVisible(true);

            }else{
                MessageAlert.showErrorMessage(null, "Please select both dates!");
            }
        }else{
            MessageAlert.showErrorMessage(null, "No friend Selected.");
        }
    }
    public void switchGeneratePdfButton(ActionEvent actionEvent){
        if(firstReport.get())
            generatePdfFirstReport(actionEvent);
        else
            generatePdfSecondReport(actionEvent);
    }
    public void generatePdfFirstReport(ActionEvent actionEvent){
        JFrame parentComponent = new JFrame();
        JFileChooser fileChooser = new JFileChooser();

        int returnVal = fileChooser.showOpenDialog(parentComponent);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            File toSave = fileChooser.getSelectedFile();
            try{
                PDDocument document = new PDDocument();
                PDPage pdPage = new PDPage();
                document.addPage(pdPage);

                PDPageContentStream contentStream = new PDPageContentStream(document,pdPage);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(9,600);
                contentStream.setLeading(14.5f);

                //content
                contentStream.showText("Friendships created in the interval: ");
                contentStream.newLine();
                friendsFromFirstReport.forEach(friend ->{
                    try{
                        contentStream.showText(friend.getFullName() + " " + " friends since: " + friend.getDateOfFriendship());
                        contentStream.newLine();

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                });

                contentStream.newLine();
                contentStream.showText("Messages received in the interval: ");
                writeContent(toSave, document, contentStream);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void generatePdfSecondReport(ActionEvent actionEvent){
        JFrame parentComponent = new JFrame();
        JFileChooser fileChooser = new JFileChooser();

        int returnVal = fileChooser.showOpenDialog(parentComponent);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            File toSave = fileChooser.getSelectedFile();
            try{
                PDDocument document = new PDDocument();
                PDPage pdPage = new PDPage();
                document.addPage(pdPage);

                PDPageContentStream contentStream = new PDPageContentStream(document,pdPage);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(9,600);
                contentStream.setLeading(14.5f);

                //content

                contentStream.showText("Messages received in the interval from " + selected.getFullName() + ": ");
                writeContent(toSave, document, contentStream);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void writeContent(File toSave, PDDocument document, PDPageContentStream contentStream) throws IOException {
        contentStream.newLine();
        messagesFromFirstReport.forEach(messageDTO -> {
            try{
                contentStream.showText(messageDTO.getFrom().getFirstName()+ " "
                        + messageDTO.getFrom().getLastName()+ " " + " sent at " +
                        messageDTO.getDate() + " the message: " + messageDTO.getText());
                contentStream.newLine();

            }catch (IOException e){
                e.printStackTrace();
            }
        });

        contentStream.endText();
        contentStream.close();
        document.save(toSave);
        document.close();
    }
}
