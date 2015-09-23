package global;

/**
 * This is a data structure to store the details of a task
 */
public class Task {
	/*
	 * Declaration of variables
	 */
	String name;

	/*
	 * Constructor
	 */
	public Task(String name) {
		this.name = name;
	}

	
	/*
	 * Public methods
	 */

	//returns the name of the task
	public String getName() {	
		return name;
	}

	//change the name of the task
	public Boolean replaceName(String newName) {
		this.name = newName;
		return true;
	}

}
