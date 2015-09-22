package global;

//data structure to store tasks

public class Task {
	String name;

	//constructor
	public Task(String name){
		this.name = name;
	}

	//public methods

	//return the name of the task
	public String getName(){	
		return name;
	}

	//change the name of the task
	public Boolean replaceName(String newName){
		this.name = newName;
		return true;
	}

}
