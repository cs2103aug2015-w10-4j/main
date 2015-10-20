package parser;

import static org.junit.Assert.assertEquals;
import global.Command;
import global.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import storage.JsonFormatStorage;

public class TestParser {

	Parser parserObj;

	@Before
	public void setup(){
			parserObj = new Parser();
	}
	
	
	@Test
	public void testParserAddEmptyName() {
		Command message;
		try {
			message = parserObj.parseCommand("add");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertEquals("Error: Task name is empty", e.getMessage());
		}
	
	}
	
	@Test
	public void testParserDeleteEmptyName() {
		Command message;
		try {
			message = parserObj.parseCommand("delete");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertEquals("Error: Task name is empty", e.getMessage());
		}
	
	}
	
	@Test
	public void testParserEditEmptyName() {
		Command message;
		try {
			message = parserObj.parseCommand("edit");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertEquals("Error: Task name is empty", e.getMessage());
		}
	
	}
	
	@Test
	public void testParserADD() throws Exception {
		Command message;
		Task task = new Task("task");
		Command cmd = new Command(Command.Type.ADD,task);
		message = parserObj.parseCommand("add task");
		assertEquals(true,cmd.compareTo(message) );
	}
	
	@Test
	public void testParserDelete() throws Exception {
		Command message;
		Task task = new Task();
		Command cmd = new Command(Command.Type.DELETE,task);
		message = parserObj.parseCommand("delete 1");
		assertEquals("DELETE",message.getCommandType().toString());
	}
}
