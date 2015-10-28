package logic;


import static org.junit.Assert.assertEquals;


import global.Command;
import global.Task;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import parser.Parser;
import storage.JsonFormatStorage;
import storage.Storage;
import logic.Logic;

public class TestSystem {
	Logic logicObject = null;
	Parser parserObj = null;
	Storage storageObj = null;
	

	
	@Before
	public void setup(){
			parserObj = new Parser();
			File newSaveFile = new File("newsave.txt");
			newSaveFile.delete();
			File saveFile = new File("save.txt");
			saveFile.delete();
			logicObject = new Logic();
			storageObj = new JsonFormatStorage(true);
	}
	
	/*
	 * pass string from parser to logic, test adding a simple task
	 * test on logic side, check whether the logic process command correct or not
	 */
	@Test
	public void testLogicParserSimpleAdd() throws Exception {

		Command commandObject = parserObj.parseCommand("add task1");
		
		String executionResult = logicObject.executeCommand(commandObject, true,
				true);
		assertEquals("Item(s) 1 successfully added.",executionResult);
	}
	
	/*
	 * pass string from parser to logic, test deleting a task
	 * add task1 before testing
	 * test on logic side, check whether the logic process command correct or not
	 */
	@Test
	public void testLogicParserSimpleDelete() throws Exception {
        Command commandObject = parserObj.parseCommand("add task1");
		
		logicObject.executeCommand(commandObject, true,
				true);

		commandObject = parserObj.parseCommand("delete 1");
		
		String executionResult = logicObject.executeCommand(commandObject, true,
				true);
		assertEquals("Item(s) 1 successfully deleted.",executionResult);
	}
	
	/*
	 * pass string from parser to logic, test deleting a task
	 * followed by passing the result task to storage and test whether the result stored is correct or not
	 * add task1 before testing
	 */
	@Test
	public void testLogicParserStorageEditOne() throws Exception {
        Command commandObject = parserObj.parseCommand("add task1");
		
		logicObject.executeCommand(commandObject, true,
				true);

		commandObject = parserObj.parseCommand("edit 1 homework by next tuesday loc nus");
		
		String executionResult = logicObject.executeCommand(commandObject, true,
				true);
		ArrayList<Task> listOfTasks = logicObject.listOfTasks;
		storageObj.writeItemList(listOfTasks);
		ArrayList<Task> message = storageObj.getItemList();
		String resultStr = "";
		
		resultStr = message.get(0).getAllInfo();
		
			
		assertEquals("Name: homework Starting time: null Ending Time: null Location: nus  Period Interval: null Period Repeats null", resultStr);
	}
	
	/*
	 * pass string from parser to logic, test editing a few tasks using special editing
	 * followed by passing the result task to storage and test whether the result stored is correct or not
	 * add task1,task2,task3 before testing
	 */
	@Test
	public void testLogicParserStorageEditTwo() throws Exception {
        Command commandObject = parserObj.parseCommand("add task1");
		logicObject.executeCommand(commandObject, true,
				true);
		
		commandObject = parserObj.parseCommand("add task2");
		logicObject.executeCommand(commandObject, true,
					true);
			
		commandObject = parserObj.parseCommand("add task3");
		logicObject.executeCommand(commandObject, true,
						true);	

		commandObject = parserObj.parseCommand("edit 1 homework next tuesday loc nus every 2 days for 2");
		logicObject.executeCommand(commandObject, true,
				true);
		
        commandObject = parserObj.parseCommand("edit 1 loc nus");
		logicObject.executeCommand(commandObject, true,
				true);
		
		 commandObject = parserObj.parseCommand("edit 3 every 2 days for 2");
			logicObject.executeCommand(commandObject, true,
					true);
		
		ArrayList<Task> listOfTasks = logicObject.listOfTasks;
		storageObj.writeItemList(listOfTasks);
		ArrayList<Task> message = storageObj.getItemList();
		String resultStr = "";
		
		for( int i=0; i < listOfTasks.size(); i++ ) {
			resultStr += message.get(i).getAllInfo() +" ";
			}
	
		assertEquals("Name: homework next tuesday Starting time: null Ending Time: null Location: nus  Period Interval: null Period Repeats null Name: task2 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats null Name: task3 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats null ", resultStr);
	}
	
	
	/*
	 * pass string from parser to logic, test deleting a task
	 * followed by passing the result task to storage and test whether the result stored is correct or not
	 * add 3 tasks before testing
	 */
	@Test
	public void testLogicParserStorageDelete() throws Exception {
        Command commandObject = parserObj.parseCommand("add task1");
        logicObject.executeCommand(commandObject, true,
				true);
        commandObject = parserObj.parseCommand("add task2");
        logicObject.executeCommand(commandObject, true,
				true);
       commandObject = parserObj.parseCommand("add task3");
       logicObject.executeCommand(commandObject, true,
				true);
		

		commandObject = parserObj.parseCommand("edit 1 homework by next tuesday loc nus");
		
		String executionResult = logicObject.executeCommand(commandObject, true,
				true);
		ArrayList<Task> listOfTasks = logicObject.listOfTasks;
		storageObj.writeItemList(listOfTasks);
		ArrayList<Task> message = storageObj.getItemList();
		String resultStr = "";
		for( int i=0; i < listOfTasks.size(); i++ ) {
		resultStr += message.get(i).getAllInfo() +" ";
		}
			
		assertEquals("Name: homework next tuesday Starting time: null Ending Time: null Location: nus  Period Interval: null Period Repeats null Name: task2 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats null Name: task3 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats null ", resultStr);
	}
	
	/*
	 * pass data from logic to storage to store file
	 * test on storage side, whether the file stored is the correct one or not
	*/
	@Test
	public void testLogicStorage() throws Exception {

		ArrayList<Task> listOfTasks = new ArrayList<Task>();
		Task task1 = new Task("task");
		listOfTasks.add(task1);
		storageObj.writeItemList(listOfTasks);
		ArrayList<Task> message = storageObj.getItemList();
		String resultStr = "";
		
		for(int i = 0; i < message.size(); i++){
			resultStr += message.get(i).getName();
		}
			
		assertEquals("task", resultStr);
	}
	 
}
