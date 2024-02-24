import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
public class takeInput extends JFrame{
    private JTextField loadBufferField;
    private JTextField storeBufferField;
    private JTextField addSubField;
    private JTextField mulDivField;
    private JTextField DaddaddLatency;
    private JTextField MulLatency;
    private JTextField DivLatency;
    private JTextField LDLatency;
    private JTextField SDLatency;
    private JTextField DsubsubLatency;
    
    Simulator simulator;
    

    public takeInput() {
        setTitle("Enter Sizes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLayout(new GridLayout(11, 2, 10, 10));  // 11 rows, 2 columns, with padding

        loadBufferField = new JTextField(5);
        storeBufferField = new JTextField(5);
        addSubField = new JTextField(5);
        mulDivField = new JTextField(5);
        DaddaddLatency = new JTextField(5);
        MulLatency = new JTextField(5);
        DivLatency = new JTextField(5);
        LDLatency = new JTextField(5);
        SDLatency = new JTextField(5);
        DsubsubLatency = new JTextField(5);

        // Add components to the frame
        add(new JLabel("Enter the load buffer size:"));
        add(loadBufferField);

        add(new JLabel("Enter the store buffer size:"));
        add(storeBufferField);

        add(new JLabel("Enter the Add/Sub reservation station size:"));
        add(addSubField);

        add(new JLabel("Enter the Mul/Div reservation station size:"));
        add(mulDivField);

        add(new JLabel("Enter the DADD/ADD latency:"));
        add(DaddaddLatency);

        add(new JLabel("Enter the DSUB/SUB latency:"));
        add(DsubsubLatency);

        add(new JLabel("Enter the MUL latency:"));
        add(MulLatency);

        add(new JLabel("Enter the DIV latency:"));
        add(DivLatency);
        
        add(new JLabel("Enter the LD latency:"));
        add(LDLatency);

        add(new JLabel("Enter the SD latency:"));
        add(SDLatency);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitData();
            }
        });
        add(submitButton);

        pack(); 
        setLocationRelativeTo(null); // Center the window

    }


        public void submitData() {
            try {
                int loadBufferSize = Integer.parseInt(loadBufferField.getText());
                int storeBufferSize = Integer.parseInt(storeBufferField.getText());
                int addSubSize = Integer.parseInt(addSubField.getText());
                int mulDivSize = Integer.parseInt(mulDivField.getText());
                int addLatency = Integer.parseInt(DaddaddLatency.getText());
                int mulLatency = Integer.parseInt(MulLatency.getText());
                int divLatency = Integer.parseInt(DivLatency.getText());
                int ldLatency = Integer.parseInt(LDLatency.getText());
                int sdLatency = Integer.parseInt(SDLatency.getText());
                int subLatency = Integer.parseInt(DsubsubLatency.getText());
               //  Simulator simulator= new Simulator();
//                simulator.initializeSizes(loadBufferSize, storeBufferSize, addSubSize, mulDivSize);
//                simulator.initializeLatencies(addLatency, mulLatency, divLatency, ldLatency, sdLatency);
             //   System.out.println("ana henaaaa");
                simulator= new Simulator(addLatency, mulLatency, divLatency, ldLatency, sdLatency,subLatency,loadBufferSize, storeBufferSize, addSubSize, mulDivSize);
               
                // Close the input frame
                this.dispose();
                GUI g = new GUI(simulator);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
  

        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  //  Simulator simulator = new Simulator();
                    new takeInput().setVisible(true);
                   
                }
            });
        }
}
