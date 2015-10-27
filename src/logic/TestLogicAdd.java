package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicAdd {
	Logic logicObject;
	
	/*
	 * Helper functions
	 */
	public String addItem(String taskName){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task(taskName));
		return logicObject.addItem(newTasks, new ArrayList<Integer>(), true, true);
	}
	
	public String addItem(String taskName, Calendar deadline){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		Task curTask = new Task(taskName);
		curTask.setEndingTime(deadline);
		newTasks.add(curTask);
		return logicObject.addItem(newTasks, new ArrayList<Integer>(), true, true);
	}
	
	public String addItem(String taskName, Calendar startTime, Calendar endTime){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		Task curTask = new Task(taskName);
		curTask.setStartingTime(startTime);
		curTask.setEndingTime(endTime);
		newTasks.add(curTask);
		return logicObject.addItem(newTasks, new ArrayList<Integer>(), true, true);
	}
	
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = new Logic();
	}
	
	
	/*
	 * Tests for basic addition of task with characters and numbers
	 */
	@Test
	public void logicAddOne(){
		String result = addItem("item 1");
		assertEquals("Item(s) 1 successfully added.", result);
	}
	
	/*
	 * Tests for adding of tasks with weird characters
	 */
	@Test
	public void logicAddTwo(){
		String result = addItem("\n\n\n %s %d !@#$%^&*()[]{}\\;',.<>/?+_-=~`");
		assertEquals("Item(s) 1 successfully added.", result);
	}
	
	/*
	 * Tests for adding of tasks that are only integers
	 */
	@Test
	public void logicAddThree(){
		String result = addItem("-1");
		assertEquals("Item(s) 1 successfully added.", result);
	}
	
	/*
	 * Tests for adding of task with a deadline
	 */
	@Test
	public void logicAddDeadlineOne(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		Task curTask = new Task("item 1");
		Calendar curDate = Calendar.getInstance();
		curTask.setEndingTime(curDate);
		newTasks.add(curTask);
		String message = logicObject.addItem(newTasks, null, true, true);
		assertEquals("Item(s) 1 successfully added.", message);
		assertEquals(curDate, logicObject.listOfTasks.get(0).getEndingTime());
	}
	
	/*
	 * Tests for adding of event-tasks
	 */
	@Test
	public void logicAddEventOne(){
		Calendar startingDate = Calendar.getInstance();
		Calendar endingDate = Calendar.getInstance();
		startingDate.set(2015, 12, 31);
		endingDate.set(2016, 1, 1);
		
		String result = addItem("item 1", startingDate, endingDate);
		assertEquals("Item(s) 1 successfully added.", result);
		assertEquals(startingDate, logicObject.listOfTasks.get(0).getStartingTime());
		assertEquals(endingDate, logicObject.listOfTasks.get(0).getEndingTime());
	}
	
	/*
	 * Tests for adding of event-tasks that have clashes
	 */
	@Test
	public void logicAddEventTwo(){
		Calendar startingDate = Calendar.getInstance();
		Calendar endingDate = Calendar.getInstance();
		startingDate.set(2015, 12, 31);
		endingDate.set(2016, 1, 2);
		String result = addItem("item 1", startingDate, endingDate);
		
		Calendar startingDate2 = Calendar.getInstance();
		Calendar endingDate2 = Calendar.getInstance();
		startingDate2.set(2016,1,1);
		endingDate2.set(2016,1,2);
		
		String result2 = addItem("item 2", startingDate2, endingDate2);
		
		assertEquals(2, logicObject.listOfTasks.size());
		assertEquals("Item(s) 1 successfully added.", result);
		assertEquals(startingDate, logicObject.listOfTasks.get(0).getStartingTime());
		assertEquals(endingDate, logicObject.listOfTasks.get(0).getEndingTime());
		assertEquals("Warning: There are clashing timings between tasks.", result2);
		assertEquals(startingDate2, logicObject.listOfTasks.get(1).getStartingTime());
		assertEquals(endingDate2, logicObject.listOfTasks.get(1).getEndingTime());
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
}
