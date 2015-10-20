package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicRedo {
	
Logic logicObject;
	
	@Before
	public void setup(){
		logicObject = new Logic();
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	


	@Test
	public void logicRedoEmpty(){

		String message = logicObject.redoCommand();		
		assertEquals("Error: No history found.", message);
		

	}
	
	@Test
	public void logicRedoadd(){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("item 1"));
        logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		logicObject.undoCommand();		
		String message = logicObject.redoCommand();
		assertEquals("Redo : Deleted item(s) restored.", message);
		assertEquals("item 1", logicObject.listOfTasks.get(0).getName());
	}
	
	

	@Test
	public void logicRedoMultipleDelete(){

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
		
		logicObject.deleteItem(argumentList, true, true);
	
	
		String message = logicObject.undoCommand();		
		assertEquals("Undo : Deleted item(s) restored.", message);
		assertEquals("some item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("some item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("some item 3", logicObject.listOfTasks.get(2).getName());
		assertEquals("some item 4", logicObject.listOfTasks.get(3).getName());
		assertEquals("some item 5", logicObject.listOfTasks.get(4).getName());
		assertEquals("some item 6", logicObject.listOfTasks.get(5).getName());
		
	    message = logicObject.redoCommand();	
	    assertEquals("Redo : Added item(s) removed.", message);
		

	}

	@Test
	public void logicUndoEdit(){
		ArrayList<Task> listToEdit = new ArrayList<Task>();
		ArrayList<String> argumentList = new ArrayList<String>();
		logicObject.listOfTasks.add(new Task("some item 2"));	
		logicObject.listOfTasks.add(new Task("some item 3"));
		
		
		argumentList.add("1");
		listToEdit.add(new Task("New item 1"));

		logicObject.editItem(listToEdit, argumentList, true, true);
		
		logicObject.undoCommand();		
		String message = logicObject.redoCommand();		
		assertEquals("Redo : Reverted edits.", message);
		
		assertEquals("New item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("some item 3", logicObject.listOfTasks.get(1).getName());

	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("anotherSave.txt");
		saveFile.delete();
		anotherSaveFile.delete();
	}
	

	


}
