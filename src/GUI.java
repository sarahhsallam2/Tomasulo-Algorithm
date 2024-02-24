import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame{

    Simulator simulator;
    private JFrame frame;
    private JTextArea registerFile;
    private JTextArea dataMemory;
    private JTextArea SimulatorQueue;
    private JTextArea loadBuffer;
    private JTextArea storeBuffer;
    private JTextArea AddRS;
    private JTextArea MultRS;
    private JTextArea InstructionQueue;
    private JTextArea stepByStepQueue;
    private JButton doneButton;
    private JButton nextButton;
    private JButton startButton;
    private JButton prevButton;
    private JButton finalResult;
    public int j=0;
    public int i=0;
    public GUI(Simulator simulator) {
        this.simulator = simulator;
       // simulator = new Simulator();
        createAndShowGUI();
    }
    public void createAndShowGUI() {
        // Create and set up the window
        frame = new JFrame("Simulator GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 900));

        registerFile = new JTextArea(10, 40);
        dataMemory = new JTextArea(10, 40);
        SimulatorQueue = new JTextArea(10, 40);
        loadBuffer = new JTextArea(10, 40);
        storeBuffer = new JTextArea(10, 40);
        AddRS = new JTextArea(10, 40);
        MultRS = new JTextArea(10, 40);
        InstructionQueue = new JTextArea(10, 40);
        stepByStepQueue= new JTextArea(10, 40);


        JTabbedPane tabbedPane = new JTabbedPane();
        JTextArea[] textAreas = {registerFile, dataMemory, SimulatorQueue, loadBuffer, storeBuffer, AddRS, MultRS, InstructionQueue};
        for (JTextArea textArea : textAreas) {
            textArea.setEditable(false);
        }
        for(int i =0;i<textAreas.length;i++) {
        	textAreas[i].setEditable(false);
        }
        
        // Memory and Register File panel
        JPanel memoryPanel = new JPanel();
        memoryPanel.setLayout(new BoxLayout(memoryPanel, BoxLayout.Y_AXIS));
        memoryPanel.add(new JScrollPane(registerFile));
        memoryPanel.add(new JScrollPane(dataMemory));
        tabbedPane.addTab("Data", memoryPanel);
//     // Create the first panel with a label and scroll pane
//      JPanel bufferPanel = new JPanel();
//
//        JPanel loadPanel = new JPanel();
//        loadPanel.setLayout(new BoxLayout(loadPanel, BoxLayout.Y_AXIS));
//        loadPanel.add(new JLabel("Load Buffer"));
//        loadPanel.add(new JScrollPane(loadBuffer));
//
//        // Create the second panel with a label and scroll pane
//        JPanel storePanel = new JPanel();
//        storePanel.setLayout(new BoxLayout(storePanel, BoxLayout.Y_AXIS));
//        storePanel.add(new JLabel("Store Buffer"));
//        storePanel.add(new JScrollPane(storeBuffer));
//
//        // Add these panels to the bufferPanel
//        bufferPanel.add(loadPanel);
//        bufferPanel.add(storePanel);
//
//        // Add the bufferPanel to the tabbedPane
//        tabbedPane.addTab("Buffer", bufferPanel);

//        // Buffer panel
        JPanel bufferPanel = new JPanel();
        bufferPanel.setLayout(new BoxLayout(bufferPanel, BoxLayout.Y_AXIS));
        bufferPanel.add(new JScrollPane(loadBuffer));
        bufferPanel.add(new JScrollPane(storeBuffer));
        tabbedPane.addTab("Buffer", bufferPanel);

        // ReservationStations panel
        JPanel resStationsPanel = new JPanel();
        resStationsPanel.setLayout(new BoxLayout(resStationsPanel, BoxLayout.Y_AXIS));
        resStationsPanel.add(new JScrollPane(AddRS));
        resStationsPanel.add(new JScrollPane(MultRS));
        tabbedPane.addTab("Reservation Stations", resStationsPanel);

        // Instruction panel
        JPanel instructionQueuePanel = new JPanel();
        instructionQueuePanel.setLayout(new BoxLayout(instructionQueuePanel, BoxLayout.Y_AXIS));
        instructionQueuePanel.add(new JScrollPane(InstructionQueue));
        tabbedPane.addTab("Instruction Queue", instructionQueuePanel);

        // Instruction panel
        JPanel stepByStepPanel = new JPanel();
        stepByStepPanel.setLayout(new BoxLayout(stepByStepPanel, BoxLayout.Y_AXIS));
        stepByStepPanel.add(new JScrollPane(stepByStepQueue));
        tabbedPane.addTab("Cycle By Cycle ", stepByStepPanel);

        // Button panel
        JPanel buttonPanel = new JPanel();
         startButton = new JButton("Start Simulation");
//        startButton.addActionListener(e -> runSimulation());
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exit the application§
            	//runSimulation();
            	JOptionPane.showMessageDialog(null, "Please press on Cycle By Cycle button to view the steps of Algorithm", "Instructions", JOptionPane.INFORMATION_MESSAGE);

            	initializeSimulation();
                stepByStepQueue.setCaretPosition(0);

            }

        });
