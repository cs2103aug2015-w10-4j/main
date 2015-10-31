package ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import global.Task;
import ui.formatter.FormatterHelper;
import ui.formatter.TextFormatter;
import ui.tasktable.TaskTableModel;

public class UI {
	
	/*
	 * Roughly, the UI can be modeled as the following grid :
	 * 
	 * +---+---+---+
	 * | 1 | 2 | 3 |
	 * +---+---+---+
	 * | 4 | 5 | 6 |
	 * +---+---+---+
	 * | 7 | 8 | 9 |
	 * +---+---+---+
	 * 
	 * where :
	 *  > displayArea occupies cell 1-3
	 *  > statusBar occupies cell 4-6
	 *  > promptLabel occupies cell 7
	 *  > userInputField occupies cell 8-9
	 */
	
	/*
	 * Declaration of variables
	 */
	private Logger logger = Logger.getGlobal();
	
	private static final int DEFAULT_WIDTH = 480;
	private static final int DEFAULT_HEIGHT = 320;
	private static final int USER_INPUT_FIELD_CHAR_COUNT = 50;
	private static final int PROMPT_LABEL_CHAR_COUNT = 10;
	
	private static final int DISPLAY_AREA_POS_Y = 0;
	private static final int DISPLAY_AREA_POS_X = 0;
	private static final int DISPLAY_AREA_LEN_Y = 1;
	private static final int DISPLAY_AREA_LEN_X = 3;
	
	private static final int PROMPT_LABEL_POS_Y = 2;
	private static final int PROMPT_LABEL_POS_X = 0;
	private static final int PROMPT_LABEL_LEN_Y = 1;
	private static final int PROMPT_LABEL_LEN_X = 1;
	
	private static final int USER_INPUT_FIELD_POS_Y = 2;
	private static final int USER_INPUT_FIELD_POS_X = 1;
	private static final int USER_INPUT_FIELD_LEN_Y = 1;
	private static final int USER_INPUT_FIELD_LEN_X = 2;
	
	private static final int STATUS_BAR_POS_Y = 1;
	private static final int STATUS_BAR_POS_X = 0;
	private static final int STATUS_BAR_LEN_Y = 1;
	private static final int STATUS_BAR_LEN_X = 3;
	
	private static final int INVISIBLE_JPANEL_WIDTH = 100;
	private static final int INVISIBLE_JPANEL_HEIGHT = 20;
	
	private static final String FRAME_TITLE = "Tasky";
	private static final String DEFAULT_PROMPT = "command ";
	private static final String DISPLAY_AREA_FONT_NAME = "Lucida Console";
	private static final int DISPLAY_AREA_FONT_STYLE = Font.PLAIN;
	private static final int DISPLAY_AREA_FONT_SIZE = 12;
	
	private static final int MAXIMUM_COLUMN_WIDTH = 30;
	
	private static final String EMPTY_STRING = "";
	
	private static final int SCROLL_SPEED = 10;
	
	public enum DisplayType {
		DEFAULT, FILTERED
	}
	
	/*
	 * Initialization of GUI variables
	 */
	private JFrame frame = new JFrame(FRAME_TITLE);
	private JPanel displayAreaPanel = new JPanel();
	private JScrollPane displayAreaScrollPane = new JScrollPane(displayAreaPanel);
	private JLabel promptLabel = new JLabel(DEFAULT_PROMPT, PROMPT_LABEL_CHAR_COUNT);
	private JTextField userInputField = new JTextField(USER_INPUT_FIELD_CHAR_COUNT);
	private StatusBar statusBar = new StatusBar();
	
	private TextFormatter taskListFormatter = new TextFormatter();
	private UserInputHistory userInputHistory = new UserInputHistory();
	
	private static final boolean useJTable = true;
	
	/*
	 * Constructor
	 */
	public UI() {
		prepareComponents();
		addComponentsToPane(frame.getContentPane());
		displayFrame();
	}
	
