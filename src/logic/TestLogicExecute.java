package logic;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import global.Command;
import global.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class contains test cases of the executeCommand function of Logic
 */
public class TestLogicExecute {

	Logic logicObject;
	File saveFile;
	
	@Before
	public void setup(){
		logicObject = new Logic();
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("anotherSave.txt");
		saveFile.delete();
		anotherSaveFile.delete();
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Task task_1 = new Task();
		task_1.setName("Item 1");
		Task task_2 = new Task();
		task_2.setName("Item 2");
		Task task_3 = new Task();
		task_3.setName("Item 3");
		logicObject.listOfTasks.add(task_1);
		logicObject.listOfTasks.add(task_2);
		logicObject.listOfTasks.add(task_3);
	}
	
	@Test
	public void logicExecuteAdd(){
		Task task;
		Command commandObject;
		// case 1
		task = new Task();
		task.setName("Submit assignment");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item(s) 4 successfully added.", logicObject.executeCommand(commandObject, true, true));
		
		// case 2
		task.setName("%% Random item %%");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item(s) 5 successfully added.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@Test
	public void logicExecuteDelete(){
		Command commandObject;
		String[] args;
		
		// case 1
		args = new String[1];
		args[0] = "2";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Item(s) 2 successfully deleted.", logicObject.executeCommand(commandObject, true, true));
		
		// case 2
		// left item 1 & 3
		args = new String[1];
		args[0] = "3";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Error: There is no item at this index.", logicObject.executeCommand(commandObject, true, true));
		
		// case 3
		args = new String[1];
		args[0] = "four";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Error: Invalid argument for command.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@Test
	public void logicExecuteDisplay(){
		Command commandObject;
		
		// case 1
		commandObject = new Command(Command.Type.DISPLAY);
		assertEquals("Displaying items.", logicObject.executeCommand(commandObject, true, true));
	}
	
	
	
	@Test
	public void logicExecuteSavePath(){
		Command commandObject;
		String[] args;
		
		args = new String[1];
		args[0] = "save.txt";
		commandObject = new Command(Command.Type.SAVETO, args);
		assertEquals("File path not changed. Entered file path is the same as current one used.", logicObject.executeCommand(commandObject, true, true));
		
		args = new String[1];
		args[0] = "anotherSave.txt";
		commandObject = new Command(Command.Type.SAVETO, args);
		assertEquals("File path successfully changed.", logicObject.executeCommand(commandObject, true, true));
		
		args = new String[1];
		args[0] = "save.txt";
		commandObject = new Command(Command.Type.SAVETO, args);
		assertEquals("File path successfully changed.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@Test
	public void logicExecute(){
		Task task;
		Command commandObject;
		String[] args;
		// case 1
		task = new Task();
		task.setName("Submit assignment");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item(s) 4 successfully added.", logicObject.executeCommand(commandObject, true, true));
		
		args = new String[1];
		args[0] = "1";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Item(s) 1 successfully deleted.", logicObject.executeCommand(commandObject, true, true));
		assertEquals("Item(s) 1 successfully deleted.", logicObject.executeCommand(commandObject, true, true));
		assertEquals("Item(s) 1 successfully deleted.", logicObject.executeCommand(commandObject, true, true));
		assertEquals("Item(s) 1 successfully deleted.", logicObject.executeCommand(commandObject, true, true));
		
		
		commandObject = new Command(Command.Type.DISPLAY);
		assertEquals("No items to display.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("anotherSave.txt");
		saveFile.delete();
		anotherSaveFile.delete();
	}
}
