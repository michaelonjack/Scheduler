import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

// Class used to access database information from the Magician table 
public class MagicianTable {
    
    static final String DATABASE_URL = "jdbc:derby://localhost:1527/Magician";
    static final String USERNAME = "USERNAME";
    static final String PASSWORD = "PASSWORD";
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String[] magicians;
    
    private PreparedStatement selectFreeMagician = null;
    private PreparedStatement addMagician = null;
    private PreparedStatement deleteMagician = null;

    // Constructor
    public MagicianTable() {}
    
    // Adds a new magician to the Magician table in the databse and
    // moves customers on the wait list to the schedule
    // Returns a String that lists the customers that have been moved
    public String addEntry(String newMagician) {
        
        WaitingListTable myWaitingListTable = new WaitingListTable();
        ScheduleTable myScheduleTable = new ScheduleTable();
        int count; // integer variable needed to use the statement.executeUpdate command
        String customer;
        String holiday;
        Timestamp timestamp;
        String customersAdded = "Customers moved from the waiting list to the schedule:\n";
        
        try {
	    // Connect to the desired database
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            addMagician = connection.prepareStatement("INSERT INTO Magician (Name) VALUES (?)");
            addMagician.setString(1, newMagician);
            count = addMagician.executeUpdate();
            
            // Returns the names of customers to be moved from the WaitingList with their holiday
            resultSet=myWaitingListTable.getNextScheduled();
            
            while(resultSet.next()) {
                customer = resultSet.getString(1);
                holiday = resultSet.getString(2);
                timestamp = resultSet.getTimestamp(3);
                customersAdded = customersAdded + "-" + customer + "\n";
                myScheduleTable.addEntry(holiday, customer, timestamp);
                myWaitingListTable.deleteEntry(holiday, customer, timestamp);
            }
            
            // If no new customers have been added, inform the user
            if(customersAdded.equals("Customers moved from the waiting list to the schedule:\n"))
                customersAdded = customersAdded + "\tNONE";

        } // end try
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // end of catch
        
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
        
        
        return customersAdded;
    } // End of addEntry method
    
    
    
    // Removes a magician from the table
    public void deleteEntry(String magician) {
        
        int count;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            // Delete the specified magician from the Magician table
            deleteMagician = connection.prepareStatement("DELETE FROM Magician "
                    + "WHERE Name = ?");
            deleteMagician.setString(1, magician);
            count = deleteMagician.executeUpdate();
            
        } // End of try block
        
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } // End of catch block
    }
    
    
    
    
    
    // Fills an array with the names of the magicians found in the database table
    public String[] getMagicianNames() {
        
         magicians = new String[getRowCount()];
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            
            // Select the 'Name' column from the Magician table
            resultSet = statement.executeQuery( "SELECT Name FROM Magician" );
            
            // Process query results
            // next() method will go to next row of data, initially set just before first row
            // Returns false when there is no more data to be processed
            for( int i = 0; resultSet.next(); i++ ) {
                magicians[i] = resultSet.getString(1);
            }// End for loop
        } // End try block
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();;
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
        
        return magicians;
    } // End of getMagicianNames method
    
    

    // Returns the name of a magician that has not yet been scheduled for a specified holiday
    public String getFreeMagician(String holiday) {
        
        String freeMagician = new String();
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            selectFreeMagician = connection.prepareStatement("SELECT Name FROM Magician WHERE "
                    + "Name NOT IN(SELECT Magician FROM Schedule WHERE Holiday = ?)");
            selectFreeMagician.setString(1, holiday);
            
            resultSet = selectFreeMagician.executeQuery();
            
            // If the table returned is not empty, set freeMagician to the name on the table
            if( resultSet.next() ) 
                freeMagician = resultSet.getString(1);
            else
                freeMagician = "0";
            
        } // end try
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();;
        } // end of catch
        
        finally {
            try {
                resultSet.close();
                connection.close();
            } // End of try block
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // End of catch block
        } // End of finally block
       
       return freeMagician;
    } // end of getFreeMagician method
    
    
    // Returns the number of rows in the Magician table
    public int getRowCount() {
        
       int numRows = 0;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT Name FROM Magician");
            
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
    
    
    // Returns true if the specified magician is already a part of the table
    public boolean isMagicianEntered(String magician) {
        
        boolean entered = false;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT Name FROM Magician");
            
            for(int i = 0; resultSet.next(); i++) {
                if(resultSet.getString(1).equals(magician))
                    entered = true;
            }
            
        }
        
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } // End of catch block
        
        finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } // end of try
            
            catch(Exception exception) {
                exception.printStackTrace();
            } // end of catch
        } // end of finally
        
        return entered;
        
    } // End of isMagicianEntered method
    
 } // End of MagicianTable class
