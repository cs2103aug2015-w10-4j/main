package history;
import global.Command;
import java.util.ArrayList;

public class History {
	ArrayList<Command> commandHistoryList  = new ArrayList<Command>();
	public boolean pushCommand(Command commandObject){
		commandHistoryList.add(commandObject);
		return true;
	}
	public Command getPreviousCommand(){
		int historySize = commandHistoryList.size();
		if(historySize > 0){
			Command commandObjectToReturn = commandHistoryList.get(historySize - 1);
			commandHistoryList.remove(historySize - 1);
			return commandObjectToReturn;
		}else{
			return null;
		}
	}
}
