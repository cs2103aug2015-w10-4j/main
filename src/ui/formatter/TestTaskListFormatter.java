package ui.formatter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import global.Task;

//@@author A0134155M
public class TestTaskListFormatter {

	private static final int ONE_BILLION = (int) 1e9;
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	private TextFormatter formatter;
	private Calendar timeForTesting;

	@Before
	public void setup(){
		formatter = new TextFormatter();
		timeForTesting = new GregorianCalendar();
		timeForTesting.set(2020, 11, 3, 9, 0);
	}
	
	/*
	 * Test formatter with infinite width available
	 */
	@Test
	public void testInfiniteWidth() {
		List<Task> taskList = new ArrayList<>();
		taskList.add(new Task("Task with only description"));
		taskList.add(new Task("Task with 2 dates", timeForTesting, timeForTesting));
		
		Object[][][] taskListData = FormatterHelper.getTaskListData(taskList, false, 1, 0);

		String result = formatter.formatTaskList(taskListData, null, ONE_BILLION);
		String expected = "+-----+--------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + "| No. |       Description        | Starting Time  |  Ending Time   | Location |   Status    |" + NEW_LINE
				        + "+-----+--------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + "|1    |Task with only description|                |                |          |Not done yet.|" + NEW_LINE
				        + "+-----+--------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + "|2    |Task with 2 dates         |03/12/20 9:00 AM|03/12/20 9:00 AM|          |Not done yet.|" + NEW_LINE
				        + "+-----+--------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + NEW_LINE;
		assertEquals(expected, result);
	}
	
	/*
	 * Test formatter with limited width (30 characters per line)
	 */
	@Test
	public void testLimitedWidth() {
		List<Task> taskList = new ArrayList<>();
		taskList.add(new Task("Task with only description"));
		taskList.add(new Task("Task with 2 dates", timeForTesting, timeForTesting));
		taskList.add(new Task("Task with a very long long long long description with location and date",
				timeForTesting, "NUS SoC"));
		
		Object[][][] taskListData = FormatterHelper.getTaskListData(taskList, false, 1, 0);

		String result = formatter.formatTaskList(taskListData, null, 30);
		String expected = "+-----+------------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + "| No. |         Description          | Starting Time  |  Ending Time   | Location |   Status    |" + NEW_LINE
				        + "+-----+------------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + "|1    |Task with only description    |                |                |          |Not done yet.|" + NEW_LINE
				        + "+-----+------------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + "|2    |Task with 2 dates             |03/12/20 9:00 AM|03/12/20 9:00 AM|          |Not done yet.|" + NEW_LINE
				        + "+-----+------------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + "|3    |Task with a very long long lon|                |03/12/20 9:00 AM|NUS SoC   |Not done yet.|" + NEW_LINE
				        + "|     |g long description with locati|                |                |          |             |" + NEW_LINE
				        + "|     |on and date                   |                |                |          |             |" + NEW_LINE
				        + "+-----+------------------------------+----------------+----------------+----------+-------------+" + NEW_LINE
				        + NEW_LINE;
		assertEquals(expected, result);
	}
	
	@After
	public void cleanup() {
	
	}

}
