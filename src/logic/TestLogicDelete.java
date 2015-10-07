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
	public void logicDeleteOne(){
		String message = logicObject.deleteItem(new ArrayList<String>(), true, true);
		assertEquals("Error: Invalid argument for command", message);
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
}
