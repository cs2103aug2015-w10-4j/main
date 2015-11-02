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

	public void addHelper(Task newTask) {
		logicObject.listOfTasks.add(newTask);
		logicObject.listOfShownTasks.add(newTask);
	}
	
	@Before
	public void setup(){
		logicObject = new Logic();
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	

	
	@Test
	public void logicUndoEmpty() throws Exception{
		String message = logicObject.undoCommand();
		assertEquals("Error: No history found.", message);
	}
	
	@Test
	public void logicUndoAdd(){

		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(new Task("item 1"));
        logicObject.addItem(newTasks, new ArrayList<Integer>(), true, true);
        logicObject.showUpdatedItems();
		String message = logicObject.undoCommand();		
		assertEquals("Undo : Added item(s) removed.", message);
	}

	@Test
	public void logicUndoMultipleDelete(){

		logicObject.listOfTasks = new ArrayList<Task>();
		addHelper(new Task("some item 1"));
        addHelper(new Task("some item 2"));	
		addHelper(new Task("some item 3"));
		addHelper(new Task("some item 4"));
		addHelper(new Task("some item 5"));	
		addHelper(new Task("some item 6"));
		
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		indexList.add(1);
		indexList.add(3);
		indexList.add(2);
		indexList.add(1);
		indexList.add(3);
		indexList.add(2);
		
		logicObject.deleteItem(indexList, true, true);
	
	
		String message = logicObject.undoCommand();		
		assertEquals("Undo : Deleted item(s) restored.", message);
	}
	
	@Test
	public void logicUndoEdit(){
		ArrayList<Task> listToEdit = new ArrayList<Task>();
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		listToEdit.add(new Task("Old item 1"));
		logicObject.addItem(listToEdit, new ArrayList<Integer>(), true, true);

		indexList.add(1);
		listToEdit.clear();
		listToEdit.add(new Task("New item 1"));
		logicObject.editItem(listToEdit, indexList, true, true);

		String message = logicObject.undoCommand();		
		assertEquals("Undo : Added item(s) removed.", message);
		
		assertEquals("Old item 1", logicObject.listOfTasks.get(0).getName());
		/*assertEquals("some item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("some item 3", logicObject.listOfTasks.get(2).getName());*/
	}
	
	@After
	public void cleanup(){
	}

}
