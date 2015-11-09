package logic;

import static org.junit.Assert.*;
import global.Task;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicHelpers {
	Logic logicObject;
	
	//@@author A0132760M
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = new Logic();
	}
	
	/*
	 * Tests clashing dates
	 */
	@Test
	public void testIsClashing(){
		Calendar dateOne = new GregorianCalendar();
		dateOne.clear();
		dateOne.set(2015, 10, 9);
		Calendar dateTwo = new GregorianCalendar();
		dateTwo.clear();
		dateTwo.set(2015, 10, 13);
		Task taskOne = new Task();
		Task taskTwo = new Task();
		taskOne.setStartingTime(dateOne);
		taskOne.setEndingTime(dateTwo);
		taskTwo.setStartingTime(dateOne);
		taskTwo.setEndingTime(dateTwo);
		
		assertEquals(true, logicObject.isClashing(taskOne, taskTwo));
				
	}
	
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
	}
}
