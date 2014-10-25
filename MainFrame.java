import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.sql.Timestamp;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class MainFrame extends JFrame {
    
    // Create tables to get the information from databases
    private HolidayTable myHolidayTable = new HolidayTable();
    private MagicianTable myMagicianTable = new MagicianTable();
    private ScheduleTable myScheduleTable = new ScheduleTable();
    private WaitingListTable myWaitingListTable = new WaitingListTable();
    
    // Create array to hold the data retrieved by the tables
    private String[] holidayList;
    private String[] magicianList;
    
    // Table of all customers and their scheduled holidays
    private JTable customerTable;
    

    // CONSTRUCTOR
    public MainFrame() {
        
        super( "Magician Scheduler Application" ); // Window title
        initComponents();
        
        loadMagicianComboBox(); // Fills combobox with magician names
        loadHolidayComboBoxes(); // Fills comboboxes with holiday names
        
        // Create handler for when the Schedule button is pressed
        ScheduleButtonHandler scheduleHandler = new ScheduleButtonHandler();
        scheduleButton.addActionListener(scheduleHandler);
        
        // Create handler for when the GetStatus button for magicians is pressed
        MagicianStatusButtonHandler magicianHandler = new MagicianStatusButtonHandler();
        magicianStatusButton.addActionListener(magicianHandler);
        
        // Create handler for when the GetStatus button for holidays is pressed
        HolidayStatusButtonHandler holidayHandler = new HolidayStatusButtonHandler();
        holidayStatusButton.addActionListener(holidayHandler);
        
        // Create handler for when the GetWaitingList button is pressed
        WaitingListButtonHandler waitingListHandler = new WaitingListButtonHandler();
        waitingListButton.addActionListener(waitingListHandler);
        
        // Create handler for when the Add.. button is pressed for magicians
        AddMagicianButtonHandler addMagicianHandler = new AddMagicianButtonHandler();
        addMagicianButton.addActionListener(addMagicianHandler);
        
        // Create handler for when the Add.. button is pressed for holidays
        AddHolidayButtonHandler addHolidayHandler = new AddHolidayButtonHandler();
        addHolidayButton.addActionListener(addHolidayHandler);
        
        // Create handler for when the Drop.. button is pressed for magicians
        DropMagicianButtonHandler dropMagicianHandler = new DropMagicianButtonHandler();
        dropMagicianButton.addActionListener(dropMagicianHandler);
        
        // Create handler for when the Cancel.. button is pressed for holidays
        CancelHolidayButtonHandler cancelHolidayHandler = new CancelHolidayButtonHandler();
        cancelHolidayButton.addActionListener(cancelHolidayHandler);
        
    } // end of constructor
    
    
    // Fills the magician combobox with the names of the magicians
    public void loadMagicianComboBox() {
        
        magicianList = myMagicianTable.getMagicianNames(); // Creates array of magician names
        magicianComboBox.removeAllItems();
        magicianComboBox.addItem("-Select-");
        
        for(int i = 0; i < magicianList.length; i++) {
	    magicianComboBox.addItem(magicianList[i]);
        }
    } // End of loadMagicianComboBox method
    
    // Loads the holiday combo boxes with the names of the holidays
    public void loadHolidayComboBoxes () {
        
        holidayList = myHolidayTable.getHolidayNames();
        holidayComboBox1.removeAllItems();
        holidayComboBox2.removeAllItems();
        holidayComboBox1.addItem("-Select-");
        holidayComboBox2.addItem("-Select-");
        
        for(int i = 0; i < holidayList.length; i++) {
            holidayComboBox1.addItem(holidayList[i]);
            holidayComboBox2.addItem(holidayList[i]);
        }
    } // End of loadHolidayComboBoxes method
    
   
    // Private inner class to handle Schedule button events
    private class ScheduleButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed( ActionEvent event ) {
            
            // Get the holiday index the user has selected
            int selectedIndex = holidayComboBox1.getSelectedIndex()-1;
            // Get the name of the customer entered by the user
            String customerName = customerNameField.getText();
            
            // If the user has not selected a holiday, send error message
            if( selectedIndex < 0 )
                JOptionPane.showMessageDialog(rootPane, "Error!\nHoliday has not"
                        + " been chosen.");
            else if( customerName.equals("") )
                JOptionPane.showMessageDialog(rootPane, "Error!\nCustomer name has not"
                        + " been entered.");
            else {
                // Get the holiday the user has selected
                String requestedHoliday = holidayList[selectedIndex];
                // Create a timestamp for the newly scheduled entry
                java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
                
                myScheduleTable.addEntry(requestedHoliday, customerName, currentTimestamp);
                
                // Reset the fields
                customerNameField.setText("");
                holidayComboBox1.setSelectedIndex(0);
            }
        } // end of actionPerformed method
    } // end of ScheduleButtonHandler class

    
    // Private inner class to handle 'Get Staus' button events for magician
    private class MagicianStatusButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            
            // Get the magician index the user has selected
            int index = magicianComboBox.getSelectedIndex()-1;
            
            // If the user has not selected a magician, send error message
            if(index < 0)
                JOptionPane.showMessageDialog(rootPane, "Magician not selected.");
            
            else {
                String selectedMagician = magicianList[index];
                
                JTable magicianStatusTable = myScheduleTable.getMagicianStatus(selectedMagician);
                String frameTitle = "Status for " + selectedMagician;
                JFrame magicianFrame = new JFrame(frameTitle);
                magicianFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                JScrollPane pane = new JScrollPane(magicianStatusTable);
                magicianFrame.getContentPane().add(pane);
                magicianFrame.setSize(320, 350);
                magicianFrame.setVisible(true); // Set the frame visible
                magicianFrame.setResizable(true); // Allow the frame to be resized
            } // End of else statement
        } // End of actionPerformed method 
    } // End of handler private inner class
    
    
    // Private inner class to handle 'Get Status' button events for holidays
    private class HolidayStatusButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            
            int index = holidayComboBox2.getSelectedIndex()-1;
            
            if( index < 0 )
                JOptionPane.showMessageDialog(rootPane, "Holiday not selected.");
            else {
                String selectedHoliday = holidayList[index];
                
                JTable holidayStatusTable = myScheduleTable.getHolidayStatus(selectedHoliday);
                String frameTitle = "Status for " + selectedHoliday;
                JFrame holidayFrame = new JFrame(frameTitle);
                holidayFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                JScrollPane pane = new JScrollPane(holidayStatusTable);
                holidayFrame.getContentPane().add(pane);
                holidayFrame.setSize(320, 350);
                holidayFrame.setVisible(true);
                holidayFrame.setResizable(true);
            } // End of else statement
        } // End of actionPerformed method
    } // End of handler private class
    
    
    // Private inner class to handle 'Get Waiting List' button events
    private class WaitingListButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            
            // Create the JTable using the WaitingList table from the database
            JTable waitingList = myWaitingListTable.createJTable();
            
            // Create new JFrame to contain the table
            JFrame waitingFrame = new JFrame("Waiting List");
            waitingFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            JScrollPane pane = new JScrollPane(waitingList);
            waitingFrame.getContentPane().add(pane);
            waitingFrame.setSize(320, 350);
            waitingFrame.setVisible(true);
            waitingFrame.setResizable(true);
        } // End of actionPerformed method
     } // End of handler inner private class
    
    
    // Private inner class to handle 'Add..' button events for magicians
    private class AddMagicianButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            
            String newMagician; // Name of magician to be added
            String paneTitle = "ADD MAGICIAN"; // Title of the pop-up pane
            String paneMessage = "Please enter the name of the new magician:"; // Prompt for the pop-up pane
            String customersMoved; // Customers moved from waiting list to schedule
            newMagician = JOptionPane.showInputDialog(rootPane, paneMessage, paneTitle, JOptionPane.QUESTION_MESSAGE);
            
            if(newMagician == null) {
                // This means user has cancelled the request; do nothing
            }
            
            // If the user attempts to enter an empty string, send an error
            else if(newMagician.equals("")) {
                JOptionPane.showMessageDialog(rootPane, "ERROR!\nMagician name not given.");
            }
            
            // If the user attempts to enter an already entered magician, send an error
            else if(myMagicianTable.isMagicianEntered(newMagician)) {
                JOptionPane.showMessageDialog(rootPane, "ERROR!\n" + newMagician + 
                        " has already been entered.");
            }
            
            else {
                
                customersMoved = myMagicianTable.addEntry(newMagician);
                loadMagicianComboBox();
                String message = newMagician + " added!\n\n" + customersMoved;
                JOptionPane.showMessageDialog(rootPane, message);
            }
        } // End of actionPerformed method
    } // End of AddMagicianButtonHandler inner class
    
    
    // Private inner class to handle 'Add..' button events for holidays
    private class AddHolidayButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            
            // String variable used to hold the holiday name the user wishes to enter
            String newHoliday;
            String paneTitle = "ADD HOLIDAY";
            String paneMessage = "Please enter the name of new holiday:";
            newHoliday = JOptionPane.showInputDialog(rootPane, paneMessage, paneTitle, JOptionPane.QUESTION_MESSAGE);
            
            
            if(newHoliday == null) {
                // This means user has cancelled the request; do nothing
            }
            
            // If the user attempts to enter an empty string, send an error
            else if(newHoliday.equals("")) {
                JOptionPane.showMessageDialog(rootPane, "ERROR!\nHoliday name not given.");
            }
            
            // If the user attempts to enter an already entered holiday, send an error
            else if(myHolidayTable.isHolidayEntered(newHoliday)) {
                JOptionPane.showMessageDialog(rootPane, "ERROR!\n" + newHoliday + 
                        " has already been entered.");
            }
            
            else {
                myHolidayTable.addEntry(newHoliday);
                loadHolidayComboBoxes(); // reset comboxes to show the new holiday
                JOptionPane.showMessageDialog(rootPane, "Holiday added!");
            }
            
        } // End of actionPerformed method
    } // End of AddHolidayButtonHandler inner class
    
    
    // Private inner class to handle 'Drop..' button events for magicians
    private class DropMagicianButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            
            int index = magicianComboBox.getSelectedIndex()-1;
            
            // If the user has not selected a magician, send an error
            if(index < 0) {
                JOptionPane.showMessageDialog(rootPane,"Magician not selected.");
            }
            
            else {
                String removedMagician = magicianList[index];
                
                // Confirm with the user that they wish to remove the chosen magician
                int decision = JOptionPane.showConfirmDialog(rootPane, "Are you sure you wish"
                        + " to drop " + removedMagician + "?");
                
                // If the user presses yes, proceed to remove magician from database
                if( decision == JOptionPane.YES_OPTION) {
                    
                    // Removes the specified magician from the Magician table
                    myMagicianTable.deleteEntry(removedMagician);
                    
                    // Removes all entries with the specified magician from the Schedule table
                    myScheduleTable.magicianDropped(removedMagician);
                    loadMagicianComboBox(); // reset combo box to show change
                    JOptionPane.showMessageDialog(rootPane, removedMagician + " has been removed.");
                }
            }
        } // End of actionPerformed method
    
    } // End of DropMagicianButtonHandler private inner class  
    
    
    private class CancelHolidayButtonHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            
            customerTable = myScheduleTable.createCustomerJTable();
            
            // Button inside the pop up frame
            JButton cancelFinalButton = new JButton("Cancel Customer/Holiday");
            CancelFinalButtonHandler cancelHandler = new CancelFinalButtonHandler();
            cancelFinalButton.addActionListener(cancelHandler);
            
            // Create new JFrame to contain the table
            JFrame customerFrame = new JFrame("Pick customer/holiday to delete.");
            customerFrame.setLayout(new BorderLayout());
            customerFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            JScrollPane pane = new JScrollPane(customerTable);
            customerFrame.getContentPane().add(pane);
            customerFrame.add(cancelFinalButton, BorderLayout.SOUTH);
            customerFrame.setSize(550, 370);
            customerFrame.setVisible(true);
            customerFrame.setResizable(true);
        } // End of actionPerformed method
    } // End of CancelHolidayButtonHandler private inner class
    
    // Cancel Button Handler for the button inside the pop-up frame when 'Cancel..' is pressed
    private class CancelFinalButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            
            int selectedRow = customerTable.getSelectedRow();
            // The first row# on the table corresponding to wait list customers 
            int endSchedule = myScheduleTable.getRowCount();
            
            // -1 is returned when no row is selected
            if(selectedRow == -1) {
                JOptionPane.showMessageDialog(null,"Customer/Holiday was not selected.");
            }
            
            else {
                
                // Get the canceled customer's name object selected in the table 
                // and convert it to string
                Object customer = customerTable.getValueAt(selectedRow, 0);
                String canceledCustomer = customer.toString();
                
                // Get the canceled holiday object selected in the table 
                // and convert it to string
                Object holiday = customerTable.getValueAt(selectedRow, 1);
                String canceledHoliday = holiday.toString();
                
                // Get the canceled timestamp object selected in the table 
                // and convert it to string then convert it to Timestamp
                Object timestamp = customerTable.getValueAt(selectedRow, 2);
                String stringTimestamp = timestamp.toString();
                Timestamp canceledTimestamp = null;
                canceledTimestamp = canceledTimestamp.valueOf(stringTimestamp);
                
                // Confirm with the user that they wish to cancel the customer/holiday
                int decision = JOptionPane.showConfirmDialog(null, "Are you sure you wish"
                        + " to cancel " + canceledCustomer + " "
                        + "scheduled for " + canceledHoliday+ "?");
                
                // If the user has confirmed their decision, continue
                if( decision == JOptionPane.YES_OPTION ) {
                    // Delete the information selected from the Schedule or Waiting List
                    if(selectedRow < endSchedule)
                        myScheduleTable.deleteEntry(canceledHoliday, canceledCustomer, canceledTimestamp);
                    else
                        myWaitingListTable.deleteEntry(canceledHoliday, canceledCustomer, canceledTimestamp);
                    
                
                    String message = canceledCustomer + " scheduled for " + canceledHoliday + ""
                            + " has been successfully canceled.";
                    JOptionPane.showMessageDialog(null, message);
                    
                    // Close the frame this button came from once completed
                    Component component = (Component) event.getSource();
                    JFrame frame = (JFrame) SwingUtilities.getRoot(component);
                    frame.dispose();
                }
            }
        } // End of actionPerformed method
    } // End of CancelFinalButtonHandler private inner class
    
    
    
    
    
    
    
    /*** This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.*/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        customerNameField = new javax.swing.JTextField();
        customerNameLabel = new javax.swing.JLabel();
        magicianComboBox = new javax.swing.JComboBox();
        magicianLabel = new javax.swing.JLabel();
        holidayComboBox2 = new javax.swing.JComboBox();
        scheduleButton = new javax.swing.JButton();
        holidayLabel2 = new javax.swing.JLabel();
        holidayComboBox1 = new javax.swing.JComboBox();
        magicianStatusButton = new javax.swing.JButton();
        holidayStatusButton = new javax.swing.JButton();
        waitingListButton = new javax.swing.JButton();
        holidayLabel1 = new javax.swing.JLabel();
        frameTitleLabel = new javax.swing.JLabel();
        addMagicianButton = new javax.swing.JButton();
        dropMagicianButton = new javax.swing.JButton();
        addHolidayButton = new javax.swing.JButton();
        cancelHolidayButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        customerNameLabel.setText("Enter customer name:");

        magicianComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-Select-" }));

        magicianLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        magicianLabel.setText("Magician");

        holidayComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-Select-" }));

        scheduleButton.setText("Schedule!");
        scheduleButton.setToolTipText("");

        holidayLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        holidayLabel2.setText("Holiday");

        holidayComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-Select-" }));

        magicianStatusButton.setText("Get Status");

        holidayStatusButton.setText("Get Status");

        waitingListButton.setText("Get Waiting List");

        holidayLabel1.setText("Holiday:");

        frameTitleLabel.setFont(new java.awt.Font("Tempus Sans ITC", 1, 24)); // NOI18N
        frameTitleLabel.setText("Magician Agent");
        frameTitleLabel.setName("titleLabel"); // NOI18N

        addMagicianButton.setText("Add Magician..");

        dropMagicianButton.setText("Drop..");

        addHolidayButton.setText("Add Holiday..");

        cancelHolidayButton.setText("Cancel..");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(customerNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(holidayLabel1)
                        .addGap(143, 143, 143))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(frameTitleLabel)
                                .addGap(57, 57, 57))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(customerNameField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(holidayComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(scheduleButton)
                        .addGap(23, 23, 23))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(magicianLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(holidayLabel2)
                        .addGap(119, 119, 119))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(magicianStatusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dropMagicianButton))
                            .addComponent(magicianComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addMagicianButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(holidayStatusButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelHolidayButton))
                            .addComponent(holidayComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addHolidayButton))
                        .addGap(39, 39, 39))))
            .addGroup(layout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addComponent(waitingListButton, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customerNameLabel)
                    .addComponent(holidayLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customerNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(holidayComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(scheduleButton)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(holidayLabel2)
                    .addComponent(magicianLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(magicianComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(holidayComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(magicianStatusButton)
                    .addComponent(holidayStatusButton)
                    .addComponent(cancelHolidayButton)
                    .addComponent(dropMagicianButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addMagicianButton)
                    .addComponent(addHolidayButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(waitingListButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame myFrame = new MainFrame();
                myFrame.setVisible(true);
                myFrame.setResizable(false);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addHolidayButton;
    private javax.swing.JButton addMagicianButton;
    private javax.swing.JButton cancelHolidayButton;
    private javax.swing.JTextField customerNameField;
    private javax.swing.JLabel customerNameLabel;
    private javax.swing.JButton dropMagicianButton;
    private javax.swing.JLabel frameTitleLabel;
    private javax.swing.JComboBox holidayComboBox1;
    private javax.swing.JComboBox holidayComboBox2;
    private javax.swing.JLabel holidayLabel1;
    private javax.swing.JLabel holidayLabel2;
    private javax.swing.JButton holidayStatusButton;
    private javax.swing.JComboBox magicianComboBox;
    private javax.swing.JLabel magicianLabel;
    private javax.swing.JButton magicianStatusButton;
    private javax.swing.JButton scheduleButton;
    private javax.swing.JButton waitingListButton;
    // End of variables declaration//GEN-END:variables
} // end of class MainFrame
