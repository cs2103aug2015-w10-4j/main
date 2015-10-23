package global;

import java.util.Calendar;

/**
 * This is a data structure to store the details of a task
 */
public class Task {
	/*
	 * Declaration of variables
	 */

	private String name = null;
	private Calendar startingTime = null;
	private Calendar endingTime = null;
	private String location = null;
	private String periodicInterval = null;
	private String periodicRepeats = null;
	private Boolean isDone = false;

	/*
	 * Constructor
	 */
	public Task(String name) {
		this.name = name;
	}

	public Task(String name, Calendar endingTime) {
		this.name = name;
		this.endingTime = endingTime;
	}

	public Task(String name, Calendar endingTime, String location) {
		this.name = name;
		this.endingTime = endingTime;
		this.location = location;
	}

	public Task(String name, Calendar startingTime, Calendar endingTime) {
		this.name = name;
		this.startingTime = startingTime;
		this.endingTime = endingTime;
	}

	public Task(String name, Calendar startingTime, Calendar endingTime,
			String location) {
		this.name = name;
		this.startingTime = startingTime;
		this.endingTime = endingTime;
		this.location = location;
	}

	public Task(String name, Calendar startingTime, Calendar endingTime,
			String location, String periodicInterval, String periodicRepeats) {
		this.name = name;
		this.startingTime = startingTime;
		this.endingTime = endingTime;
		this.location = location;
		this.periodicInterval = periodicInterval;
		this.periodicRepeats = periodicRepeats;
	}

	public Task() {

	}

	/*
	 * Public methods
	 */

	// returns the name of the task
	public String getName() {
		return name;
	}

	/**
	 * return time in string format
	 * 
	 * @return time, format : year month Time hour minute (2014 06 04 24 24)
	 * @SuppressWarnings("deprecation")
	 */
	public Calendar getEndingTime() {
		return this.endingTime;
	}

	public Calendar getStartingTime() {
		return this.startingTime;
	}

	public String getLocation() {
		return this.location;
	}

	public String getPeriodicInterval() {
		return this.periodicInterval;
	}

	public String getPeriodicRepeats() {
		return this.periodicRepeats;
	}
	
	public String getAllInfo() {
		return "Name: "+ name + " Starting time: " + this.startingTime + " Ending Time: "+ this.getEndingTime() +
				" Location: " +this.location + " Period Interval: "+ this.periodicInterval + " Period Repeats " + this.periodicRepeats;
	}

	public boolean hasStartingTime() {
		if (this.startingTime == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean hasName() {
		if(this.name != null) {
			return true;
		} 
			return false;		
	}
	
	public boolean hasLocation() {
		if(this.location != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasPeriodicInterval() {
		if(this.periodicInterval != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasPeriodicRepeats() {
		if(this.periodicRepeats != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasEndingTime() {
		if (this.endingTime == null) {
			return false;
		} else {
			return true;
		}
	}
	public boolean isDone(){
		return this.isDone;
	}

	// change the name of the task
	public boolean setName(String newName) {
		this.name = newName;
		return true;
	}

	public boolean setEndingTime(Calendar endingTime) {
		this.endingTime = endingTime;
		return true;
	}

	public boolean setStartingTime(Calendar startingTime) {
		this.startingTime = startingTime;
		return true;
	}

	public boolean setLocation(String location) {
		this.location = location;
		return true;
	}

	public boolean setPeriodicInterval(String periodicInterval) {
		this.periodicInterval = periodicInterval;
		return true;
	}
	public boolean setPeriodicRepeats(String periodicInstances) {
		this.periodicRepeats = periodicInstances;
		return true;
	}
	public boolean setDone(boolean status){
		this.isDone = status;
		return true;
	}
	
	public Task clone(){
		Task newTask = new Task();
		newTask.setName(this.getName());
		newTask.setStartingTime(this.getStartingTime());
		newTask.setEndingTime(this.getEndingTime());
		newTask.setLocation(this.getLocation());
		newTask.setPeriodicInterval(this.getPeriodicInterval());
		newTask.setPeriodicRepeats(this.getPeriodicRepeats());
		newTask.isDone = this.isDone();
		return newTask;
	}

}
