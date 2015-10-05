package logic;

import static org.junit.Assert.assertEquals;

import java.io.File;

import global.Command;
import global.Task;

import org.junit.Before;
import org.junit.Test;
public class TestLogic {

	Logic logicObject;
	File saveFile;
	
	@Before
	public void setupLogic(){
		logicObject = new Logic();
		File saveFile = new File("save.txt");
		saveFile.delete();
		
		
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
	public void logicAdd(){
		Task task;
		Command commandObject;
		// case 1
		task = new Task();
		task.setName("Submit assignment");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item successfully added.", logicObject.executeCommand(commandObject, true));
		
		// case 2
		task.setName("%% Random item %%");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item successfully added.", logicObject.executeCommand(commandObject, true));
	}
	
	@Test
	public void logicDelete(){
		Command commandObject;
		String[] args;
		
		// case 1
		args = new String[1];
		args[0] = "2";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Item successfully deleted.", logicObject.executeCommand(commandObject, true));
		
		// case 2
		// left item 1 & 3
		args = new String[1];
		args[0] = "3";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Error: There is no item at this index.", logicObject.executeCommand(commandObject, true));
		
		args = new String[1];
		args[0] = "four";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Error: Invalid argument for command", logicObject.executeCommand(commandObject, true));
	}
	
	@Test
	public void logicDisplay(){
		Command commandObject;
		
		// case 1
		commandObject = new Command(Command.Type.DISPLAY);
		assertEquals("1. Item 1\r\n2. Item 2\r\n3. Item 3\r\n", logicObject.executeCommand(commandObject, true));
	}
	
	
	@Test
	public void logicTests(){
		Task task;
		Command commandObject;
		String[] args;
		// case 1
		task = new Task();
		task.setName("Submit assignment");
		commandObject = new Command(Command.Type.ADD, task);
		assertEquals("Item successfully added.", logicObject.executeCommand(commandObject, true));
		
		args = new String[1];
		args[0] = "1";
		commandObject = new Command(Command.Type.DELETE, args);
		assertEquals("Item successfully deleted.", logicObject.executeCommand(commandObject, true));
		assertEquals("Item successfully deleted.", logicObject.executeCommand(commandObject, true));
		assertEquals("Item successfully deleted.", logicObject.executeCommand(commandObject, true));
		assertEquals("Item successfully deleted.", logicObject.executeCommand(commandObject, true));
		
		
		commandObject = new Command(Command.Type.DISPLAY);
		assertEquals("No items to display.", logicObject.executeCommand(commandObject, true));
	}
	
	
	@Test
	public void logicSavePath(){
		Command commandObject;
		String[] args;
		
		// case 1
		args = new String[1];
		args[0] = "2";
		commandObject = new Command(Command.Type.SAVEPATH, args);
		assertEquals("File path successfully changed.", logicObject.executeCommand(commandObject, true));
		//saveFilePath(argumentList);
	}
}
