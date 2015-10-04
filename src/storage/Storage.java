package storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import global.Task;

public interface Storage {
	
	public boolean writeItemList(ArrayList<Task> tasks) throws IOException;

	public boolean saveFileToPath(String path) throws IOException;
	
	public ArrayList<Task> getItemList() throws FileNotFoundException;
	
}
