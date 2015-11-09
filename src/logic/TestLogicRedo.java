package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicRedo {
    
Logic logicObject;

     //@@author A0108355H
    @Before
    public void setup(){
        logicObject = new Logic();
        File saveFile = new File("save.txt");
        saveFile.delete();
    }

    //@@author A0108355H
    @Test
    public void logicRedoEmpty(){

        String message = logicObject.redoCommand();        
        assertEquals("Error: No history found.", message);
        

    }
    
    //@@author A0108355H
    @Test
    public void logicRedoadd(){
        ArrayList<Task> newTasks = new ArrayList<Task>();
        newTasks.add(new Task("item 1"));
        logicObject.addItem(newTasks, new ArrayList<Integer>(), true, true);
        logicObject.undoCommand();

        String message = logicObject.redoCommand();
        assertEquals("Redo : Deleted item(s) restored.", message);
        assertEquals("item 1", logicObject.listOfTasks.get(0).getName());
    }

    //@@author A0108355H
    @Test
    public void logicRedoMultipleDelete(){

        logicObject.listOfTasks = new ArrayList<Task>();
        logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));    
        logicObject.listOfTasks.add(new Task("some item 3"));
        logicObject.listOfTasks.add(new Task("some item 4"));
        logicObject.listOfTasks.add(new Task("some item 5"));    
        logicObject.listOfTasks.add(new Task("some item 6"));
        
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        
        indexList.add(0);
        indexList.add(2);
        indexList.add(1);
        
        logicObject.deleteItem(indexList, true, true);
    
        String message = logicObject.undoCommand();        
        assertEquals("Undo : Deleted item(s) restored.", message);
        assertEquals("some item 1", logicObject.listOfTasks.get(0).getName());
        assertEquals("some item 2", logicObject.listOfTasks.get(1).getName());
        assertEquals("some item 3", logicObject.listOfTasks.get(2).getName());
        assertEquals("some item 4", logicObject.listOfTasks.get(3).getName());
        assertEquals("some item 5", logicObject.listOfTasks.get(4).getName());
        assertEquals("some item 6", logicObject.listOfTasks.get(5).getName());
        logicObject.showUpdatedItems();
        message = logicObject.redoCommand();    
        assertEquals("Redo : Added item(s) removed.", message);
        

    }

    //@@author A0108355H
    @Test
    public void logicUndoEdit(){
        ArrayList<Task> listToEdit = new ArrayList<Task>();
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        logicObject.listOfTasks.add(new Task("some item 2"));    
        logicObject.listOfTasks.add(new Task("some item 3"));
        
        indexList.add(0);
        listToEdit.add(new Task("New item 1"));

        logicObject.editItem(listToEdit, indexList, true, true);
        

        logicObject.undoCommand();
        String message = logicObject.redoCommand();        
        assertEquals("Redo : Reverted edits.", message);
        
        assertEquals("New item 1", logicObject.listOfTasks.get(0).getName());
        assertEquals("some item 3", logicObject.listOfTasks.get(1).getName());

    }
    
    //@@author A0108355H
    @After
    public void cleanup(){
        File saveFile = new File("save.txt");
        File anotherSaveFile = new File("anotherSave.txt");
        saveFile.delete();
        anotherSaveFile.delete();
    }
}
