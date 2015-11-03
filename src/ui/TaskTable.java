package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ui.formatter.FormatterHelper;
import ui.tasktable.TaskTableModel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class TaskTable extends JTable {
	
	private static final Color HEADER_COLOR = new Color(0x443266);
	private static final Color DEFAULT_ROW_COLOR = Color.WHITE;
	private static final Color ALTERNATE_ROW_COLOR = new Color(0xC3C3E5);
	private static final Color DONE_COLOR = Color.GREEN;
	
	private static final String HEADER_FONT_NAME = "SansSerif";
	private static final int HEADER_FONT_STYLE = Font.BOLD;
	private static final int HEADER_FONT_SIZE = 12;
	
	private static final int[] COLUMN_ALIGNMENTS = { SwingConstants.LEFT,
			SwingConstants.LEFT,
			SwingConstants.CENTER,
			SwingConstants.CENTER,
			SwingConstants.CENTER,
			SwingConstants.CENTER
	};
	
	private static final boolean[] SET_MAX_WIDTH = {true, false, false, false, false, false};
	
	private TaskTableModel model = null;
	
	//@@author A0134155M
	public TaskTable(TaskTableModel dm) {
		super(dm);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		assert COLUMN_ALIGNMENTS.length == FormatterHelper.COLUMN_COUNT;
		assert SET_MAX_WIDTH.length == FormatterHelper.COLUMN_COUNT;

		this.model = dm;
		
		prepareTable();
	}
	
	//@@author A0134155M
	public TaskTable(TaskTableModel dm, TableColumnModel cm) {
		super(dm, cm);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		assert COLUMN_ALIGNMENTS.length == FormatterHelper.COLUMN_COUNT;
		assert SET_MAX_WIDTH.length == FormatterHelper.COLUMN_COUNT;

		this.model = dm;
		
		prepareTable();
	}
	
	//@@author A0134155M
	private void prepareTable() {
		prepareTableHeader();
		prepareTableAlignment();
		prepareTableGrid();
		fixColumnWidth();
	}
	
	//@@author A0134155M
	private void fixColumnWidth() {
		TableColumnModel columnModel = getColumnModel();
		int columnCount = columnModel.getColumnCount();
		
		for (int i = 0; i < columnCount; i++) {
			int columnWidth = getColumnWidth(i);
			setColumnWidth(i, columnWidth);
		}
	}
	
	//@@author A0134155M
	private void setColumnWidth(int columnIndex, int columnWidth) {
		TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
		tableColumn.setPreferredWidth(columnWidth);
		
		if (SET_MAX_WIDTH[columnIndex]) {
			tableColumn.setMaxWidth(columnWidth);
		}
	}
	
	//@@author A0134155M
	private int getColumnWidth(int columnIndex) {
		int headerWidth = getHeaderWidth(columnIndex);
		int contentWidth = getContentWidth(columnIndex);
		int resultingWidth = Math.max(headerWidth, contentWidth);
		
		return resultingWidth;
	}
	
	//@@author A0134155M
	private int getContentWidth(int columnIndex) {
		int rowCount = getRowCount();
		
		int maxContentWidth = 0;
		for (int i = 0; i < rowCount; i++) {
			TableCellRenderer tableCellRenderer = getCellRenderer(i, columnIndex);
			Component component = prepareRenderer(tableCellRenderer, i, columnIndex);
			
			int cellPreferredWidth = component.getPreferredSize().width + getIntercellSpacing().width;
			
			maxContentWidth = Math.max(maxContentWidth, cellPreferredWidth);
		}
		
		return maxContentWidth;
	}
	
	//@@author A0134155M
	private int getHeaderWidth(int columnIndex) {
		TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();
		if (renderer == null) {
			renderer = getTableHeader().getDefaultRenderer();
		}
		
		Component component = renderer.getTableCellRendererComponent(this,
				tableColumn.getHeaderValue(), false, false, -1, columnIndex);
		
		return component.getPreferredSize().width;
	}
	
	//@@author A0134155M
	private void prepareTableHeader() {
		JTableHeader tableHeader = getTableHeader();
		tableHeader.setFont(new Font(HEADER_FONT_NAME, HEADER_FONT_STYLE, HEADER_FONT_SIZE));
		tableHeader.setBackground(HEADER_COLOR);
		tableHeader.setForeground(Color.WHITE);
		
	}
	
	//@@author A0134155M
	private void prepareTableGrid() {
		setShowGrid(true);
		setGridColor(Color.LIGHT_GRAY);
	}
	
	//@@author A0134155M
	private void prepareTableAlignment() {
		prepareHeaderAlignment();
		prepareContentAlignment();
	}
	
	//@@author A0134155M
	private void prepareContentAlignment() {
		TableColumnModel tableColumnModel = getColumnModel();
		int columnCount = tableColumnModel.getColumnCount();
		
		for (int i = 0; i < columnCount; i++) {
			TableColumn currentTableColumn = tableColumnModel.getColumn(i);
			
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(COLUMN_ALIGNMENTS[i]);
			
			currentTableColumn.setCellRenderer(renderer);
		}
	}
	
	//@@author A0134155M
	private void prepareHeaderAlignment() {
		TableCellRenderer headerRenderer = tableHeader.getDefaultRenderer();
		JLabel headerLabel = (JLabel) headerRenderer;
		headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	//@@author A0134155M
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		giveColour(c, row, column);
		return c;
	}
	
	//@@author A0134155M
	private void giveColour(Component c, int row, int column) {
		if (row % 2 == 0) {
			c.setBackground(DEFAULT_ROW_COLOR);
		} else {
			c.setBackground(ALTERNATE_ROW_COLOR);
		}
		
		//If the task is done, make it green
		if (model.isTaskDone(row).equals(Boolean.TRUE)) {
			c.setBackground(DONE_COLOR);
		}
	}
}
