package global;

import java.util.Date;

/**
 * This is a data structure to store the details of a task
 */
public class Task {
	/*
	 * Declaration of variables
	 */
	String name;
	Date date;

	/*
	 * Constructor
	 */
	public Task(String name) {
		this.name = name;
	}
	
	public Task(String name, Date date) {
		this(name);
		this.date = date;
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
	public Boolean replaceName(String newName) {
		this.name = newName;
		return true;
	}
	
	public boolean setDate(Date date) {
		this.date = date;
		return true;
	}

}


