package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicDelete {
	Logic logicObject;
	
	@Before
	public void setup(){
		logicObject = new Logic();
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	
	@Test
	public void logicDeleteMultipleOne(){
		logicObject.listOfTasks = new ArrayList<Task>();
		logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));	
		logicObject.listOfTasks.add(new Task("some item 3"));
		
		ArrayList<String> argumentList = new ArrayList<String>();		

		argumentList.add("1");
		argumentList.add("3");
		argumentList.add("2");
		
		String message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("3th Item successfully deleted. 2th Item successfully deleted. 1th Item successfully deleted. ", message);
	}
	
	@Test
	public void logicDeleteMultipleTwo(){
		logicObject.listOfTasks = new ArrayList<Task>();
		logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));	
		logicObject.listOfTasks.add(new Task("some item 3"));
		logicObject.listOfTasks.add(new Task("some item 4"));
        logicObject.listOfTasks.add(new Task("some item 5"));	
		logicObject.listOfTasks.add(new Task("some item 6"));
		
		ArrayList<String> argumentList = new ArrayList<String>();
		
		argumentList.add("1");
		argumentList.add("3");
		argumentList.add("2");
		argumentList.add("1");
		argumentList.add("3");
		argumentList.add("2");
		
		String message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("3th Item successfully deleted. 2th Item successfully deleted. 1th Item successfully deleted. ", message);
	}
	
	@Test
	public void logicDeleteMultipleThree(){
		logicObject.listOfTasks = new ArrayList<Task>();
		logicObject.listOfTasks.add(new Task("some item 1"));
    
		
		ArrayList<String> argumentList = new ArrayList<String>();
		

		argumentList.add("3");

		
		String message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
	}
	
	
	
	/*
	@Test
	public void logicDeleteOne(){
		String message = logicObject.deleteItem(new ArrayList<String>(), true, true);
		assertEquals("Error: Invalid argument for command.", message);
	}
	
	@Test
	public void logicDeleteTwo(){
		ArrayList<String> argumentList = new ArrayList<String>();
		argumentList.add("1");
		String message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
	}
	
	@Test
	public void logicDeleteThree(){
		ArrayList<String> argumentList = new ArrayList<String>();
		argumentList.add("-3");
		String message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
	}
	
	@Test
	public void logicDeleteFour(){
		logicObject.listOfTasks = new ArrayList<Task>();
		logicObject.listOfTasks.add(new Task("some item"));
		
		ArrayList<String> argumentList = new ArrayList<String>();
		argumentList.add("1");
		String message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Item successfully deleted.", message);
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	*/
}