//        textArea.setBackground(new Color(200,162,180));

        finalResult = new JButton("Final Result");
       // Add action listeners to buttons if needed
        finalResult.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               // Exit the application
           	JOptionPane.showMessageDialog(null, "Please press on any of the buttons to view the final results of Algorithm", "Instructions", JOptionPane.INFORMATION_MESSAGE);

        	   runSimulation();
           }
       });
        
        doneButton = new JButton("Done");
        // Add action listeners to buttons if needed
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exit the application
            	JLabel label = new JLabel("Thank you :)))");
            	label.setForeground(new Color(200,162,180));  // Set the text color to red (or any other color you prefer)

            	// Show the message dialog with the custom label
            	JOptionPane.showMessageDialog(null, label, "Thank you message", JOptionPane.PLAIN_MESSAGE);
//            	JOptionPane.showMessageDialog(null, "Thank you :)))", "Thank you message", JOptionPane.PLAIN_MESSAGE);

                System.exit(0);
            }
        });
     // Inside the createAndShowGUI() method in the GUI class

         nextButton = new JButton("Next Cycle");
        // while (i<simulator.GUIPrinting.size()) {
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //	performNextCycle() ; // i have I and will set the texts here then updated GUI by calling what is inside the updateGUI method and call the GUIPrinting arrayList wirh index of I
            	//registerFile.setText(simulator.PrintRegFile());
            	if(i<simulator.GUIPrinting.size()) {
            		stepByStepQueue.setText(simulator.GUIPrinting.get(i));
                i++;
            	}
            	else {

            	JOptionPane.showMessageDialog(frame, "You have reached the end of the cycles, but you can view final result", "You are done :)", JOptionPane.ERROR_MESSAGE);
            	}
                stepByStepQueue.setCaretPosition(0);

            }
        });
        
        prevButton = new JButton("Previous Cycle");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //	performNextCycle() ; // i have I and will set the texts here then updated GUI by calling what is inside the updateGUI method and call the GUIPrinting arrayList wirh index of I
            	//registerFile.setText(simulator.PrintRegFile());
            	if(i>0) {
            		i--;
            		i--;
            		if(i>=0) {
            		
                		stepByStepQueue.setText(simulator.GUIPrinting.get(i++));

            		}
            		else {
            			i++;
            			i++;
            		}
            	}
            	else {
            		i++;
            		i++;
            		
            	}
                stepByStepQueue.setCaretPosition(0);

            }

        });
        buttonPanel.add(startButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(finalResult);
        buttonPanel.add(doneButton);
        // Add components to the frame
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Display the window
        frame.pack();
        setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);

    }

