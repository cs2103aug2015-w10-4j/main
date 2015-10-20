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
	public void testWriteItemList() {
		Command message;
		try {
			message = parserObj.parseCommand("ADD");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertEquals("Error: Task name is empty", e.getMessage());
		}
	
	}
}
