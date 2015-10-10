package storage;

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

public class TestManualFormatStorage {
	Storage storageObject;
	ArrayList<Task> result ;
	String TEST_ITEMS = "item1\nitem2\nitem3\n";

	@Before
	public void setup(){
		try {
			File newSaveFile = new File("newsave.txt");
			newSaveFile.delete();
			storageObject = new ManualFormatStorage();
			result = new ArrayList<Task>();
			String[] strArr = TEST_ITEMS.split(" ");
			for(int i = 0; i < strArr.length; i++){
				Task newTask = new Task(strArr[i]);
				result.add(newTask);
			}
	
			File saveFile = new File("save.txt");
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(saveFile));
			buffWriter.write(TEST_ITEMS);
			buffWriter.close();
		} catch (IOException e){
		
		}
	
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
		assertEquals(false, message);
	}

	@Test
	public void testGetItemList() throws IOException{
	
		ArrayList<Task> message = storageObject.getItemList();
		String result = "";
		for(int i = 0; i < message.size(); i++){
			result += message.get(i).getName() + "\n";
		}
			
		assertEquals(TEST_ITEMS, result);
	}

	@After
	public void cleanup(){
		File saveFile = new File("save.txt");
		File anotherSaveFile = new File("newsave.txt");
		saveFile.delete();
		anotherSaveFile.delete();
	}

	
}
