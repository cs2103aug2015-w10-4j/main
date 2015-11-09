package ui;

import java.util.ArrayList;
import java.util.List;

//@@author A0134155M
public class UserInputHistory {
    
    private static final String EMPTY_STRING = "";
    
    private List<String> userInputs = new ArrayList<>();
    private int position = 0;
    
    /**
     * Add user input to user input history list.
     * @param userInput
     */
    public void addToHistory(String userInput) {
        userInputs.add(userInput);
        position = userInputs.size();
    }
    
    /**
     * Move the pointer in user input history list to the older
     * one. If the pointer is already pointing to the oldest one,
     * nothing happens to the pointer.
     * @return user input referred by the pointer
     */
    public String moveUpInHistory() {
        if (position > 0) {
            position--;
        }
        return getPosition(position);
    }
    
    /**
     * Move the pointer in user input history list to the more
     * recent one. If the pointer is already pointing to the most
     * recent one, nothing happens to the pointer.
     * @return user input referred by the pointer
     */
    public String moveDownInHistory() {
        if (position + 1 <= userInputs.size()) {
            position++;
        }
        return getPosition(position);
    }
    
    /**
     * Get user input at position according to the parameter
     * If position is equal to the size of userInput that is
     * already recorded, it returns empty string
     * @param position
     * @return user input at the requested position
     */
    public String getPosition(int position) {
        if (position < 0 || position > userInputs.size()) {
            throw new IndexOutOfBoundsException();
        }

        if (position == userInputs.size()) {
            return EMPTY_STRING;
        } else {
            return userInputs.get(position);
        }
    }
}
