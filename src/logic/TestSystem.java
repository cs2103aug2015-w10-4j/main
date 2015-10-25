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
			File saveFile = new File("save.txt");
			saveFile.delete();
			logicObject = new Logic();
			storageObj = new JsonFormatStorage(true);
	}
	
	@Test
	public void testLogicParserSimpleAdd() throws Exception {

		Command commandObject = parserObj.parseCommand("add task1");
		
		String executionResult = logicObject.executeCommand(commandObject, true,
				true);
		assertEquals("Item(s) 1 successfully added.",executionResult);
	}
	
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
