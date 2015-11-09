package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class TestLogicMark {
	Logic logicObject = new Logic();
	//@@author A0132760M
	@Before
	public void setup(){
		File saveFile = new File("save.txt");
		saveFile.delete();
		logicObject = new Logic();
		
		logicObject.listOfTasks.add(new Task("Item 1"));
	}
	
	/**
	 * Test the simplest case of marking and unmarking of a task
	 */
	@Test
	public void TestMarkOne(){
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		indexList.add(0);
		String result = logicObject.markDoneStatus(indexList, true, true, true);
		assertEquals(true, logicObject.listOfTasks.get(0).isDone());
		assertEquals("Item(s) successfully marked as done.", result);
		
		result = logicObject.markDoneStatus(indexList, true, true, false);
		assertEquals(false, logicObject.listOfTasks.get(0).isDone());
		assertEquals("Item(s) successfully marked as undone.", result);
	}
	
}
