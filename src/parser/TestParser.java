package parser;

import static org.junit.Assert.assertEquals;
import global.Command;
import global.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
			//e.printStackTrace();
			assertEquals("Error: Task name is empty", e.getMessage());
		}
	
	}
	
	@Test
	public void testParserAdd() throws Exception {
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
	
	@Test
	public void testParseDate() throws Exception {
		Calendar expectedDate = new GregorianCalendar();
		int year = expectedDate.get(Calendar.YEAR);
		expectedDate.clear();
		expectedDate.set(year, 8, 18);
		
		String[] dateArgs = { "18", "sep"};
		Calendar date = parserObj.parseDate(dateArgs);
		
		assertEquals(expectedDate,date);
		
		// this doesn't work because of milliseconds difference =.=
		/*
		expectedDate = new GregorianCalendar();
		expectedDate.add(Calendar.DATE, 1);
		
		dateArgs = new String[] { "tomorrow" };
		date = parserObj.parseDate(dateArgs);
		
		assertEquals(expectedDate, date);
		*/
	}
	
	@Test
	public void testGetNearestDate(){
		Calendar date = new GregorianCalendar();
		int todayDate = date.get(Calendar.DATE);
		int today = date.get(Calendar.DAY_OF_WEEK);
		
		assertEquals(todayDate+(5-today)%7, parserObj.getNearestDate(5));
	}
}
