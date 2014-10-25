import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.swing.JOptionPane;
import javax.swing.JTable;


// Class used to access database information from the Schedule table 
public class ScheduleTable {
    
    static final String DATABASE_URL = "jdbc:derby://localhost:1527/Magician";
    static final String USERNAME = "USERNAME";
    static final String PASSWORD = "PASSWORD";
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    
    private WaitingListTable myWaitingListTable = new WaitingListTable();
    private MagicianTable myMagicianTable = new MagicianTable();
    
    private PreparedStatement getHolidayRowCount = null;
    private PreparedStatement getHolidayStatusStatement = null;
    private PreparedStatement getMagicianStatusStatement = null;
    private PreparedStatement getMagicianRowCount = null;
    private PreparedStatement addScheduleEntry = null;
    private PreparedStatement magicianDroppedStatement = null;
    private PreparedStatement deleteScheduleEntry = null;

    // CONSTRUCTOR
    public ScheduleTable() {
        
    }
    
    // Adds an entry to the Schedule table when a customer is scheduled
    public void addEntry(String holiday, String customer, Timestamp timestamp) {
        
        // integer variable needed to use the statement.executeUpdate command
        int count; 
        // Get a magician to be assigned to the newly scheduled customer
        String freeMagician = myMagicianTable.getFreeMagician(holiday);
        
        try {
	    // Connect to the desired database
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            
            // Insert the new information into Schedule table if there is a free magician
            if( freeMagician != "0" ) {
                
                addScheduleEntry = connection.prepareStatement("INSERT INTO Schedule "
                        + "(HOLIDAY, CUSTOMER_NAME, MAGICIAN, TIMESTAMP)"
                        + "VALUES (?,?,?,?)");
                addScheduleEntry.setString(1,holiday);
                addScheduleEntry.setString(2,customer);
                addScheduleEntry.setString(3,freeMagician);
                addScheduleEntry.setTimestamp(4,timestamp);
                count = addScheduleEntry.executeUpdate();
                
                JOptionPane.showMessageDialog(null, customer + " added to schedule!");
            }
            
            // If a magician is not available, insert info into the waiting list table
            else {
                myWaitingListTable.addEntry(holiday, customer,timestamp);
                JOptionPane.showMessageDialog(null, customer + " added to waiting list.");
            }

        } // end try
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // end of catch
        
    } // end addEntry method
    
    
    // Removes a specified customer and holiday from the WaitingList table
    public void deleteEntry(String holiday, String customer, Timestamp timestamp) {
        
        int count;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            deleteScheduleEntry = connection.prepareStatement("DELETE FROM Schedule "
                    + "WHERE CUSTOMER_NAME = ? AND Holiday = ? AND Timestamp = ?");
            deleteScheduleEntry.setString(1, customer);
            deleteScheduleEntry.setString(2, holiday);
            deleteScheduleEntry.setTimestamp(3, timestamp);
            
            count = deleteScheduleEntry.executeUpdate();
            
            // Check if customer on waiting list can be moved to schedule
            resultSet = myWaitingListTable.getNextScheduled();
            // Continue until a costumer has been moved from waiting list to schedule
            while( resultSet.next() ) {
                if(resultSet.getString(2).equals(holiday)) {
                    String movedCustomer = resultSet.getString(1);
                    String movedHoliday = resultSet.getString(2);
                    Timestamp movedTimestamp = resultSet.getTimestamp(3);
                    
                    addEntry(movedHoliday, movedCustomer, movedTimestamp);
                    myWaitingListTable.deleteEntry(movedHoliday, movedCustomer, movedTimestamp);
                }
            }
            
        } // End of try block
        
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } // End of catch block
    } // End of method deleteEntry
    
    
    
    
    
    
    
    
    
    
    // Creates and returns a JTable for the status of a specified holiday
    // by accessing the Schedule table
    public JTable getHolidayStatus(String holiday) {
        
        // The names of the columns in the table
        String[] columnNames = {"Customer Name","Magician"};
        // The data that occupies the table
        String[][] data = new String[getScheduledHolidayRowCount(holiday)][2];
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            // Select the customers scheduled for the specified holiday
            getHolidayStatusStatement = connection.prepareStatement("SELECT CUSTOMER_NAME"
                    + " FROM Schedule WHERE Holiday = ?");
            getHolidayStatusStatement.setString(1,holiday);
            resultSet = getHolidayStatusStatement.executeQuery();
            
            // Fill the first column of data array with customer names
            for( int i = 0; resultSet.next(); i++ ) {
                data[i][0] = resultSet.getString(1);
            }// End for loop
            
            // Select the magicians scheduled for the specified holiday
            getHolidayStatusStatement = connection.prepareStatement("SELECT Magician "
                    + "FROM Schedule WHERE Holiday = ?");
            getHolidayStatusStatement.setString(1, holiday);
            resultSet = getHolidayStatusStatement.executeQuery();
            
            // Fill the second column of data array with magician names
            for( int i = 0; resultSet.next(); i++ ) {
                data[i][1] = resultSet.getString(1);
            }// End for loop
        } // end of try block
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        finally {
            try {
                resultSet.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
        
        // Create and return a new JTable with the gathered information
        // Set the cells of the table to be uneditable
        JTable holidayStatus = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        return holidayStatus;
    } // End of getHolidayStatus method
    
    
    
    // Get the number of rows where a specified holiday is on the Schedule table
    public int getScheduledHolidayRowCount(String holiday) {
        
        int numRows = 0;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            getHolidayRowCount = connection.prepareStatement("SELECT Holiday "
                    + "FROM Schedule WHERE Holiday = ?");
            getHolidayRowCount.setString(1, holiday);
                    
            resultSet = getHolidayRowCount.executeQuery();
            
            for( int i = 1; resultSet.next(); i++ ) {
                numRows = i;
            }// End for loop
        }
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        finally {
            try {
                resultSet.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
        
        return numRows;
    } // End of getRowCount method
    
    
    public JTable getMagicianStatus(String magician) {
        
        String[] columnNames = {"Customer Name","Holiday"};
        String[][] data = new String[getScheduledMagicianRowCount(magician)][2];
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            // Get the names of the customers scheduled for the specified magician
            getMagicianStatusStatement = connection.prepareStatement("SELECT CUSTOMER_NAME"
                    + " FROM Schedule WHERE Magician = ?");
            getMagicianStatusStatement.setString(1,magician);
            resultSet = getMagicianStatusStatement.executeQuery();
            
            // Fill the first column of data array with customer names
            for( int i = 0; resultSet.next(); i++ ) {
                data[i][0] = resultSet.getString(1);
            }// End for loop
            
            // Get the names of the holidays scheduled for the specified magician
            getMagicianStatusStatement = connection.prepareStatement("SELECT Holiday "
                    + "FROM Schedule WHERE Magician = ?");
            getMagicianStatusStatement.setString(1, magician);
            resultSet = getMagicianStatusStatement.executeQuery();
            
            // Fill the second column of data array with holiday names
            for( int i = 0; resultSet.next(); i++ ) {
                data[i][1] = resultSet.getString(1);
            }// End for loop
        }
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        finally {
            try {
                resultSet.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
        
        // Create and return a new JTable with the gathered information
        // Set the cells of the table to be uneditable
        JTable magicianStatus = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        return magicianStatus;
    } // End of getMagicianStatus method
    
    
    // Returns the number of rows where a specified magician is on the Schedule table
     public int getScheduledMagicianRowCount(String magician) {
        
        int numRows = 0;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
            
            getMagicianRowCount = connection.prepareStatement("SELECT Magician "
                    + "FROM Schedule WHERE Magician = ?");
            getMagicianRowCount.setString(1, magician);
                    
            resultSet = getMagicianRowCount.executeQuery();
            
            for( int i = 1; resultSet.next(); i++ ) {
                numRows = i;
            }// End for loop
        }
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        finally {
            try {
                resultSet.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
        
        return numRows;
    } // End of getRowCount method
     
     
     
     // Moves the customers assigned to a dropped magician 
     public void magicianDropped(String droppedMagician) {
         
         int count; // integer variable used to execute the database update
         String customer; // Name of customer formerly scheduled with removed magician
         String holiday; // The holiday that customer was scheduled for
         Timestamp timestamp; // The customer's timestamp when they first scheduled
         
         try {
             connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
             
             // Select all customer information for those scheduled for the dropped magician
             magicianDroppedStatement = connection.prepareStatement("SELECT CUSTOMER_NAME,"
                     + "Holiday,Timestamp FROM Schedule WHERE Magician = ?");
             magicianDroppedStatement.setString(1, droppedMagician);
             resultSet = magicianDroppedStatement.executeQuery();
             
             // Reinsert the customer into the schedule with a new magician if possible,  
             // else it gets added to the waitlist with its original timestamp
             while(resultSet.next()) {
                 customer = resultSet.getString(1);
                 holiday = resultSet.getString(2);
                 timestamp = resultSet.getTimestamp(3);
                 addEntry(holiday, customer, timestamp);
             }
             
             // Once the customer has been reinserted into the schedule/waiting list
             // with the adjusted magician list, delete their original entry from the schedule
             magicianDroppedStatement = connection.prepareStatement("DELETE FROM Schedule "
                     + "WHERE Magician = ?");
             magicianDroppedStatement.setString(1, droppedMagician);
             count = magicianDroppedStatement.executeUpdate();
             
             
         } // End of try block
         
         catch(SQLException sqlException) {
             sqlException.getStackTrace();
         } // End of catch block
         
         finally {
            try {
                resultSet.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
     }
     
     
     
     // Creates a table of all customers in schedule and waiting list and their holidays
     public JTable createCustomerJTable() {
         
         int numRows = myWaitingListTable.getRowCount() + getRowCount();
         String[] columnNames = {"Customer Name","Holiday","Time Scheduled"};
         String[][] customerList = new String[numRows][3];
         
         try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery( "SELECT CUSTOMER_NAME, Holiday, Timestamp FROM Schedule" );
            
            for(int i = 0; resultSet.next(); i++) {
                customerList[i][0] = resultSet.getString(1);
                customerList[i][1] = resultSet.getString(2);
                customerList[i][2] = resultSet.getTimestamp(3).toString();
            }
            
            // Index where the schedule entries end and wait list entries begin
            int startIndex = getRowCount();
            
            resultSet = myWaitingListTable.getCustomers();
            for(int i = startIndex; resultSet.next(); i++) {
                customerList[i][0] = resultSet.getString(1);
                customerList[i][1] = resultSet.getString(2);
                customerList[i][2] = resultSet.getTimestamp(3).toString();
            }
                
            
            
        } // end of try block
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
         
        // Create and return a new JTable with the gathered information
        // Set the cells of the table to be uneditable
         JTable customerTable = new JTable(customerList, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
         
         return customerTable;
     }
     
     
    // Returns the number of rows in the WaitingList table
    public int getRowCount() {
        
        int numRows = 0;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery( "SELECT CUSTOMER_NAME FROM Schedule" );
            
            for( int i = 1; resultSet.next(); i++ ) {
                numRows = i;
            }// End for loop
        }
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
        
        return numRows;
    } // End of getRowCount method
     
} // end class ScheduleTable
