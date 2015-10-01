package global;

import java.util.Calendar;

/**
 * This is a data structure to store the details of a task
 */
public class Task {
	/*
	 * Declaration of variables
	 */
	String name = null;
	Calendar date = null;

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
		
	}

	
	/*
	 * Public methods
	 */

	//returns the name of the task
	public String getName() {	
		return name;
	}
	
	/**
	 * return time in string format
	 * @return   time, format : year month date hour minute (2014 06 04 24 24)
	 * @SuppressWarnings("deprecation")
	 */
	public Date getTime() {
		return this.date;
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