	private void addComponentsToPane(Container contentPane) {
		addDisplayAreaScrollPane(contentPane);
		addPromptLabel(contentPane);
		addUserInputField(contentPane);
		addStatusBar(contentPane);
	}

	private void addStatusBar(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = STATUS_BAR_POS_X;
		constraint.gridy = STATUS_BAR_POS_Y;
		constraint.gridheight = STATUS_BAR_LEN_Y;
		constraint.gridwidth = STATUS_BAR_LEN_X;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d", "STATUS_BAR", "GridBagConstraints.HORIZONTAL",
				STATUS_BAR_POS_X, STATUS_BAR_POS_Y, STATUS_BAR_LEN_Y, STATUS_BAR_LEN_X));
	
		contentPane.add(statusBar, constraint);
	}

	private void addUserInputField(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = USER_INPUT_FIELD_POS_X;
		constraint.gridy = USER_INPUT_FIELD_POS_Y;
		constraint.gridheight = USER_INPUT_FIELD_LEN_Y;
		constraint.gridwidth = USER_INPUT_FIELD_LEN_X;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d", "USER_INPUT_FIELD", "GridBagConstraints.HORIZONTAL",
				USER_INPUT_FIELD_POS_X, USER_INPUT_FIELD_POS_Y,
				USER_INPUT_FIELD_LEN_Y, USER_INPUT_FIELD_LEN_X));
		
		contentPane.add(userInputField, constraint);
	}

	private void addPromptLabel(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridx = PROMPT_LABEL_POS_X;
		constraint.gridy = PROMPT_LABEL_POS_Y;
		constraint.gridheight = PROMPT_LABEL_LEN_Y;
		constraint.gridwidth = PROMPT_LABEL_LEN_X;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d", "PROMPT_LABEL", "GridBagConstraints.HORIZONTAL",
				PROMPT_LABEL_POS_X, PROMPT_LABEL_POS_Y, PROMPT_LABEL_LEN_Y, PROMPT_LABEL_LEN_X));
		
		contentPane.add(promptLabel, constraint);
	}

	private void addDisplayAreaScrollPane(Container contentPane) {
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = GridBagConstraints.BOTH;
		constraint.gridx = DISPLAY_AREA_POS_X;
		constraint.gridy = DISPLAY_AREA_POS_Y;
		constraint.gridheight = DISPLAY_AREA_LEN_Y;
		constraint.gridwidth = DISPLAY_AREA_LEN_X;
		constraint.weightx = 1.0;
		constraint.weighty = 1.0;
		
		logger.log(Level.CONFIG, String.format("%s: fill = %s, gridx = %d, gridy = %d, "
				+ "gridheight = %d, gridwidth = %d, weightx = %.2f, weighty = %.2f",
				"DISPLAY_AREA", "GridBagConstraints.HORIZONTAL",
				STATUS_BAR_POS_X, STATUS_BAR_POS_Y, STATUS_BAR_LEN_Y, STATUS_BAR_LEN_X, 1.0, 1.0));
		
		contentPane.add(displayAreaScrollPane, constraint);
	}

	private void displayFrame() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void prepareComponents() {
		prepareFrame();
		prepareUserInput();
		prepareDisplayAreaPanel();
		prepareDisplayAreaScrollPane();
		preparePromptLabel();
	}
	
	private void preparePromptLabel() {
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}

	private void prepareDisplayAreaScrollPane() {
		displayAreaScrollPane.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	private void prepareDisplayAreaPanel() {
		displayAreaPanel.setLayout(new VerticalLayout());
		displayAreaPanel.setBackground(Color.WHITE);
	}

	private void prepareUserInput() {
		userInputField.setEditable(false);
		userInputField.setColumns(USER_INPUT_FIELD_CHAR_COUNT);
		
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
		
		
		@SuppressWarnings("serial")
		Action lastText = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		    	String userInputFromHistory = userInputHistory.moveUpInHistory();
		    	userInputField.setText(userInputFromHistory);
		    }
		};
		userInputField.getInputMap().put(KeyStroke.getKeyStroke("UP"), lastText);
		
		@SuppressWarnings("serial")
		Action nextText = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		    	String userInputFromHistory = userInputHistory.moveDownInHistory();
		    	userInputField.setText(userInputFromHistory);
		    }
		};
		userInputField.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), nextText);
		
		@SuppressWarnings("serial")
		Action scrollDown = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JViewport viewPort = displayAreaScrollPane.getViewport();
				Point position = viewPort.getViewPosition();
				movePosition(viewPort, position, 0, SCROLL_SPEED);
				viewPort.setViewPosition(position);
			}
		};
		userInputField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), scrollDown);
		
		@SuppressWarnings("serial")
		Action scrollUp = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JViewport viewPort = displayAreaScrollPane.getViewport();
				Point position = viewPort.getViewPosition();
				movePosition(viewPort, position, 0, -SCROLL_SPEED);
				viewPort.setViewPosition(position);
			}
		};
		userInputField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), scrollUp);
		
	}
	
	private void movePosition(JViewport viewPort, Point position, int dx, int dy) {
		position.x += dx;
		position.y += dy;
		
		position.x = Math.max(position.x, 0);
		position.x = Math.min(position.x, getViewPortMaxX(viewPort));
		
		position.y = Math.max(position.y, 0);
		position.y = Math.min(position.y, getViewPortMaxY(viewPort));
	}
	
	private int getViewPortMaxX(JViewport viewPort) {
		return viewPort.getView().getWidth() - viewPort.getWidth();
	}
	
	private int getViewPortMaxY(JViewport viewPort) {
		return viewPort.getView().getHeight() - viewPort.getHeight();
	}

	private void prepareFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		
		/*
		 * Focus given to userInputField when window is activated
		 */
		frame.addWindowListener(new WindowAdapter() {
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
		logger.info("Entering promptUser(prompt = " + prompt + ")");
		
		prepareComponentForUserInput(prompt);
		waitForUserInput();
		sanitizeUserInput();
		
		String userInput = getUserInput();
		
		userInputHistory.addToHistory(userInput);
		cleanUserInputField();
		
		logger.info("Returning from promptUser");
		
		return userInput;
	}

	private void cleanUserInputField() {
		userInputField.setText(EMPTY_STRING);
	}

	private String getUserInput() {
		return userInputField.getText();
	}
	
	private void sanitizeUserInput() {
		String userInput = userInputField.getText();
		userInput = userInput.replaceAll("\t", " ");
		userInputField.setText(userInput);
	}

	private void prepareComponentForUserInput(String prompt) {
		promptLabel.setText(prompt);
		userInputField.setEditable(true);
		userInputField.grabFocus();
	}

	private void waitForUserInput() throws InterruptedException {
		synchronized (userInputField) {
			while (userInputField.getText().isEmpty()) {
				userInputField.wait();
			}
		}
	}
	
	/**
	 * Asks the UI to display content to user
	 * @param stringToShow
	 * @return true if successful
	 */
	public boolean showToUser(String stringToShow) {
		JTextArea textArea = createJTextAreaWithMonospaceFont();
		textArea.setText(stringToShow);

		displayAreaPanel.removeAll();
		displayAreaPanel.add(textArea);
		displayAreaPanel.revalidate();
		displayAreaPanel.repaint();

		return true;
	}
	
	private boolean showToUserFilteredTable(TaskTableModel[] tableModels, List<String> filters) {
		displayAreaPanel.removeAll();

		VerticalLayout displayAreaPanelLayout = (VerticalLayout) displayAreaPanel.getLayout();
		displayAreaPanelLayout.resetLayout();
		
		for (String filter : filters) {
			JLabel label = new JLabel(filter);
			displayAreaPanel.add(label);
		}
		
		for (int i = 0; i < tableModels.length; i++) {
			TaskTable currentTable = new TaskTable(tableModels[i]);
			currentTable.setFocusable(false);
			currentTable.setRowSelectionAllowed(false);
			
			displayAreaPanel.add(currentTable.getTableHeader());
			displayAreaPanel.add(currentTable);
			displayAreaPanel.add(createInvisibleJPanel(INVISIBLE_JPANEL_WIDTH,
					INVISIBLE_JPANEL_HEIGHT));
		}
		
		displayAreaPanel.revalidate();
		displayAreaPanel.repaint();

		return true;
	}
	
	private boolean showToUserDefaultTable(TaskTableModel[] tableModels, List<String> titles) {
		assert tableModels.length <= titles.size();
		displayAreaPanel.removeAll();

		VerticalLayout displayAreaPanelLayout = (VerticalLayout) displayAreaPanel.getLayout();
		displayAreaPanelLayout.resetLayout();
		
		for (int i = 0; i < tableModels.length; i++) {
			TaskTable currentTable = new TaskTable(tableModels[i]);
			currentTable.setFocusable(false);
			currentTable.setRowSelectionAllowed(false);
			
			JLabel titleLabel = new JLabel(titles.get(i));

			displayAreaPanel.add(titleLabel);
			displayAreaPanel.add(currentTable.getTableHeader());
			displayAreaPanel.add(currentTable);
			displayAreaPanel.add(createInvisibleJPanel(INVISIBLE_JPANEL_WIDTH,
					INVISIBLE_JPANEL_HEIGHT));
		}
		
		displayAreaPanel.revalidate();
		displayAreaPanel.repaint();

		return true;
	}
	
	private JPanel createInvisibleJPanel(int width, int height) {
		JPanel invisiblePanel = new JPanel();
		invisiblePanel.setSize(new Dimension(width, height));
		invisiblePanel.setOpaque(false);
		return invisiblePanel;
	}

	private JTextArea createJTextAreaWithMonospaceFont() {
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font(DISPLAY_AREA_FONT_NAME, DISPLAY_AREA_FONT_STYLE,
				DISPLAY_AREA_FONT_SIZE));
		return textArea;
	}

	/**
	 * Asks the UI to display the list of tasks
	 * @param tasks
	 * @return true if successful
	 */
	public boolean showTasks(List<Task> tasks, DisplayType displayType, List<String> titles) {
		int minRowCount = -1;
		if (displayType == DisplayType.DEFAULT) {
			minRowCount = 3;
		} else {
			minRowCount = 1;
		}

		Object[][][] taskListsData = FormatterHelper.getTaskListData(tasks,
				displayType == DisplayType.DEFAULT, minRowCount);
		assert taskListsData != null;
		if (!useJTable) {
			//TODO: fix set of strings
			String formattedTaskList = taskListFormatter.formatTaskList(taskListsData,
					MAXIMUM_COLUMN_WIDTH);
			return showToUser(formattedTaskList);
		} else {
			TaskTableModel[] tableModels = new TaskTableModel[taskListsData.length];

			for (int taskListIndex = 0; taskListIndex < taskListsData.length; taskListIndex++) {
				Object[][] currentTaskListData = taskListsData[taskListIndex];
				tableModels[taskListIndex] = new TaskTableModel(currentTaskListData);
			}

			if (displayType == DisplayType.FILTERED) {
				return showToUserFilteredTable(tableModels, titles);
			} else {
				return showToUserDefaultTable(tableModels, titles);
			}
		}
	}
	
	/**
	 * Asks the UI to display content to user in the status bar
	 * @param stringToShow
	 * @return true if successful
	 */
	public boolean showStatusToUser(String stringToShow) {
		logger.info("Entering showStatusToUser(stringToShow=" + stringToShow + ")");
		
		statusBar.setText(stringToShow);
		
		logger.info("Returning from showStatusToUser");
		
		return true;
	}
}
