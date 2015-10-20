package ui;

import java.util.ArrayList;
import java.util.List;

public class UserInputHistory {
	private List<String> userInputs = new ArrayList<>();
	private int position = 0;
	
	public void addToHistory(String userInput) {
		userInputs.add(userInput);
		position = userInputs.size();
	}
	
	public String moveUpInHistory() {
		if (position > 0) {
			position--;
		}
		return getPosition(position);
	}
	
	public String moveDownInHistory() {
		if (position + 1 <= userInputs.size()) {
			position++;
		}
		return getPosition(position);
	}
	
	public String getPosition(int position) {
		if (position == userInputs.size()) {
			return "";
		} else {
			return userInputs.get(position);
		}
	}
}
