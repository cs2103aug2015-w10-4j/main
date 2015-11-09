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
	//@@author A0132760M
	Logic logicObject;
	File saveFile;
	
	public void addHelper(Task newTask){
		logicObject.listOfTasks.add(newTask);
		logicObject.listOfShownTasks.add(newTask);
	}
	
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("anotherSave.txt");
		saveFile.delete();
		anotherSaveFile.delete();
		logicObject = new Logic();
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Task task_1 = new Task();
		task_1.setName("Item 1");
		Task task_2 = new Task();
		task_2.setName("Item 2");
		Task task_3 = new Task();
		task_3.setName("Item 3");
		addHelper(task_1);
		addHelper(task_2);
		addHelper(task_3);
	}
	
	@Test
	public void logicExecuteAdd(){
		Task task;
		Command commandObject;
		// case 1
		task = new Task();
		task.setName("Submit assignment");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item(s) successfully added.", logicObject.executeCommand(commandObject, true, true));
		
		// case 2
		task.setName("%% Random item %%");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item(s) successfully added.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@Test
	public void logicExecuteDelete(){
		Command commandObject;
		String[] args;
		
		args = new String[1];
		args[0] = "2";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Item(s) successfully deleted.", logicObject.executeCommand(commandObject, true, true));
		
	}
	
	@Test
	public void logicExecuteDisplay(){
		Command commandObject;
		
		// case 1
		commandObject = new Command(Command.Type.DISPLAY, new String[]{});
		assertEquals("Displaying items.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@Test
	public void logicExecuteUndo(){
		Command commandObject;
		// case 1
		commandObject = new Command(Command.Type.UNDO);
		assertEquals("Error: No history found.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@Test
	public void logicExecuteRedo(){
		Command commandObject;
		// case 1
		commandObject = new Command(Command.Type.REDO);
		assertEquals("Error: No history found.", logicObject.executeCommand(commandObject, true, true));
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
	public void logicExecuteMark(){
		Command commandObject;
		String[] args;
		
		args = new String[1];
		args[0] = "1";
		commandObject = new Command(Command.Type.MARK, args);
		assertEquals("Item(s) successfully marked as done.", logicObject.executeCommand(commandObject, true, true));
	}
	
	@Test
	public void logicExecuteNull(){
		Command commandObject = null;
		assertEquals("Error: Invalid command.", logicObject.executeCommand(commandObject, true, true));
		
		commandObject = new Command(null, new String[]{"123"});
		assertEquals("Error: Handler for this command type has not been defined.", logicObject.executeCommand(commandObject, true, true));
		
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("anotherSave.txt");
		saveFile.delete();
		anotherSaveFile.delete();
	}
}
