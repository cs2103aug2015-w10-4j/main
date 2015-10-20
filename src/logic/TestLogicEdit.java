package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicEdit {
	Logic logicObject;
	
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = new Logic();
		
		logicObject.listOfTasks.add(new Task("Item 1"));
		logicObject.listOfTasks.add(new Task("Item 2"));
		logicObject.listOfTasks.add(new Task("Item 3"));
	}
	
	/*
	 * Tests corner cases of index in edit item
	 */
	@Test
	public void TestEditOne(){
		ArrayList<Task> listToEdit = new ArrayList<Task>();
		ArrayList<String> argumentList = new ArrayList<String>();
		String message;
		
		argumentList.add("4");
		listToEdit.add(new Task("New item 99"));
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		assertEquals("Item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("Item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("Item 3", logicObject.listOfTasks.get(2).getName());
		
		argumentList.clear();
		argumentList.add("3");
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Item(s) 3 successfully edited.", message);
		assertEquals("Item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("Item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("New item 99", logicObject.listOfTasks.get(2).getName());
		
		argumentList.clear();
		argumentList.add("0");
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		assertEquals("Item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("Item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("New item 99", logicObject.listOfTasks.get(2).getName());
		
		argumentList.clear();
		argumentList.add("-1");
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Error: There is no item at this index.", message);
		assertEquals("Item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("Item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("New item 99", logicObject.listOfTasks.get(2).getName());
	}
	
	/*
	 * Tests editing normal values multiple times
	 */
	@Test
	public void TestEditTwo(){
		ArrayList<Task> listToEdit = new ArrayList<Task>();
		ArrayList<String> argumentList = new ArrayList<String>();
		String message;
		
		argumentList.add("1");
		listToEdit.add(new Task("New item 1"));
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Item(s) 1 successfully edited.", message);
		assertEquals("New item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("Item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("Item 3", logicObject.listOfTasks.get(2).getName());
		
		argumentList.clear();
		listToEdit.clear();
		argumentList.add("2");
		listToEdit.add(new Task("New item 2"));
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Item(s) 2 successfully edited.", message);
		assertEquals("New item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("New item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("Item 3", logicObject.listOfTasks.get(2).getName());
		
		argumentList.clear();
		listToEdit.clear();
		argumentList.add("3");
		listToEdit.add(new Task("New item 3"));
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Item(s) 3 successfully edited.", message);
		assertEquals("New item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("New item 2", logicObject.listOfTasks.get(1).getName());
		assertEquals("New item 3", logicObject.listOfTasks.get(2).getName());
		
		argumentList.clear();
		listToEdit.clear();
		argumentList.add("2");
		listToEdit.add(new Task("item 2 changed again!"));
		message = logicObject.editItem(listToEdit, argumentList, true, true);
		assertEquals("Item(s) 2 successfully edited.", message);
		assertEquals("New item 1", logicObject.listOfTasks.get(0).getName());
		assertEquals("item 2 changed again!", logicObject.listOfTasks.get(1).getName());
		assertEquals("New item 3", logicObject.listOfTasks.get(2).getName());
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
	
}
