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
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = new Logic();
	}	
	
	@Test
	public void logicDeleteOne(){
		logicObject.listOfTasks = new ArrayList<Task>();
		logicObject.listOfTasks.add(new Task("item 1"));
		logicObject.listOfTasks.add(new Task("item 2"));
		logicObject.listOfTasks.add(new Task("item 3"));
    
		
		ArrayList<Integer> argumentList = new ArrayList<Integer>();
		String message;
		argumentList.clear();
		argumentList.add(4);
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add(-1);
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add(0);
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add(55);
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add(2);
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Item(s) 2 successfully deleted.", message);
	}
	
	@Test
	public void logicDeleteMultipleOne(){
		logicObject.listOfTasks = new ArrayList<Task>();
		logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));	
		logicObject.listOfTasks.add(new Task("some item 3"));
		
		ArrayList<Integer> indexList = new ArrayList<Integer>();		

		indexList.add(1);
		indexList.add(3);
		indexList.add(2);
		
		String message = logicObject.deleteItem(indexList, true, true);
		assertEquals("Item(s) 1, 2, 3 successfully deleted.", message);
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
		
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		indexList.add(1);
		indexList.add(3);
		indexList.add(2);
		indexList.add(1);
		indexList.add(3);
		indexList.add(2);
		
		String message = logicObject.deleteItem(indexList, true, true);
		assertEquals("Item(s) 1, 2, 3 successfully deleted.", message);
	}
	
	@Test
	public void logicDeleteMultipleThree(){
		logicObject.listOfTasks = new ArrayList<Task>();
		logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));	
		logicObject.listOfTasks.add(new Task("some item 3"));
		logicObject.listOfTasks.add(new Task("some item 4"));
        logicObject.listOfTasks.add(new Task("some item 5"));	
		logicObject.listOfTasks.add(new Task("some item 6"));
		
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		String message;
		
		indexList.add(6);
		indexList.add(22);
		indexList.add(2);
		indexList.add(4);
		indexList.add(6);
		indexList.add(6);
		
		message = logicObject.deleteItem(indexList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		assertEquals(logicObject.listOfTasks.size(), 6);
	}
	
	/*
	 * Try to delete tasks with several overlapping range delete
	 */
	@Test
	public void logicDeleteMultipleItemsSuccess() {
		logicObject.listOfTasks = new ArrayList<Task>();
		for (int i = 'a'; i <= 'z'; i++) {
			logicObject.listOfTasks.add(new Task(String.valueOf(i)));
		}
		
		ArrayList<Integer> indexList = new ArrayList<>();
		String message;
		
		indexList.add(1);
		indexList.add(2);
		indexList.add(3);
		indexList.add(4);
		indexList.add(5);
		indexList.add(24);
		indexList.add(25);
		
		message = logicObject.deleteItem(indexList, true, true);
		assertEquals("Item(s) 1, 2, 3, 4, 5, 24, 25 successfully deleted.", message);
		assertEquals(logicObject.listOfTasks.size(), 19);
	}
	
	/*
	 * Try to delete a task with invalid index
	 */
	@Test
	public void logicDeleteMultipleItemsFail() {
		logicObject.listOfTasks = new ArrayList<Task>();
		for (int i = 'a'; i <= 'z'; i++) {
			logicObject.listOfTasks.add(new Task(String.valueOf(i)));
		}
		
		ArrayList<Integer> indexList = new ArrayList<>();
		String message;
		
		indexList.add(-1);
		indexList.add(2);
		indexList.add(-3);
		
		
		message = logicObject.deleteItem(indexList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		assertEquals(logicObject.listOfTasks.size(), 26);
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	
}
