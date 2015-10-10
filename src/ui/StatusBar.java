package ui;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {
	
	private static final String DEFAULT_STATUS_BAR_TEXT = "Tasky is ready.";
	private JLabel statusLabel;
	
	public StatusBar() {
		super(new FlowLayout(FlowLayout.LEFT));
		initializeLabel();
		initializeStatusBar();
	}

	private void initializeStatusBar() {
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusLabel);
		setPreferredSize(getPreferredSize());
	}

	private void initializeLabel() {
		statusLabel = new JLabel(DEFAULT_STATUS_BAR_TEXT);
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.PLAIN, 11));
	}
	
	public void setText(String text) {
		statusLabel.setText(text);
	}
	
}
