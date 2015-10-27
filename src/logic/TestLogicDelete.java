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
	
	public void addHelper(Task newTask){
		logicObject.listOfTasks.add(newTask);
		logicObject.listOfShownTasks.add(newTask);
	}
	
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = new Logic();
	}	
	
	@Test
	public void logicDeleteOne(){
		logicObject.listOfShownTasks = new ArrayList<Task>();
		Task task1 = new Task("item 1");
		Task task2 = new Task("item 2");
		Task task3 = new Task("item 3");
		addHelper(task1);
		addHelper(task2);
		addHelper(task3);
    
		
		ArrayList<String> argumentList = new ArrayList<String>();
		String message;
		
		argumentList.add("six");
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: Invalid argument for command.", message);
		
		argumentList.clear();
		argumentList.add("item 12");
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: Invalid argument for command.", message);
		
		argumentList.clear();
		argumentList.add("4");
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add("-1");
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add("0");
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add("55");
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		
		argumentList.clear();
		argumentList.add("2");
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Item(s) 2 successfully deleted.", message);
	}
	
	@Test
	public void logicDeleteMultipleOne(){
		logicObject.listOfTasks = new ArrayList<Task>();
		addHelper(new Task("some item 1"));
		addHelper(new Task("some item 2"));	
		addHelper(new Task("some item 3"));
		
		ArrayList<String> argumentList = new ArrayList<String>();		

		argumentList.add("1");
		argumentList.add("3");
		argumentList.add("2");
		
		String message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Item(s) 1, 2, 3 successfully deleted.", message);
	}
	
	@Test
	public void logicDeleteMultipleTwo(){
		logicObject.listOfTasks = new ArrayList<Task>();
		addHelper(new Task("some item 1"));
		addHelper(new Task("some item 2"));
		addHelper(new Task("some item 3"));
		addHelper(new Task("some item 4"));
		addHelper(new Task("some item 5"));
		addHelper(new Task("some item 6"));
		
		
		ArrayList<String> argumentList = new ArrayList<String>();
		
		argumentList.add("1");
		argumentList.add("3");
		argumentList.add("2");
		argumentList.add("1");
		argumentList.add("3");
		argumentList.add("2");
		
		String message = logicObject.deleteItem(argumentList, true, true);
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
		
		ArrayList<String> argumentList = new ArrayList<String>();
		String message;
		
		argumentList.add("6");
		argumentList.add("22");
		argumentList.add("2");
		argumentList.add("4");
		argumentList.add("6");
		argumentList.add("6");
		
		message = logicObject.deleteItem(argumentList, true, true);
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
			addHelper((new Task(String.valueOf(i))));
		}
		
		ArrayList<String> argumentList = new ArrayList<>();
		String message;
		
		argumentList.add("1-3");
		argumentList.add("2-5");
		argumentList.add("24-25");
		
		message = logicObject.deleteItem(argumentList, true, true);
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
		
		ArrayList<String> argumentList = new ArrayList<>();
		String message;
		
		argumentList.add("-1-26");
		
		message = logicObject.deleteItem(argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		assertEquals(logicObject.listOfTasks.size(), 26);
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	
}
