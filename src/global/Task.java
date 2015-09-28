package global;

import java.util.Calendar;


/**
 * This is a data structure to store the details of a task
 */
public class Task {
	/*
	 * Declaration of variables
	 */
	String name;
	Calendar date;

	/*
	 * Constructor
	 */
	
	public Task(String name) {
		this.name = name;
	}
	
	public Task(String name, Calendar date) {
		this(name);
		this.date = date;
	}
	
	public Task() {
		this(null, null);
	}

	
	/*
	 * Public methods
	 */

	//returns the name of the task
	public String getName() {	
		return name;
	}

	//change the name of the task
	public boolean setName(String newName) {
		this.name = newName;
		return true;
	}
	
	public boolean setDate(Calendar date) {
		this.date = date;
		return true;
	}
	
	public Calendar getDate() {
		return date;
	}

}
