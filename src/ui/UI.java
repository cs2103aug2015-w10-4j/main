package ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class UI {
	/*
	 * Declaration of variables
	 */
	private static final int DISPLAY_ROW_COUNT = 30;
	private static final int DISPLAY_COLUMN_COUNT = 70;
	private static final int USER_INPUT_FIELD_LENGTH = 60;
	private static final int PROMPT_LENGTH = 10;
	
	private static final String FRAME_TITLE = "Tasky";
	private static final String DEFAULT_PROMPT = "command ";
	
	/*
	 * Initialization of GUI variables
	 */
	private JFrame frame = new JFrame(FRAME_TITLE);
	private JTextArea displayArea = new JTextArea(DISPLAY_ROW_COUNT, DISPLAY_COLUMN_COUNT);
	private JLabel promptLabel = new JLabel(DEFAULT_PROMPT, PROMPT_LENGTH);
	private JTextField userInputField = new JTextField(USER_INPUT_FIELD_LENGTH);
	private StatusBar statusBar = new StatusBar();
	
	/*
	 * Constructor
	 */
	public UI() {
		prepareComponents();
		addComponentsToPane(frame.getContentPane());
		displayFrame();
	}
	
	private void addComponentsToPane(Container contentPane) {
		setLayout(contentPane);
		addDisplayArea(contentPane);
		addPromptLabel(contentPane);
		addUserInputField(contentPane);
		addStatusBar(contentPane);
	}

	private void addStatusBar(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = 0;
		constraint.gridy = 2;
		constraint.gridheight = 1;
		constraint.gridwidth = 3;
		constraint.weightx = 1.0;
	
		contentPane.add(statusBar, constraint);
	}

	private void addUserInputField(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = 1;
		constraint.gridy = 1;
		constraint.gridheight = 1;
		constraint.gridwidth = 2;
		constraint.weightx = 1.0;
		
		contentPane.add(userInputField, constraint);
	}

	private void addPromptLabel(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = 0;
		constraint.gridy = 1;
		constraint.gridheight = 1;
		constraint.gridwidth = 1;
		constraint.weightx = 1.0;
		
		contentPane.add(promptLabel, constraint);
	}

	private void addDisplayArea(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.BOTH;
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.gridheight = 1;
		constraint.gridwidth = 3;
		constraint.weightx = 1.0;
		
		contentPane.add(displayArea, constraint);
	}

	private void setLayout(Container contentPane) {
		contentPane.setLayout(new GridBagLayout());
	}

	private void displayFrame() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void prepareComponents() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		userInputField.setEditable(false);
		displayArea.setEditable(false);
		displayArea.setPreferredSize(new Dimension(DISPLAY_ROW_COUNT, DISPLAY_COLUMN_COUNT));
		
		userInputField.setColumns(USER_INPUT_FIELD_LENGTH);
		
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
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
	
	/**
	 * Asks the UI to display content to user
	 * @param stringToShow
	 * @return true if successful
	 */
	public boolean showToUser(String stringToShow) {
		displayArea.setText(stringToShow);
		return true;
	}
	
	public boolean showStatusToUser(String stringToShow) {
		statusBar.setText(stringToShow);
		return true;
	}
	
}
