package global;

import java.util.Calendar;

/**
 * This is a data structure to store the details of a task
 */
public class Task {
	/*
	 * Declaration of variables
	 */
	private static final String PERIODIC_DAILY = "daily";
	private static final String PERIODIC_WEEKLY = "weekly";
	private static final String PERIODIC_MONTHLY = "monthly";
	String name = null;
	Calendar startingTime = null;
	Calendar endingTime = null;
	String location = null;
	String periodic = null;

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
	
	public Task(String name, Calendar startingTime, Calendar endingTime, String location) {
		this.name = name;
		this.startingTime = startingTime;
		this.endingTime = endingTime;
		this.location = location;
	}
	
	public Task(String name, Calendar startingTime, Calendar endingTime, String location,String periodic) {
		this.name = name;
		this.startingTime = startingTime;
		this.endingTime = endingTime;
		this.location = location;
		this.periodic = periodic;
		if(isCorrectPeriodic() == false){
			IllegalArgumentException e = new IllegalArgumentException();
			throw e;
		}
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
	
	public Calendar getStartingTime() {
		return this.startingTime;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public String getPeriodic() {
		return this.periodic;
	}
	
	public boolean hasStartingTime(){
		if (this.startingTime == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean hasEndingTime(){
		if (this.endingTime == null) {
			return false;
		} else {
			return true;
		}
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
	
	public boolean setStartingTime(Calendar startingTime) {
		this.startingTime = startingTime;
		return true;
	}
	
	public boolean setLocation(String location) {
		this.location = location;
		return true;
	}
	
	public boolean setPeriodic(String periodic) {
		this.periodic = periodic;
		return true;
	}
	
	//return false if periodic type is incorrect
	public boolean isCorrectPeriodic() {
		String currentPeriodic = getPeriodic();
		if (! currentPeriodic.equals(PERIODIC_DAILY) &&
				! currentPeriodic.equals(PERIODIC_WEEKLY) &&
				! currentPeriodic.equals(PERIODIC_MONTHLY)){
			return false;
		}
		return true;
	}
	
}