//    private void createAndShowGUI() {
//        // Create and set up the window
//        frame = new JFrame("Simulator GUI");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//       // frame.setLayout(new BorderLayout());
//        setPreferredSize(new Dimension(800, 800));
//
//        // Create text areas
//        registerFile = new JTextArea(10, 30);
//        dataMemory = new JTextArea(10, 30);
//        SimulatorQueue = new JTextArea(10, 30);
//        loadBuffer = new JTextArea(10, 30);
//        storeBuffer = new JTextArea(10, 30);
//        AddRS = new JTextArea(10, 30);
//        MultRS = new JTextArea(10, 30);
//        InstructionQueue = new JTextArea(10, 30);
//
//
////        // Set text areas as non-editable
//        registerFile.setEditable(false);
//        dataMemory.setEditable(false);
//        SimulatorQueue.setEditable(false);
//        loadBuffer.setEditable(false);
//        storeBuffer.setEditable(false);
//        AddRS.setEditable(false);
//        MultRS.setEditable(false);
//        InstructionQueue.setEditable(false);
//
//
////        // Scroll panes for text areas
//        JScrollPane regFileScrollPane = new JScrollPane(registerFile);
//        JScrollPane dataMemoryScrollPane = new JScrollPane(dataMemory);
//        JScrollPane printStatementsScrollPane = new JScrollPane(SimulatorQueue);
//        JScrollPane loadBufferScrollPane = new JScrollPane(loadBuffer);
//        JScrollPane storeBufferScrollPane = new JScrollPane(storeBuffer);
//        JScrollPane addRSScrollPane = new JScrollPane(AddRS);
//        JScrollPane MultRSScrollPane = new JScrollPane(MultRS);
//        JScrollPane InstructionQueueScrollPane = new JScrollPane(InstructionQueue);
//        
//
//        // Button to trigger simulation
//        JButton startButton = new JButton("Start Simulation");
//        startButton.addActionListener(e -> runSimulation());
//        
//        JButton button = new JButton("Done");
//        setLayout(new BorderLayout());
//        add(button, BorderLayout.SOUTH);
//       // add(textArea, BorderLayout.NORTH);
////        add(scrollPane, flowLayout.CENTER);
//        
////        // Adding components to the frame
////        frame.add(regFileScrollPane);
////        frame.add(dataMemoryScrollPane);
////        frame.add(printStatementsScrollPane);
////        frame.add(loadBufferScrollPane);
////        frame.add(storeBufferScrollPane);
////        frame.add(addRSScrollPane);
////        frame.add(MultRSScrollPane);
////        frame.add(InstructionQueueScrollPane);
////        frame.add(startButton, BorderLayout.SOUTH);
//
//        // Display the window
//        frame.pack();
//        frame.setVisible(true);
//    }
    private void initializeSimulation() {
        // Any initialization needed for the simulation
        startButton.setEnabled(false);
    		stepByStepQueue.setText(simulator.GUIPrinting.get(0));
        i++;
    	
        nextButton.setEnabled(true);
    }

    private void performNextCycle() {
        simulator.run();  // Execute a single cycle
        updateGUI();  // Update the GUI after each cycle

        // Optionally, disable nextButton if the simulation has ended
        if (!simulator.continueExecution()) {
            nextButton.setEnabled(false);
        }
    }
    public void runSimulation() {
        // Run the simulator

        simulator.run();
//        System.out.println("anaaaaaaa");
//        System.out.println(simulator.GUIPrinting);

        // Update GUI components
        updateGUI();
    }
    public void updateGUI() {
        // Set text areas with updated data
        registerFile.setText(simulator.PrintRegFile());
        dataMemory.setText(simulator.PrintDataMemory());
        SimulatorQueue.setText(simulator.PrintSimulatorQueue()); 
        storeBuffer.setText(simulator.PrintStoreBuffer());
        loadBuffer.setText(simulator.PrintLoadBuffer());
        MultRS.setText(simulator.PrintMultResStation());
        AddRS.setText(simulator.PrintAddResStation());
        InstructionQueue.setText(simulator.PrintSimulatorQueue());
        
        registerFile.setCaretPosition(0);
        dataMemory.setCaretPosition(0);
        SimulatorQueue.setCaretPosition(0);
        storeBuffer.setCaretPosition(0);
        loadBuffer.setCaretPosition(0);
        MultRS.setCaretPosition(0);
        AddRS.setCaretPosition(0);
        InstructionQueue.setCaretPosition(0);


    }

//    public static void main(String[] args) {
//        // Schedule a job for the event dispatch thread
//        SwingUtilities.invokeLater(() -> new GUI());
//    }
}