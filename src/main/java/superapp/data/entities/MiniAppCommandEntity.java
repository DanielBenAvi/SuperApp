package superapp.data.entities;

import java.util.Date;
import java.util.Map;



public class MiniAppCommandEntity {
	
	private String commandId; 
	private String command;
	private String targetObject;
	private Date invocationTimestamp;
	private String invokedBy;
	private Map<String, Object> commandAttributes;
	
	
	public String getCommandId() {
		return commandId;
	}
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getTargetObject() {
		return targetObject;
	}
	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}
	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}
	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}
	public String getInvokedBy() {
		return invokedBy;
	}
	public void setInvokedBy(String invokedBy) {
		this.invokedBy = invokedBy;
	}
	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}
	public void setCommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
	}
	@Override
	public String toString() {
		return "MiniAppCommandEntity [commandId=" + commandId + ", command=" + command + ", targetObject="
				+ targetObject + ", invocationTimestamp=" + invocationTimestamp + ", invokedBy=" + invokedBy
				+ ", commandAttributes=" + commandAttributes + "]";
	}
	
	
	
}
