package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class UI {
	/*
	 * Declaration of variables
	 */
	private final int displayRowCount = 20;
	private final int displayColumnCount = 40;
	private final int userInputFieldLength = 30;
	private final int promptLength = 10;
	
	private final String frameTitle = "Tasky";
	
	/*
	 * Initialization of GUI variables
	 */
	JFrame frame = new JFrame(frameTitle);
	JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
	JTextArea displayArea = new JTextArea(displayRowCount, displayColumnCount);
	JLabel promptLabel = new JLabel("command: ", promptLength);
	JTextField userInputField = new JTextField(userInputFieldLength);
	
	/*
	 * Constructor
	 */
	public UI() {
		userInputField.setEditable(false);
		displayArea.setEditable(false);
		mainPanel.add(displayArea, BorderLayout.PAGE_START);
		mainPanel.add(promptLabel, BorderLayout.LINE_START);
		mainPanel.add(userInputField, BorderLayout.CENTER);
		
		userInputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (userInputField) {
					userInputField.notify();
				}
			}
		});
		
		/*
		 * Clears userInputField when "esc" is pressed
		 */
		@SuppressWarnings("serial")
		Action clearText = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		        userInputField.setText("");
		    }
		};
		userInputField.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), clearText);
		
		/*
		 * Focus given to userInputField when window is activated
		 */
		frame.addWindowListener(new WindowAdapter(){
	        public void windowActivated(WindowEvent e) {
	        	userInputField.grabFocus();
	        }
		});
		
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Prompt message and obtain user input
	 * @param prompt message to prompt user
	 * @return userInput
	 * @throws InterruptedException
	 */
	public String promptUser(String prompt) throws InterruptedException {
		promptLabel.setText(prompt);
		userInputField.setEditable(true);
		userInputField.grabFocus();
		synchronized (userInputField) {
			while (userInputField.getText().isEmpty()) {
				userInputField.wait();
			}
		}
		String userInput = userInputField.getText();
		userInputField.setText("");
		return userInput;
	}
	
	public boolean showToUser(String toShow) {
		displayArea.setText(toShow);
		return true;
	}
	
}
