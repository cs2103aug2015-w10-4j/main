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
	Calendar startingTime = null;
	Calendar endingTime = null;
	String location = null;

	/*
	 * Constructor
	 */
	public Task(String name) {
		this.name = name;
	}
	
	public Task(String name, Calendar endingTime) {
		this(name);
		this.endingTime = endingTime;
	}
	
	public Task(String name, Calendar endingTime, String location) {
		this(name);
		this.endingTime = endingTime;
		this.location = location;
	}
	
	public Task(String name, Calendar startingTime, Calendar endingTime) {
		this(name);
		this.startingTime = startingTime;
		this.endingTime = endingTime;
	}
	
	public Task(String name, Calendar startingTime, Calendar endingTime, String location) {
		this(name);
		this.startingTime = startingTime;
		this.endingTime = endingTime;
		this.location = location;
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
	 * @return   time, format : year month Time hour minute (2014 06 04 24 24)
	 * @SuppressWarnings("deprecation")
	 */
	public Calendar getEndingTime() {
		return this.endingTime;
	}
	
	public Calendar getStartingTime(){
		return this.startingTime;
	}
	
	public String getLocation(){
		return location;
	}


	//change the name of the task
	public boolean setName(String newName) {
		this.name = newName;
		return true;
	}
	
	public boolean setEndingTime(Calendar endingTime) {
		this.endingTime = endingTime;
		return true;
	}
	
	public boolean setStartingTime(Calendar startingTime){
		this.startingTime = startingTime;
		return true;
	}
	
	public boolean setLocation(String location){
		this.location = location;
		return true;
	}
	
	
	
}


