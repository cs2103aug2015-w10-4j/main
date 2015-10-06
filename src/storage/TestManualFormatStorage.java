package storage;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import global.Task;
import logic.Logic;

public class TestManualFormatStorage {
Logic logicObject;
ArrayList<Task> result ;
String TEST_ITEMS = "item1 item2 item3 ";

@Before
public void setup(){
	logicObject = new Logic();
	result = new ArrayList<Task>();
	String[]strArr = TEST_ITEMS.split(" ");
	//Task newTask = new Task("item 1");
	for(int i = 0; i < strArr.length; i++){
		Task newTask = new Task(strArr[i]);
		result.add(newTask);
	}

	
	//File saveFile = new File("save.txt");
	//saveFile.delete();
}

@Test
public void testWriteItemList() throws IOException{
	//Task newTask = new Task("item 1");
	boolean message = logicObject.storageObject.writeItemList(result);
	assertEquals(true, message);
}

@Test
public void TestSaveFileToPath() throws IOException{
	boolean message = logicObject.storageObject.saveFileToPath("newsave.txt");
	assertEquals(true, message);
}

@Test
public void TestGetItemList() throws IOException{
	ArrayList<Task> message = logicObject.storageObject.getItemList();
	String result = "";
	for(int i =0; i < message.size(); i++){
		result += message.get(i).getName() + " ";
	}
			
	assertEquals(TEST_ITEMS, result);
}


	
}
