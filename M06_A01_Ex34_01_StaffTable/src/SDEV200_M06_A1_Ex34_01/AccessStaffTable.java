/**
* Assignment: SDEV200_M06_A1_Ex34_01
* File: AccessStaffTable.java
* Version: 1.0
* Date: 2/17/2024
* Author: Tomomi Hobara
* Description: This program displays, inserts and updates staff record on a database table. 
                The View button displays a record with a specified ID.
                The Insert button inserts a new record into the table.
                The Update button updates the record for specified ID.
                The Clear button clears the record shown in the text fields. 
* Variables: 
    - resultText: Text to display results of record processing
    - id, lastName, firstName, mi, address, city, state, telephone, email: String, fields in the Staff table
    - currentRecord: Current instance of AccessStaffTable for tracking the current record
    - previousRecord: Previous instance of AccessStaffTable for tracking the last viewed record
* Steps:
    1. Create and populate the Staff table
    2. Define constructors, GUI components, and getter and setter methods
    3. Set up listeners for the buttons
    4. Define methods for viewing, inserting, updating, and clearing records

*/
package SDEV200_M06_A1_Ex34_01;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class AccessStaffTable extends Application {
    // Declare data fields
    private Text resultText;
    private String id = "";
    private String lastName = "";
    private String firstName = "";
    private String mi = "";
    private String address = "";
    private String city = "";
    private String state = "";
    private String telephone = "";
    private String email = "";
    private TextField tfId;
    private TextField tfLastName;
    private TextField tfFirstName;
    private TextField tfMi;
    private TextField tfAddress;
    private TextField tfCity;
    private TextField tfState;
    private TextField tfTelephone;
    private TextField tfEmail;
    public static AccessStaffTable currentRecord;
    public static AccessStaffTable previousRecord;
    
    
    public static void main (String[] args) {
        Application.launch(args);
    }

    // No-arg constructor
    public AccessStaffTable() {
    }

    // Constructor with ID parameter
    public AccessStaffTable(String id) {
        this.id = id;
    }

    @Override  // Override the start method in the Application class
    public void start (Stage primaryStage) {
        // Initialize database connection
        Database.initializeDB();
        currentRecord = new AccessStaffTable();
        previousRecord = new AccessStaffTable();

        // Create a pane for text
        HBox paneForText = new HBox();
        paneForText.setPadding(new Insets(15, 15, 15, 15));
        
        // Create a text
        resultText = new Text("Show results");

        // Place the text in pane
        paneForText.getChildren().add(resultText);
        
        // Create a GridPane and set its properties
        GridPane paneForData = new GridPane();
        paneForData.setAlignment(Pos.TOP_LEFT);
        paneForData.setPadding(new Insets(15, 15, 15, 15));
        paneForData.setHgap(15);
        paneForData.setVgap(15);

        // Create nodes
        Label lblId = new Label("ID") ;
        Label lblLastName = new Label("Last Name");
        Label lblFirstName = new Label("First Name");
        Label lblMi = new Label("MI");
        Label lblAddress = new Label("Address");
        Label lblCity = new Label("City");
        Label lblState = new Label("State");
        Label lblTelephone = new Label("Telephone");
        Label lblEmail = new Label("Email");
        tfId = new TextField();
        tfLastName = new TextField();
        tfFirstName = new TextField();
        tfMi = new TextField();
        tfAddress = new TextField();
        tfCity = new TextField();
        tfState = new TextField();
        tfTelephone = new TextField();
        tfEmail = new TextField();

        // Format Textfield
        tfMi.setPrefColumnCount(1);

        // Place nodes in GridPane
        paneForData.addRow(0, lblId, tfId);
        paneForData.addRow(1, lblLastName, tfLastName, lblFirstName, tfFirstName, lblMi, tfMi);
        paneForData.addRow(2, lblAddress, tfAddress);
        paneForData.addRow(3, lblCity, tfCity, lblState, tfState);
        paneForData.addRow(4, lblTelephone, tfTelephone);
        paneForData.addRow(5, lblEmail, tfEmail);

        // Create a HBox for Buttons
        HBox paneForButtons = new HBox();
        paneForButtons.setAlignment(Pos.CENTER);
        paneForButtons.setPadding(new Insets(15, 15, 15, 15));
        paneForButtons.setSpacing(15);

        // Create Buttons
        Button btView = new Button("View");
        Button btInsert = new Button("Insert");
        Button btUpdate = new Button("Update");
        Button btClear = new Button("Clear");

        // Place Buttons in pane
        paneForButtons.getChildren().addAll(btView, btInsert, btUpdate, btClear);

        // Create listeners for the Buttons
        // View record based on the id value in the text field
        btView.setOnAction(e -> {
            try {
                currentRecord = Database.viewRecord(tfId.getText());
                if (currentRecord != null) {
                    // Fill the text fields with returned data
                    displayRecord(currentRecord);  // Display record in the text fields
                    resultText.setText("Record found");
                    previousRecord = currentRecord;  // Store the last viewed record
                }
                else {
                    clearRecord();
                    resultText.setText("Record not found");            
                }
            } 
            catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        // Insert a new record based on the values in the text fields
        btInsert.setOnAction(e -> {
            try {
                currentRecord = Database.insertRecord(getRecord());
                if (currentRecord != null) {
                    displayRecord(currentRecord);
                    resultText.setText("Record inserted");
                    previousRecord = currentRecord;
                    }
                    else {
                    resultText.setText("Insert failed. ID already exists.");  // Duplicate IDs are not permitted 
                    }
                }
                catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });

        // Update the record based on the values in the text fields
        btUpdate.setOnAction(e -> {
            try {
                currentRecord = Database.updateRecord(previousRecord, getRecord());
                if (currentRecord != null) {
                displayRecord(currentRecord);
                resultText.setText("Record updated");
                previousRecord = currentRecord;
                }
                else {
                resultText.setText("Update failed. ID cannot be changed.");  // Changing ID is not permitted 
                }
            }
            catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        
        // Clear the values in the text fields
        btClear.setOnAction(e -> clearRecord());

        // Create a VBox to stack the two panes vertically
        VBox root = new VBox();
        root.getChildren().addAll(paneForText, paneForData, paneForButtons);
    
        // Create a scene and place it in the stage
        Scene scene = new Scene(root, 630, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Exercise16_17");
        primaryStage.show();
    }

    // Clear the text fields
    public void clearRecord() {
        tfId.clear();
        tfLastName.clear();
        tfFirstName.clear();
        tfMi.clear();
        tfAddress.clear();
        tfCity.clear();
        tfState.clear();
        tfTelephone.clear();
        tfEmail.clear();
        resultText.setText("Show results");
    }

    // Display record in textfield
    private void displayRecord(AccessStaffTable currentRecord) {
        tfLastName.setText(currentRecord.getLastName());
        tfFirstName.setText(currentRecord.getFirstName());
        tfMi.setText(currentRecord.getMi());
        tfAddress.setText(currentRecord.getAddress());
        tfCity.setText(currentRecord.getCity());
        tfState.setText(currentRecord.getState());
        tfTelephone.setText(currentRecord.getTelephone());
        tfEmail.setText(currentRecord.getEmail());
    }

    // Get record from textfield and assign the values to an instance
    private AccessStaffTable getRecord() {
        currentRecord = new AccessStaffTable();

        currentRecord.setId(tfId.getText());
        currentRecord.setLastName(tfLastName.getText());
        currentRecord.setFirstName(tfFirstName.getText());
        currentRecord.setMi(tfMi.getText());
        currentRecord.setAddress(tfAddress.getText());
        currentRecord.setCity(tfCity.getText());
        currentRecord.setState(tfState.getText());
        currentRecord.setTelephone(tfTelephone.getText());
        currentRecord.setEmail(tfEmail.getText());

        return currentRecord;
    }

  
    // Create getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String toString(){
        return id + " " + lastName + " " + firstName + " " + mi + " " + address + " " + city 
                + " " + state + " " + telephone + " " + email;
    }
}