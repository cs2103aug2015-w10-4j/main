package global;

import java.util.ArrayList;

public class Command {

    private Type commandType;
    private ArrayList<String> argumentList;
    private ArrayList<Task> tasks;
    
    public enum Type {
        ADD, EDIT, DELETE, DISPLAY, EXIT, SAVETO, UNDO, REDO, MARK, UNMARK, SEARCH, HELP, ALIAS;
    }
    
    //@@author A0124093M
    public Command(Type commandType) { 
        setCommandType(commandType);
        this.tasks = null;
        this.argumentList = null;
    }
    
    //@@author A0124093M
    public Command(Type commandType, Task task) { 
        setCommandType(commandType);
        addTask(task);
        this.argumentList = null;
    }
    
    //@@author A0124093M
    public Command(Type commandType, String[] args) { 
        setCommandType(commandType);
        setArguments(args);
        this.tasks = null;
    }
    
    //@@author A0124093M
    public Command(Type commandType, String[] args, Task task) { 
        setCommandType(commandType);
        addTask(task);
        setArguments(args);
    }
    
    public Command(Type commandType, String[] args, ArrayList<Task> tasks) { 
        setCommandType(commandType);
        addTasks(tasks);
        setArguments(args);
    }
    
    // --------------- getter methods --------------------
    //@@author A0124093M
    public Type getCommandType() {
        return commandType;
    }
    
    //@@author A0124093M
    public ArrayList<Task> getTasks() {
        return tasks;
    }
    
    //@@author A0124093M
    public Task getTask(int index) {
        if(tasks != null && tasks.size() > index) {
            return tasks.get(index);
        } else {
            return null;
        }
        
    }
    
    //@@author A0124093M
    public ArrayList<String> getArguments() {
        return argumentList;
    }
    
    // --------------- setter methods -------------------
    //@@author A0124093M
    public void setCommandType(Type commandType) {
        this.commandType = commandType;
    }
    
    //@@author A0124093M
    public void setTask(int index, Task task){
        this.tasks.set(index,task);
    }
    
    //@@author A0124093M
    public void addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<Task>();
        }
        this.tasks.add(task);
    }
    
    public void addTasks(ArrayList<Task> tasks) {
        for(int i = 0; i < tasks.size(); i++){
            addTask(tasks.get(i));
        }
    }
    
    public void setArguments(String[] args) {
        argumentList = new ArrayList<String>();
        for(int i = 0; i < args.length; i++){
            argumentList.add(args[i]);
        }
    }
    
    //-------------has methods------------------//
    public boolean hasArgumentList() {
        if(this.getArguments() == null) {
            return false;
        } 
        return true;
    
    }
    
    public boolean hasTasksList() {
        if(this.getTasks() == null) {
            return false;
        } 
        return true;
    }
    
    public boolean compareTo(Command cmd) {
        boolean isTypeSame = false;
        boolean isAListSame = false;
        boolean isTasksSame = false;
        
        if(cmd.getCommandType().equals(this.getCommandType())){
        isTypeSame = true;
        }
        
    if(cmd.hasArgumentList() && this.hasArgumentList()) {
        if(cmd.getArguments().size() == this.getArguments().size()) {
            int similarCount = 0;
        for(int i=0; i < cmd.getArguments().size(); i++ ) {
            if(cmd.getArguments().get(i).equals((this.getArguments().get(i)))) {
                similarCount ++;
            }
        }
        
        if(similarCount == cmd.getArguments().size()) {
            isAListSame = true;
        }
        
        }
    } else if(cmd.hasArgumentList() == false && this.hasArgumentList() == false) {
        
        isAListSame = true;
    }
        
    
    if(cmd.hasTasksList() && this.hasTasksList() ) {
        if(cmd.getTasks().size() == this.getTasks().size()) {
            String cmdStr = "";
            String thisStr = "";
        for(int i = 0; i < cmd.getTasks().size(); i++) {
            cmdStr += cmd.getTasks().get(i).getAllInfo() + " ";
            thisStr += this.getTasks().get(i).getAllInfo() + " ";
        }
        
        if(cmdStr.equals(thisStr)) {
            isTasksSame = true;
        }
        
        }
    } else if(cmd.hasTasksList() == false && this.hasTasksList() == false) {
        isTasksSame = true;
    }
        
        if(isTypeSame && isAListSame && isTasksSame) {
            return true;
        }
        return false;    
    }
}
