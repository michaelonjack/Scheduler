import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.swing.JTable;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

// Class used to access database information from the WaitingList table 
public class WaitingListTable {
    
    static final String DATABASE_URL = "jdbc:derby://localhost:1527/Magician";
    static final String USERNAME = "USERNAME";
    static final String PASSWORD = "PASSWORD";
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    
    private PreparedStatement addWaitListEntry = null;
    private PreparedStatement deleteWaitListEntry = null;
    
    // Constructor
    public WaitingListTable() {}
    
    
    // Adds a specified customer and hoiday to the WaitingList table
    public void addEntry(String holiday, String customer, Timestamp timestamp) {
        
        int count; // integer variable needed to use the statement.executeUpdate command
        
        try {
	    // Connect to the desired database
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            addWaitListEntry = connection.prepareStatement("INSERT INTO WaitingList "
                    + "(Holiday, CUSTOMER_NAME, Timestamp) values (?, ?, ?)"); 
            addWaitListEntry.setString(1, holiday);
            addWaitListEntry.setString(2, customer);
            addWaitListEntry.setTimestamp(3, timestamp);
            count = addWaitListEntry.executeUpdate();
            
            
            
        } // end of try block
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();;
        } // end of catch
            
    } // End addEntryMethod
    
    
    // Removes a specified customer and holiday from the WaitingList table
    public void deleteEntry(String holiday, String customer, Timestamp timestamp) {
        
        int count;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            deleteWaitListEntry = connection.prepareStatement("DELETE FROM WaitingList "
                    + "WHERE CUSTOMER_NAME = ? AND Holiday = ? AND Timestamp = ?");
            deleteWaitListEntry.setString(1, customer);
            deleteWaitListEntry.setString(2, holiday);
            deleteWaitListEntry.setTimestamp(3, timestamp);
            
            count = deleteWaitListEntry.executeUpdate();
            
        } // End of try block
        
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } // End of catch block
    } // End of method deleteEntry
    
    
    
    // Creates a JTable for the Waiting List from database data
    public JTable createJTable() {
        
        String[] columnNames = {"Customer Name", "Holiday"};
        String[][] data = new String[getRowCount()][2];
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            
            // Fill the first column of data array with customer names
            // Fill the second column of data array with holiday names
            resultSet = statement.executeQuery( "SELECT CUSTOMER_NAME, Holiday FROM WaitingList"
                    + " ORDER BY Timestamp ASC" );
            for( int i = 0; resultSet.next(); i++ ) {
                data[i][0] = resultSet.getString(1);
                data[i][1] = resultSet.getString(2);
            }// End for loop
            
        } // End of try block
        
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
        JTable waitingList = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return waitingList;
    }
    
    
    // Returns the number of rows in the WaitingList table
    public int getRowCount() {
        
        int numRows = 0;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery( "SELECT CUSTOMER_NAME FROM WaitingList" );
            
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
    
    
    // Returns a list of customers and their holidays that can be moved from the Waiting 
    // List to the schedule when a magician is added
    public ResultSet getNextScheduled() {
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            
            resultSet = statement.executeQuery("SELECT CUSTOMER_NAME, WaitingList.Holiday, WaitingList.Timestamp"
                    + " FROM (SELECT holiday, MIN(Timestamp) AS Timestamp FROM WaitingList GROUP BY Holiday) min_date"
                    + " JOIN WaitingList ON min_date.Holiday = WaitingList.Holiday AND "
                    + "min_date.Timestamp = WaitingList.Timestamp");
            
        } // End of try block
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        return resultSet;
    } // End of getNextScheduled
    
    
    // Returns the customer names and holidays from the waiting list
    // Used when contructing the total customer list
    public ResultSet getCustomers() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            
            resultSet = statement.executeQuery("SELECT CUSTOMER_NAME, Holiday, Timestamp FROM WaitingList");
            
        } // End of try block
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // End of catch block
        
        return resultSet;
    }
    
} // End WaitingList class
