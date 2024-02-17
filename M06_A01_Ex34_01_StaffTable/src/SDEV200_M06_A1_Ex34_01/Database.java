// Description: Create and populate a table. Process data when the buttons in AccessStaffTable.java are pressed.

package SDEV200_M06_A1_Ex34_01;

import java.sql.*;

public class Database {
    private static final String url = "jdbc:mysql://localhost:3306/javabook";
    private static final String user = "root";
    private static final String password = "";
    private static final String tableName = "StaffTable34_01";
    private static Connection connection;

    // Create and populate a table
    static void initializeDB() {
        try {
            //Load the JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
            
            // Establish a connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected");

            // Create a statement
            Statement stmt = connection.createStatement();

            // Check if table exists
            if (checkTableExists(connection, tableName)) {
            // Drop StaffTable if it exists
                dropTable(stmt, tableName);
            }

            // Create table
            createTable(stmt, tableName);

            // Insert data into table
            insertData(stmt, tableName);

            // // Close the resources
            // stmt.close();
            // connection.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // Check if table exists
    public static boolean checkTableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }

    // Drop table if exists
    public static void dropTable(Statement stmt, String tableName) throws SQLException {
        String dropTableStmt = "DROP TABLE " + tableName;
        stmt.executeUpdate(dropTableStmt);
        System.out.println(tableName + " dropped");
    }
    

    // Create a table
    private static void createTable(Statement stmt, String tableName) throws SQLException {
        String createTableStmt = "CREATE TABLE " + tableName + " ("
            + "id CHAR(9) NOT NULL,"
            + "lastName VARCHAR(15),"
            + "firstName VARCHAR(15),"
            + "mi CHAR(1),"
            + "address VARCHAR(20),"
            + "city VARCHAR(20),"
            + "state VARCHAR(2),"
            + "telephone CHAR(10),"
            + "email VARCHAR(40),"
            + "PRIMARY KEY (id)"
            + ")";
        
        stmt.executeUpdate(createTableStmt);
        System.out.println(tableName + " created");
    }    


    // Insert initial data into table
    private static void insertData(Statement stmt, String tableName) throws SQLException {
        String insertDataStmt = "INSERT INTO " + tableName + " ("

            + "id, lastName, firstName, mi, address, city, state, telephone, email) "
            + "VALUES "
            + "('121', 'Smith', 'Peter', 'F', '100 Main Street', 'Savannah', 'GA', '9214345665', 'smithPeter@abc.com'), "
            + "('122', 'Johnson', 'Ann', 'M', '200 First Avenue', 'Atlanta', 'GA', '9123456789', 'ann_johnson@xyz.com'), "
            + "('123', 'Williams', 'John', 'W', '300 Pine Street', 'Savannah', 'GA', '9876543210', 'jwilliams22@defg.com'), "
            + "('124', 'Brown', 'Lisa', 'L', '400 Elm Road', 'Macon', 'GA', '8765432109', 'lisabrown@zmail.com')";
        
        stmt.executeUpdate(insertDataStmt);
        System.out.println("Data inserted into " + tableName);  
    }



    // View record based on ID textfield
    public static AccessStaffTable viewRecord(String id) throws SQLException {
       
        // Define a prepared statement to look up staff records by id
        String viewRecordStmt = "SELECT * FROM " + tableName + " WHERE id = ?"; 

        // Create an instance for looking up a staff record
        AccessStaffTable record = new AccessStaffTable(id);

        // Establish connection and create a prepared statement
        try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement pStmt = connection.prepareStatement(viewRecordStmt)) {
            pStmt.setString(1, id);  // Set parameter for the prepared statement
            
            // Execute the statement
            try (ResultSet resultSet = pStmt.executeQuery()) {
                // Set the cursor to the first row
                if (resultSet.next()) {
                // Assign rest of the values to the instance
                record.setLastName(resultSet.getString("lastName"));
                record.setFirstName(resultSet.getString("firstName"));
                record.setMi(resultSet.getString("mi"));
                record.setAddress(resultSet.getString("address"));
                record.setCity(resultSet.getString("city"));
                record.setState(resultSet.getString("state"));
                record.setTelephone(resultSet.getString("telephone"));
                record.setEmail(resultSet.getString("email"));
                
                System.out.println(record.toString());
                return record;
                }

                else {
                    System.out.println("No records found for ID: " + id);
                    return null;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();  // Handle the exception
            return null;
        }
    }


    // Insert a new record based on the values in the text fields
    public static AccessStaffTable insertRecord(AccessStaffTable currentRecord) throws SQLException {
        System.out.println("Record to be inserted: " + currentRecord.toString());
   
        // Define a prepared statement for checking if the new ID is unique in the table
        String checkIdStmt = "SELECT id FROM " + tableName + " WHERE id = ? ";

        // Define a prepared statement for inserting a record
        String insertRecordStmt = "INSERT INTO " + tableName +
        " SET id = ?, lastName=?, firstName=?, mi=?, address=?, city=?, state=?, telephone=?, email=?";
    
        try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement pSCheckIdStmt = connection.prepareStatement(checkIdStmt);
            PreparedStatement pSInsertRecordStmt = connection. prepareStatement(insertRecordStmt)) {
                
                pSCheckIdStmt.setString(1, currentRecord.getId());  // Assign id for WHERE clause
                
                try (ResultSet checkIDResult = pSCheckIdStmt.executeQuery()) {
                    if (!checkIDResult.next()) {  // If not row is returned (= Unique ID)                       
                            // Insert the new record
                            pSInsertRecordStmt.setString(1, currentRecord.getId());
                            pSInsertRecordStmt.setString(2, currentRecord.getLastName());
                            pSInsertRecordStmt.setString(3, currentRecord.getFirstName());
                            pSInsertRecordStmt.setString(4, currentRecord.getMi());
                            pSInsertRecordStmt.setString(5, currentRecord.getAddress());
                            pSInsertRecordStmt.setString(6, currentRecord.getCity());
                            pSInsertRecordStmt.setString(7, currentRecord.getState());
                            pSInsertRecordStmt.setString(8, currentRecord.getTelephone());
                            pSInsertRecordStmt.setString(9, currentRecord.getEmail());
                            
                            int rowsInserted = pSInsertRecordStmt.executeUpdate();

                            if (rowsInserted > 0) {
                                System.out.println("Record inserted: " + currentRecord.toString());
                                return currentRecord;
                            }
                            else {
                                return null;
                            }
                        }
                        else {
                            return null;
                        }
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();  // Handle the exception
                    return null;      
                }
    }
    
    
    // Update record based on the values in the text fields
    public static AccessStaffTable updateRecord(AccessStaffTable previousRecord, AccessStaffTable currentRecord) throws SQLException {
        System.out.println("Previous: " + previousRecord.toString());
        System.out.println("Current: " + currentRecord.toString());
        
        // Define a prepared statement
        String updateRecordStmt = "UPDATE " + tableName +
            " SET lastName=?, firstName=?, mi=?, address=?, city=?, state=?, telephone=?, email=?" +
            " WHERE id = ?";

        if (previousRecord.getId().equals(currentRecord.getId())) {
            try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement pSUpdateRecordStmt = connection.prepareStatement(updateRecordStmt)) {
            // Set parameter for the prepared statement
            pSUpdateRecordStmt.setString(1, currentRecord.getLastName());
            pSUpdateRecordStmt.setString(2, currentRecord.getFirstName());
            pSUpdateRecordStmt.setString(3, currentRecord.getMi());
            pSUpdateRecordStmt.setString(4, currentRecord.getAddress());
            pSUpdateRecordStmt.setString(5, currentRecord.getCity());
            pSUpdateRecordStmt.setString(6, currentRecord.getState());
            pSUpdateRecordStmt.setString(7, currentRecord.getTelephone());
            pSUpdateRecordStmt.setString(8, currentRecord.getEmail());
            pSUpdateRecordStmt.setString(9, currentRecord.getId());  // In WHERE clause 

                // Execute the statement
                try {
                    int rowsUpdated = pSUpdateRecordStmt.executeUpdate();
                    // Set the cursor to the first row
                    if (rowsUpdated > 0) {
                    return currentRecord;
                    }
                    else {
                    return null;
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();  // Handle the exception
                    return null;
                }
            }
        }
        return null;  // Return null when the IDs do not match. (Updating ID is not permitted.)
    }    

}
