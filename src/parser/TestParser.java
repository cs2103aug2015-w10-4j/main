package parser;

import static org.junit.Assert.assertEquals;
import global.Command;
import global.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import storage.JsonFormatStorage;

public class TestParser {

	Parser parserObj;

	//@@author A0108355H
	@Before
	public void setup(){
			parserObj = new Parser();
	}


	//@@author A0108355H
	@Test
	public void testParserAddEmptyName() {
		Command message;
		try {
			message = parserObj.parseCommand("add");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertEquals("Error: Task name is empty.", e.getMessage());
		}
	
	}
	
	//@@author A0108355H
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
	
	//@@author A0108355H
	@Test
	public void testParserEditEmptyName() {
		Command message;
		try {
			message = parserObj.parseCommand("edit");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertEquals("Error: Invalid number of arguments.", e.getMessage());
		}
	
	}
	
	//@@author A0108355H
	@Test
	public void testParserEditSpecial() throws Exception {
		Command message;

		message = parserObj.parseCommand("edit 1 every 2 days for 2");
		assertEquals("Name: null Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats null", message.getTask(0).getAllInfo());
		
	
	}
	
	//@@author A0108355H
	@Test
	public void testParserEditNormal() throws Exception {
		Command message;

		message = parserObj.parseCommand("edit 1 task1 loc nus by next friday"); // needs to change, next friday's always changes x.x
		String actual = "Name: task1 Starting time: null Ending Time: java.util.GregorianCalendar[time=?,areFieldsSet=false,areAllFieldsSet=false,lenient=true,zone=sun.util.calendar.ZoneInfo[id=\"Asia/Singapore\",offset=28800000,dstSavings=0,useDaylight=false,transitions=9,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=?,YEAR=2015,MONTH=10,WEEK_OF_YEAR=?,WEEK_OF_MONTH=?,DAY_OF_MONTH=13,DAY_OF_YEAR=?,DAY_OF_WEEK=?,DAY_OF_WEEK_IN_MONTH=?,AM_PM=1,HOUR=11,HOUR_OF_DAY=?,MINUTE=59,SECOND=?,MILLISECOND=?,ZONE_OFFSET=?,DST_OFFSET=?] Location: nus Period Interval: null Period Repeats null";
				assertEquals(actual, message.getTask(0).getAllInfo());
		
	
	}
	
	//@@author A0108355H
	@Test
	public void testParserSearch() throws Exception {
		Command message;

		message = parserObj.parseCommand("search task");
		String actual ="Name: task Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats null";
		assertEquals(actual, message.getTask(0).getAllInfo());
		
	
	}
	
	//@@author A0108355H
	@Test
	public void testParserSaveto() throws Exception {
		Command message;
		message = parserObj.parseCommand("saveto new.txt");
		String actual ="new.txt";
		assertEquals(actual, message.getArguments().get(0));	
	}
	

	//@@author A0108355H
	@Test
	public void testParserMark() throws Exception {
		Command message;
		message = parserObj.parseCommand("mark 1");
		String actual ="1";
		assertEquals(actual, message.getArguments().get(0));	
	}
	
	//@@author A0108355H
	@Test
	public void testParserUnMark() throws Exception {
		Command message;
		message = parserObj.parseCommand("unmark 1");
		String actual ="1";
		assertEquals(actual, message.getArguments().get(0));	
	}
	
	//@@author A0108355H
	@Test
	public void testParserAdd() throws Exception {
		Command message;
		Task task = new Task("task");
		Command cmd = new Command(Command.Type.ADD,task);
		message = parserObj.parseCommand("add task");
		assertEquals(true,cmd.compareTo(message) );
	}
	
	//@@author A0108355H
	@Test
	public void testParserDelete() throws Exception {
		Command message;
		Task task = new Task();
		Command cmd = new Command(Command.Type.DELETE,task);
		message = parserObj.parseCommand("delete 1");
		assertEquals("DELETE",message.getCommandType().toString());
	}
	
	//@@author A0108355H
	@Test
	public void testParseDate() throws Exception {
		Calendar expectedDate = new GregorianCalendar();
		int year = expectedDate.get(Calendar.YEAR);
		expectedDate.clear();
		expectedDate.set(2015, 8, 18);
		expectedDate.set(Calendar.HOUR, 23);
		expectedDate.set(Calendar.MINUTE, 59);
		
		String[] dateArgs = { "18", "sep", "2015"};
		Calendar date = parserObj.parseTime(dateArgs);
		
		assertEquals(expectedDate, date);
		
		
		// this doesn't work because of milliseconds difference =.=
		/*
		expectedDate = new GregorianCalendar();
		expectedDate.add(Calendar.DATE, 1);
		
		dateArgs = new String[] { "tomorrow" };
		date = parserObj.parseDate(dateArgs);
		
		assertEquals(expectedDate, date);
		*/
	}

	//@@author A0108355H
	@Test
	public void testGetNearestDate(){
		Calendar date = new GregorianCalendar();
		int todayDate = date.get(Calendar.DATE);
		int today = date.get(Calendar.DAY_OF_WEEK);
		
		assertEquals(todayDate+(5-today)%7, parserObj.getNearestDate(5));
	}

}
