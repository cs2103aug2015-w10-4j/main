package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UI {
	
	private final int displayRowCount = 20;
	private final int displayColumnCount = 40;
	private final int userInputFieldLength = 30;
	private final int promptLength = 10;
	
	private final String frameTitle = "Tasky";
	
	JFrame frame = new JFrame(frameTitle);
	JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
	JTextArea displayArea = new JTextArea(displayRowCount, displayColumnCount);
	JLabel promptLabel = new JLabel("command: ", promptLength);
	JTextField userInputField = new JTextField(userInputFieldLength);
	
	String userFeedback;
	
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
		
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public String promptUser(String prompt) throws InterruptedException {
		promptLabel.setText(prompt);
		userInputField.setEditable(true);
		synchronized (userInputField) {
			while (userInputField.getText().isEmpty()) {
				userInputField.wait();
			}
		}
		return userInputField.getText();
	}
	
	public boolean showToUser(String toShow) {
		displayArea.setText(toShow);
		return true;
	}
	
}
