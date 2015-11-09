package storage;

import global.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public interface Storage {
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final String HELP_PATH = "help.txt";
	static final String HELP_MESSAGE =
"Commands   Description                                         Example usage\n"+
"add        Create new task entry with optional fields:         add task123\n"+
"           - Deadline: 'by'/'from...to'/'start...end'          add task123 by tomorrow 6PM\n"+
"           - Location: 'at'/'loc'                              add task123 at playground\n"+
"           - Periodic: 'every n days/weeks/years for n times'  add task123 by next thursday every 3 weeks for 10 times\n"+
"\n"+
"display    Display the default view of Tasky                   display\n"+
"\n"+
"edit       Edit task entry at specified index                  edit 1 task456 from 12 sep to 15 sep\n"+
"                                                               edit 1 at town\n"+
"\n"+
"delete     Delete task entry at specified index                delete 1\n"+
"\n"+
"undo       Undo previous add, edit or delete                   undo\n"+
"\n"+
"redo       Redo previous add, edit or delete                   redo\n"+
"\n"+
"search     Search for a task with the specified keyword        search task456\n"+
"           or field+value                                      search loc home\n"+
"                                                               search by 18 sep\n"+
"\n"+
"mark       Marks a task at specified index as done             mark 1\n"+
"\n"+
"unmark     Unmarks a task at specified index as undone         unmark 1\n"+ 
"\n"+
"saveto     Specify the path of the savefile                    saveto new_file.txt\n"+
"\n"+
"exit       Exits the program                                   exit\n";
	static final String ERROR_HELP = "Unable to retrieve help file!";

	/**
	 * Saves the list of tasks in the file
	 * @param ArrayList<Task> ArrayList that stores the RAW tasks as Strings in text file
	 * @return true if file is saved
	 * @throws IOException 
	 */
	public boolean writeItemList(ArrayList<Task> tasks) throws IOException;

	/**
	 * Saves path to text file
	 *
	 * @param path path is a String contain the path of file to save
	 * @return true if location changes
	 * @throws IOException
	 */
	public boolean saveFileToPath(String path) throws IOException;

	/**
	 * Reads the saved file and returns the ArrayList of tasks
	 * @return ArrayList of tasks read from file
	 * @throws FileNotFoundException if there is no file in the filePath
	 */
	public ArrayList<Task> getItemList() throws FileNotFoundException;
	
	//@@author A0124093M
	public default String getHelpMessage() {
		return HELP_MESSAGE;
	}
	
}
