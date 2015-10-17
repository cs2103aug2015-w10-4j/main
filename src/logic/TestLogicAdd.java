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
		logicObject = Logic.getInstance();
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
	public void logicAddWithDateOne(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		Task curTask = new Task("item 1");
		Calendar curDate = Calendar.getInstance();
		curTask.setEndingTime(curDate);
		newTasks.add(curTask);
		String message = logicObject.addItem(newTasks, null, true, true);
		assertEquals("Item(s) 1 successfully added.", message);
		assertEquals(curDate, logicObject.listOfTasks.get(0).getEndingTime());
	}
	
	@After
	public void cleanup(){
		Logic.destroyAnyInstance();
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
}
