package ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@SuppressWarnings("serial")
public class VerticalLayout extends GridBagLayout {
	
	private static final double COMPONENT_WEIGHT_X = 1.0;
	private static final double COMPONENT_WEIGHT_Y = 1.0;
	private static final double NEUTRAL_WEIGHT = 0.0;
	private static final int COMPONENT_POS_X = 0;
	private static final int COMPONENT_GRID_HEIGHT = 1;
	private static final int COMPONENT_GRID_WIDTH = 1;
	private static final int COMPONENT_FILL = GridBagConstraints.BOTH;

	private int componentCount = 0;
	private Component lastComponent = null;
	private GridBagConstraints lastComponentConstraint = null;
	
	//@@author A0134155M
	public VerticalLayout() {
		super();
	}
	
	//@@author A0134155M
	@Override
	public void addLayoutComponent(Component comp, Object constraint) {
		assert comp != null : "Cannot add null Component";
		assert constraint == null ||
				constraint instanceof Insets : "Can only accept Insets as constraint";
		
		resetLastComponentWeightY();
		
		GridBagConstraints newComponentConstraint = new GridBagConstraints();
		newComponentConstraint.weightx = COMPONENT_WEIGHT_X;
		newComponentConstraint.weighty = COMPONENT_WEIGHT_Y;
		newComponentConstraint.gridy = componentCount;
		newComponentConstraint.gridx = COMPONENT_POS_X;
		newComponentConstraint.gridheight = COMPONENT_GRID_HEIGHT;
		newComponentConstraint.gridwidth = COMPONENT_GRID_WIDTH;
		newComponentConstraint.fill = COMPONENT_FILL;
		if (constraint != null) {
			newComponentConstraint.insets = (Insets) constraint;
		}
		
		super.addLayoutComponent(comp, newComponentConstraint);
		componentCount++;
		lastComponent = comp;
		lastComponentConstraint = newComponentConstraint;
	}
	
	//@@author A0134155M
	private void resetLastComponentWeightY() {
		if (lastComponent != null) {
			lastComponentConstraint.weighty = NEUTRAL_WEIGHT;
			super.setConstraints(lastComponent, lastComponentConstraint);
		}
	}
	
	//@@author A0134155M
	public void resetLayout() {
		componentCount = 0;
		lastComponent = null;
	}
}
