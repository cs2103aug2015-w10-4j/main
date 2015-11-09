package global;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This is a data structure to store the details of a task
 */
public class Task implements Comparable<Task> {

    /*
     * Declaration of variables
     */
    private String name = null;
    private Calendar startingTime = null;
    private Calendar endingTime = null;
    private String location = null;
    private String periodicInterval = null;
    private String periodicRepeats = null;
    private boolean isDone = false;

    /*
     * Constructor
     */
    //@@author A0108355H
    public Task(String name) {
        this.name = name;
    }

    //@@author A0108355H
    public Task(String name, Calendar endingTime) {
        this.name = name;
        this.endingTime = endingTime;
    }

    //@@author A0108355H
    public Task(String name, Calendar endingTime, String location) {
        this.name = name;
        this.endingTime = endingTime;
        this.location = location;
    }

    //@@author A0108355H
    public Task(String name, Calendar startingTime, Calendar endingTime) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    //@@author A0108355H
    public Task(String name, Calendar startingTime, Calendar endingTime,
            String location) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.location = location;
    }

    //@@author A0108355H
    public Task(String name, Calendar startingTime, Calendar endingTime,
            String location, String periodicInterval, String periodicRepeats) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.location = location;
        this.periodicInterval = periodicInterval;
        this.periodicRepeats = periodicRepeats;
    }

    //@@author A0108355H
    public Task() {

    }

    /*
     * Public methods
     */

    //@@author A0108355H
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
    //@@author A0108355H
    public Calendar getEndingTime() {
        return this.endingTime;
    }

    //@@author A0108355H
    public Calendar getStartingTime() {
        return this.startingTime;
    }
    
    /**
     * Returns starting time if it not null,
     * else returns ending time (which may still be null)
     * 
     * This is mainly used to return the date this item is to be classified in
     * 
     * @return
     */
    //@@author A0108355H
    public Calendar getTime() {
        if (hasStartingTime()) {
            return this.startingTime;
        } else {
            return this.endingTime;
        }
    }

    //@@author A0108355H
    public String getLocation() {
        return this.location;
    }

    //@@author A0132760M
    public String getPeriodicInterval() {
        return this.periodicInterval;
    }
    
    //@@author A0132760M
    public String getPeriodicRepeats() {
        return this.periodicRepeats;
    }
    
    //@@author A0108355H
    public String getAllInfo() {
        return "Name: " + name + " Starting time: " + this.startingTime
                + " Ending Time: " + this.getEndingTime() + " Location: "
                + this.location + " Period Interval: " + this.periodicInterval
                + " Period Repeats: " + this.periodicRepeats + " Done: " + this.isDone;
    }

    //@@author A0108355H
    public boolean hasStartingTime() {
        if (this.startingTime == null) {
            return false;
        } else {
            return true;
        }
    }
    
    //@@author A0108355H
    public boolean hasName() {
        if(this.name != null) {
            return true;
        } 
            return false;        
    }
    
    //@@author A0108355H
    public boolean hasLocation() {
        if(this.location != null) {
            return true;
        }
        return false;
    }

    //@@author A0132760M
    public boolean hasPeriodicInterval() {
        if(this.periodicInterval != null) {
            return true;
        }
        return false;
    }
    
    //@@author A0132760M
    public boolean hasPeriodicRepeats() {
        if(this.periodicRepeats != null) {
            return true;
        }
        return false;
    }
    
    //@@author A0108355H
    public boolean hasEndingTime() {
        if (this.endingTime == null) {
            return false;
        } else {
            return true;
        }
    }
    
    //@@author A0132760M
    public boolean isDone(){
        return this.isDone;
    }

    //@@author A0108355H
    // change the name of the task
    public boolean setName(String newName) {
        this.name = newName;
        return true;
    }

    //@@author A0108355H
    public boolean setEndingTime(Calendar endingTime) {
        this.endingTime = endingTime;
        return true;
    }

    //@@author A0108355H
    public boolean setStartingTime(Calendar startingTime) {
        this.startingTime = startingTime;
        return true;
    }

    //@@author A0108355H
    public boolean setLocation(String location) {
        this.location = location;
        return true;
    }

    //@@author A0124093M
    public boolean setPeriodicInterval(String periodicInterval) {
        this.periodicInterval = periodicInterval;
        return true;
    }
         
    //@@author A0124093M
    public boolean setPeriodicRepeats(String periodicInstances) {
        this.periodicRepeats = periodicInstances;
        return true;
    }

    //@@author A0132760M
    public boolean setDone(boolean status) {
        this.isDone = status;
        return true;
    }
    
    public Task clone() {
        Task newTask = new Task();
        newTask.setName(this.getName());
        if (this.hasStartingTime()) {
            newTask.setStartingTime((Calendar) this.getStartingTime().clone());
        }
        if (this.hasEndingTime()) {
            newTask.setEndingTime((Calendar) this.getEndingTime().clone());
        }
        newTask.setLocation(this.getLocation());
        newTask.setPeriodicInterval(this.getPeriodicInterval());
        newTask.setPeriodicRepeats(this.getPeriodicRepeats());
        newTask.setDone(this.isDone());
        return newTask;
    }
    
    //@@author A0108355H
    public int compareTo(Task taskObj) {
        Calendar thisTime;
        Calendar objTime;
        if (this.hasStartingTime()) {
            thisTime = this.getStartingTime();
        } else {
            thisTime = this.getEndingTime();
        }

        if (taskObj.hasStartingTime()) {
            objTime = taskObj.getStartingTime();
        } else {
            objTime = taskObj.getEndingTime();
        }

        if (thisTime == null && objTime == null) {
            return 0;
        } else if (thisTime == null) {
            return -1;
        } else if (objTime == null) {
            return 1;
        } else {
            if (thisTime.before(objTime)) {
                return -1;
            } else if (thisTime.after(objTime)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
