package storage;

public class Task {
String name;

//constructor
public Task(String name){
this.name = name;
}

//public methods
public String getName(){
	
	return name;
}

public Boolean replaceName(String newName){
	this.name = newName;
	return true;
}
}
