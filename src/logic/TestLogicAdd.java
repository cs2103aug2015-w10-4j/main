package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicAdd {
	Logic logicObject;
	
	@Before
	public void setup(){
		logicObject = new Logic();
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	
	@Test
	public void logicAddOne(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("item 1"));
		String message = logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		assertEquals("Item successfully added.", message);
	}
	
	@Test
	public void logicAddTwo(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("\n\n\n %s %d"));
		String message = logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		assertEquals("Item successfully added.", message);
	}
	
	@Test
	public void logicAddThree(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("-1"));
		String message = logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		assertEquals("Item successfully added.", message);
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
}
