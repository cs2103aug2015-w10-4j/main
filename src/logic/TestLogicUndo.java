package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicUndo {
	
Logic logicObject;
	
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = Logic.getInstance();
	}
	

	
	@Test
	public void logicUndoEmpty(){
		String message = logicObject.undoCommand();
		assertEquals("Error: No history found.", message);
	}
	
	@Test
	public void logicUndoAdd(){

		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("item 1"));
        logicObject.addItem(newTasks, new ArrayList<String>(), true, true);
		String message = logicObject.undoCommand();		
		assertEquals("Undo : Added item(s) removed.", message);
	}

	@Test
	public void logicUndoMultipleDelete(){

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
	}
	
	@Test
	public void logicUndoEdit(){
		ArrayList<Task> listToEdit = new ArrayList<Task>();
		ArrayList<String> argumentList = new ArrayList<String>();
		
		listToEdit.add(new Task("Old item 1"));
		logicObject.addItem(listToEdit, argumentList, true, true);

		argumentList.add("1");
		listToEdit.clear();
		listToEdit.add(new Task("New item 1"));
		logicObject.editItem(listToEdit, argumentList, true, true);
		
		String message = logicObject.undoCommand();		
		assertEquals("Undo : Reverted edits.", message);
		
		assertEquals("Old item 1", logicObject.listOfTasks.get(0).getName());
		/*assertEquals("some item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("some item 3", logicObject.listOfTasks.get(2).getName());*/
	}
	
	@After
	public void cleanup(){
		Logic.destroyAnyInstance();
		logicObject = null;
	}

}
