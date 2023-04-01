package superapp.logic.boundaries;

import java.util.Map;

/**
 * @author Ido & Yosef
 */

public class CommandAttributes {
	private Map<String, Object> commandAttributes;

	public CommandAttributes() {
	}

	public CommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
	}

	
	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}

	public void setCommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
	}

	@Override
	public String toString() {
		return "CommandAttributes [commandAttributes=" + commandAttributes + "]";
	}

	
	
	
}
