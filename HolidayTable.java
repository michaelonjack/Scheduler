import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


// Class used to access database information from the Holiday table 
public class HolidayTable {
    
    static final String DATABASE_URL = "jdbc:derby://localhost:1527/Magician";
    static final String USERNAME = "USERNAME";
    static final String PASSWORD = "PASSWORD";
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String[] holidays;
    
    private PreparedStatement addHoliday = null;
    
    
    // Constructor
    public HolidayTable() {}
    
    // Adds a new holiday to the Holiday table in the databse
    public void addEntry(String newHoliday) {
        
        int count; // integer variable needed to use the statement.executeUpdate command
        
        try {
	    // Connect to the desired database
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            addHoliday = connection.prepareStatement("INSERT INTO Holiday (Name) VALUES (?)");
            addHoliday.setString(1, newHoliday);
            
            count = addHoliday.executeUpdate();

        } // end try
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        } // end of catch
        
    } // End of addEntry method

    
     // Fills the holidays array with the names from the holiday table in Magician database
    // and returns the array
    public String[] getHolidayNames() {
        
        holidays = new String[getRowCount()];
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            // Select the Name column from the Holiday table
            resultSet = statement.executeQuery("SELECT Name FROM Holiday");
            
            // Process query results
            for( int i = 0; resultSet.next(); i++ ) {
                holidays[i] = resultSet.getString(1);
            }// end for loop
        } // end of try
        
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();;
        } // end of catch
        
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
        
        return holidays;
    } // End of getHolidayNames method
    
    
    
    
    
    // Returns the number of rows in the Holiday table
    public int getRowCount() {
        
        int numRows = 0;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT Name FROM Holiday");
            
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
    
    
    
    // Returns true if the specified holiday is already a part of the table
    public boolean isHolidayEntered(String holiday) {
        
        boolean entered = false;
        
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT Name FROM Holiday");
            
            for(int i = 0; resultSet.next(); i++) {
                if(resultSet.getString(1).equals(holiday))
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
        
    } // End of isHolidayEntered method
    
} // End of Holiday Table class
