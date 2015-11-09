package logic;
import global.Command;

import java.util.ArrayList;
import java.util.logging.Logger;

import parser.Parser;

/**
 * This file contains the history class in which the Logic pushes its reversed command to
 */
public class History {
    Logger logger = Logger.getGlobal();
    ArrayList<Command> commandHistoryList  = new ArrayList<Command>();
    ArrayList<Command> commandUndoHistoryList = new ArrayList<Command>();
    
    
    boolean clearUndoHistoryList(){
        commandUndoHistoryList.clear();
        return true;
    }
    boolean pushCommand(Command commandObject, boolean isForUndo) {
        if (isForUndo) {
            commandHistoryList.add(commandObject);
            //commandUndoHistoryList.clear();
        } else {
            commandUndoHistoryList.add(commandObject);
        }
        return true;
    }
    
    boolean pushUndoCommand(Command commandObject) {
        commandHistoryList.add(commandObject);
        return true;
    }
    
    Command getPreviousCommand(boolean isForUndo) {
        ArrayList<Command> arrayListToUse = (isForUndo) ? commandHistoryList : commandUndoHistoryList; 
        int historySize = arrayListToUse.size();
        if (historySize > 0) {
            Command commandObjectToReturn = arrayListToUse.get(historySize - 1);
            arrayListToUse.remove(historySize - 1);
            return commandObjectToReturn;
        } else {
            return null;
        }
    }
}
