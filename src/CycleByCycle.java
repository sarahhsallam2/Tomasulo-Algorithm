import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
public class CycleByCycle extends JFrame {
	// TODO Auto-generated constructor stub
    private JTextArea textArea;
    private int counter;
    private ArrayList<String> saved= new ArrayList<String>();
private Simulator sim;
	public CycleByCycle(Simulator simulator) {

	    	this.sim = simulator;
	    	 setTitle("Pipeline Execution Output");
	         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	         setPreferredSize(new Dimension(600, 400));

	         textArea = new JTextArea();
	         textArea.setEditable(false);
	         textArea.setBackground(new Color(200,162,180));
	         textArea.setForeground(Color.black);
	         textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

	         JScrollPane scrollPane = new JScrollPane(textArea);
	         getContentPane().setBackground(Color.DARK_GRAY);
	         add(scrollPane);
	         
	         JButton button = new JButton("Done");
	         setLayout(new BorderLayout());
	         add(button, BorderLayout.SOUTH);
	        // add(textArea, BorderLayout.NORTH);
	         add(scrollPane, BorderLayout.CENTER);
	         
	         
	         
	         button.addActionListener(new ActionListener() {
	             @Override
	             public void actionPerformed(ActionEvent e) {
	                 // Exit the application
	                 System.exit(0);
	             }
	         });
	         

	         pack();
	         setLocationRelativeTo(null);

	        // Redirect console output to the text area
	        PrintStream printStream = new PrintStream(new ConsoleOutputStream(textArea));
	        System.setOut(printStream);
	        System.setErr(printStream);
	    }

		public void printGUI() {
			
			for(int i =0;i<sim.GUIPrinting.size();i++) {
				saved.add(sim.GUIPrinting.get(i));


		}
			textArea.setText(saved.get(0));

		}

	

	    // Custom OutputStream to redirect console output to the text area
	    private static class ConsoleOutputStream extends OutputStream {
	        private final JTextArea textArea;

	        public ConsoleOutputStream(JTextArea textArea) {
	            this.textArea = textArea;
	        }

	        @Override
	        public void write(int b) {
	            SwingUtilities.invokeLater(() -> textArea.append(String.valueOf((char) b)));
	        }
	    }
	}



