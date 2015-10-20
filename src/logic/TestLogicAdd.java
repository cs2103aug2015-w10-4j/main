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
	
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = new Logic();
	}
	
	@Test
	public void logicAddOne(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("item 1"));
		String message = logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		assertEquals("Item(s) 1 successfully added.", message);
	}
	
	@Test
	public void logicAddTwo(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("\n\n\n %s %d"));
		String message = logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		assertEquals("Item(s) 1 successfully added.", message);
	}
	
	@Test
	public void logicAddThree(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("-1"));
		String message = logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		assertEquals("Item(s) 1 successfully added.", message);
	}
	
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
	
	@Test
	public void logicAddEventOne(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		Task curTask = new Task("item 1");
		Calendar startingDate = Calendar.getInstance();
		Calendar endingDate = Calendar.getInstance();
		startingDate.set(2015, 12, 31);
		endingDate.set(2016, 1, 1);
		curTask.setStartingTime(startingDate);
		curTask.setEndingTime(endingDate);
		newTasks.add(curTask);
		String message = logicObject.addItem(newTasks, null, true, true);
		assertEquals("Item(s) 1 successfully added.", message);
		assertEquals(startingDate, logicObject.listOfTasks.get(0).getStartingTime());
		assertEquals(endingDate, logicObject.listOfTasks.get(0).getEndingTime());
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
}
