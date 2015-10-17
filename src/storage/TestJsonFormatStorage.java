package storage;

// enable saveFile.delete() at line 89 for testing 
// comment saveFile.delete(); at line 79 after done testing
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

	@Before
	public void setup(){
	//	try {
			File newSaveFile = new File("newsave.txt");
			newSaveFile.delete();
			storageObject = new JsonFormatStorage();
			result = new ArrayList<Task>();
			String[] strArr = TEST_ITEMS.split(" ");
			for(int i = 0; i < strArr.length; i++){
				Task newTask = new Task(strArr[i]);
				result.add(newTask);
			}
	
			File saveFile = new File("save1.txt");
	/*		BufferedWriter buffWriter = new BufferedWriter(new FileWriter(saveFile));
			buffWriter.write(TEST_ITEMS);
			buffWriter.close();
		} catch (IOException e){
		
		}
	*/
	//File saveFile = new File("save.txt");
	//saveFile.delete();
	}

	@Test
	public void testWriteItemList() throws IOException{
	//Task newTask = new Task("item 1");
		boolean message = storageObject.writeItemList(result);
		assertEquals(true, message);
	}

	@Test
	public void testSaveFileToPath() throws IOException{
		boolean message = storageObject.saveFileToPath("newsave.txt");
		assertEquals(true, message);
	}

	@Test
	public void testGetItemList() throws IOException{
	
		ArrayList<Task> message = storageObject.getItemList();
		String resultStr = "";
		
		for(int i = 0; i < message.size(); i++){
			resultStr += message.get(i).getName();
		}
			
		assertEquals(TEST_ITEMS, resultStr);
	}

	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("newsave.txt");
	//	saveFile.delete();
		anotherSaveFile.delete();
	}

	
}
