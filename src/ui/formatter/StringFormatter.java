package ui.formatter;

public class StringFormatter {
	
	/* 
	 * To align left a string S with a particular width W,
	 * one can use : String.format("%-Ws", S); (format = "%-Ws", args = S)
	 * Since W is a variable and we cannot specify W directly,
	 * we create another string as a format of this String.format, using
	 * another String.format
	 */
	private static final String ALIGN_LEFT_ARGS_ARGS = "%%-%ds";
	
	/*
	 * Similar to the above, the difference is that to align center
	 * is similar to align right with some width W1 (for the string
	 * and spaces on the left), and W2 (for the spaces on the right).
	 * Thus, the actual String.format format that we use is :
	 * String.format("%-W1s%W2s", S, "") where S is the string that
	 * we want to format.
	 */
	private static final String ALIGN_CENTER_ARGS_ARGS = "%%%ds%%%ds";
	
	/*
	 * Similar to the ALIGN_LEFT one, but in this case, we don't need
	 * the minus sign for the width.
	 */
	private static final String ALIGN_RIGHT_ARGS_ARGS = "%%%ds";
	
	public static enum Alignment {
		ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT
	};
	
	//@@author A0134155M
	/**
	 * Format a string so that its length would be equal to <code>width</code>
	 * and it will be aligned according to <code>alignment</code>
	 * @param str the string to be formatted
	 * @param alignment the alignment of the initial string in the resulting string.
	 * @param width the final width of the resulting string.
	 * @return
	 */
	public static String formatString(String str, Alignment alignment, int width) {
		assert str != null;
		assert width >= str.length() : "Width must be larger than or equal to str";
		
		String result = null;
		String stringFormatArgs;
		
		if (width >= str.length()) {
			switch (alignment) {
				case ALIGN_LEFT :
					stringFormatArgs = String.format(ALIGN_LEFT_ARGS_ARGS, width);
					result = String.format(stringFormatArgs, str);
					break;
				case ALIGN_CENTER :
					int spaceOnLeft = (width - str.length()) / 2;
					int spaceOnRight = width - str.length() - spaceOnLeft;
					stringFormatArgs = String.format(ALIGN_CENTER_ARGS_ARGS, spaceOnLeft + str.length(),
																		  spaceOnRight);
					result = String.format(stringFormatArgs, str, "");
					break;
				case ALIGN_RIGHT :
					stringFormatArgs = String.format(ALIGN_RIGHT_ARGS_ARGS, width);
					result = String.format(stringFormatArgs, str);
					break;
				default :
					assert false;
			}
		}
		
		assert result != null;
		return result;
	}
}
