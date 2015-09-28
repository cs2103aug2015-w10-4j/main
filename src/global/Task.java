package global;

import java.sql.Time;
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
    
	//timeStr: year month date hour minute (2014 06 04 24 24)
	@SuppressWarnings("deprecation")
	public Task(String name,String timeStr){
		String [] timeArr = timeStr.split(" ");
		this.date = new Date(Integer.parseInt(timeArr[0]) , Integer.parseInt(timeArr[1]) , Integer.parseInt(timeArr[2]) ,
				Integer.parseInt(timeArr[3]) , Integer.parseInt(timeArr[4]));
		this.name = name;
	}
	
	/*
	 * Accessor
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
	@SuppressWarnings("deprecation")
	public String getTime() {
		return date.getYear() + " " + date.getMonth() + " " + 
	date.getDate() + " " + date.getHours() + " " +date.getMinutes();
	}

	/*
	 * mutator
	 */

	//change the name of the task
	public Boolean replaceName(String newName) {
		this.name = newName;
		return true;
	}
	
	/**
	 * always return true currently
	 * @return  time, format : year month date hour minute (2014 06 04 24 24)
	 * @SuppressWarnings("deprecation")
	 */
	@SuppressWarnings("deprecation")
	public boolean setTime(String timeStr){
		String [] timeArr = timeStr.split(" ");
		this.date.setYear(Integer.parseInt(timeArr[0]));
		this.date.setMonth(Integer.parseInt(timeArr[1]));
		this.date.setDate(Integer.parseInt(timeArr[2]));
		this.date.setHours(Integer.parseInt(timeArr[3]));
		this.date.setMinutes(Integer.parseInt(timeArr[4]));
	return true;
	}

}
