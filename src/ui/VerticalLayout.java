package ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
	
	public VerticalLayout() {
		super();
	}
	
	@Override
	public void addLayoutComponent(Component comp, Object constraint) {
		assert comp != null : "Cannot add null Component";
		assert constraint == null : "Can only be called from java.awt.Container.add(Component comp)";
		
		resetLastComponentWeightY();
		
		GridBagConstraints newComponentConstraint = new GridBagConstraints();
		newComponentConstraint.weightx = COMPONENT_WEIGHT_X;
		newComponentConstraint.weighty = COMPONENT_WEIGHT_Y;
		newComponentConstraint.gridy = componentCount;
		newComponentConstraint.gridx = COMPONENT_POS_X;
		newComponentConstraint.gridheight = COMPONENT_GRID_HEIGHT;
		newComponentConstraint.gridwidth = COMPONENT_GRID_WIDTH;
		newComponentConstraint.fill = COMPONENT_FILL;
		
		super.addLayoutComponent(comp, newComponentConstraint);
		componentCount++;
		lastComponent = comp;
	}
	
	private void resetLastComponentWeightY() {
		if (lastComponent != null) {
			GridBagConstraints oldComponentConstraint = new GridBagConstraints();
			oldComponentConstraint.weightx = COMPONENT_WEIGHT_X;
			oldComponentConstraint.weighty = NEUTRAL_WEIGHT;
			oldComponentConstraint.gridy = componentCount - 1;
			oldComponentConstraint.gridx = COMPONENT_POS_X;
			oldComponentConstraint.gridheight = COMPONENT_GRID_HEIGHT;
			oldComponentConstraint.gridwidth = COMPONENT_GRID_WIDTH;
			oldComponentConstraint.fill = COMPONENT_FILL;
			
			super.setConstraints(lastComponent, oldComponentConstraint);
		}
	}
	
	public void resetLayout() {
		componentCount = 0;
		lastComponent = null;
	}
}
