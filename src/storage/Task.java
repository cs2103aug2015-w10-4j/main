package storage;

//data structure to store tasks

public class Task {
String name;
int index;

//constructor
public Task(String name){
    this.name = name;
}

public Task(int index){
	this.index = index;
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

public int getIndex(){
	return index;
}

public Boolean replaceIndex(int index){
	this.index = index;
	return true;
}

}
