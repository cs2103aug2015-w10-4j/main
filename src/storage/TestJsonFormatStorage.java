package storage;

// enable saveFile.delete() at line 89 for testing 
// comment saveFile.delete(); at line 79 after done testing
// from martin:
// 1. Don't need to do so... the line will only execute when the tests are run

// 2. Also, the latter tests are not dependent on the earlier ones:
// e.g. in test 1, you add item 1, item 2, item 3
// in test 2, item 1, item 2 and item 3 will not exist

// 3. The tests also follow this order:
// Before->Test1->After
// Before->Test2->After
// ...
// and NOT
// Before->Test1->Test2->...->After
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import global.Task;

public class TestJsonFormatStorage {
	JsonFormatStorage storageObject;
//	Storage storageObject;
	ArrayList<Task> result ;
	String TEST_ITEMS = "item11\nitem12\nitem13\n";

	//@@author A0108355H
	@Before
	public void setup(){
		try {
			File newSaveFile = new File("newsave.txt");
			newSaveFile.delete();
			File saveFile = new File("save.txt");
			saveFile.delete();
			storageObject = new JsonFormatStorage(true);
			result = new ArrayList<Task>();
			
			String[] strArr = TEST_ITEMS.split(" ");
			for(int i = 0; i < strArr.length; i++){
				Task newTask = new Task(strArr[i]);
				result.add(newTask);
			}
			
			storageObject.writeItemList(result); // should manually write this in json
												// instead of calling the function
	
			/*
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(saveFile));
			buffWriter.write(TEST_ITEMS);
			buffWriter.close();
			*/
		} catch (IOException e){
			e.printStackTrace();
		}
	
	}

	//@@author A0108355H
	//test write tasks to file
	@Test
	public void testWriteItemList() throws IOException{
	//Task newTask = new Task("item 1");
		boolean message = storageObject.writeItemList(result);
		assertEquals(true, message);
	}

	//@@author A0108355H
	@Test
	public void testSaveFileToPath() throws IOException{
		boolean message = storageObject.saveFileToPath("newsave.txt");
		assertEquals(true, message);
	}

	//@@author A0108355H
	@Test
	public void testGetItemList() throws IOException{
		ArrayList<Task> message = storageObject.getItemList();
		String resultStr = "";
		
		for(int i = 0; i < message.size(); i++){
			resultStr += message.get(i).getName();
		}
			
		assertEquals(TEST_ITEMS, resultStr);
	}

	//@@author A0108355H
	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("newsave.txt");
		saveFile.delete();
		anotherSaveFile.delete();
	}

	
}
