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

public class TestTaskListFormatter {

	private static final int ONE_BILLION = (int) 1e9;
	
	private TaskListFormatter formatter;
	private Calendar timeForTesting;

	@Before
	public void setup(){
		formatter = new TaskListFormatter();
		timeForTesting = new GregorianCalendar();
		timeForTesting.set(2020, 11, 3);
	}
	
	/*
	 * Test formatter with infinite width available
	 */
	@Test
	public void testInfiniteWidth() {
		List<Task> taskList = new ArrayList<>();
		taskList.add(new Task("Task with only description"));
		taskList.add(new Task("Task with 2 dates", timeForTesting, timeForTesting));
		
		String result = formatter.formatTaskList(taskList, ONE_BILLION);
		String expected = "+-----+--------------------------+---------------+-------------+----------+-------+---------+\n"
				        + "| No. |       Description        | Starting Time | Ending Time | Location | Every | Repeats |\n"
				        + "+-----+--------------------------+---------------+-------------+----------+-------+---------+\n"
				        + "|1    |Task with only description|-              |-            |-         |-      |-        |\n"
				        + "+-----+--------------------------+---------------+-------------+----------+-------+---------+\n"
				        + "|2    |Task with 2 dates         |03 Dec 2020    |03 Dec 2020  |-         |-      |-        |\n"
				        + "+-----+--------------------------+---------------+-------------+----------+-------+---------+\n";
		assertEquals(result, expected);
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
		
		String result = formatter.formatTaskList(taskList, 30);
		String expected = "+-----+------------------------------+---------------+-------------+----------+-------+---------+\n"
				        + "| No. |         Description          | Starting Time | Ending Time | Location | Every | Repeats |\n"
				        + "+-----+------------------------------+---------------+-------------+----------+-------+---------+\n"
				        + "|1    |Task with only description    |-              |-            |-         |-      |-        |\n"
				        + "+-----+------------------------------+---------------+-------------+----------+-------+---------+\n"
				        + "|2    |Task with 2 dates             |03 Dec 2020    |03 Dec 2020  |-         |-      |-        |\n"
				        + "+-----+------------------------------+---------------+-------------+----------+-------+---------+\n"
				        + "|3    |Task with a very long long lon|-              |03 Dec 2020  |NUS SoC   |-      |-        |\n"
				        + "|     |g long description with locati|               |             |          |       |         |\n"
				        + "|     |on and date                   |               |             |          |       |         |\n"
				        + "+-----+------------------------------+---------------+-------------+----------+-------+---------+\n";
		assertEquals(expected, result);
	}
	
	@After
	public void cleanup() {
	
	}

}
