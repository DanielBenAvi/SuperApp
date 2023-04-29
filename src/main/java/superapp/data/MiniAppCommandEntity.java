package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "MINI_APP_COMMANDS")
public class MiniAppCommandEntity {
	@Id	private String commandId;
	private String command;
	private String targetObject;
	private Date invocationTimestamp;
	private String invokedBy;
	private Map<String, Object> commandAttributes;

	public MiniAppCommandEntity(){
	}

	public String getCommandId() {
		return commandId;
	}

	public MiniAppCommandEntity setCommandId(String commandId) {
		this.commandId = commandId;
		return this;
	}

	public String getCommand() {
		return command;
	}

	public MiniAppCommandEntity setCommand(String command) {
		this.command = command;
		return this;
	}

	public String getTargetObject() {
		return targetObject;
	}

	public MiniAppCommandEntity setTargetObject(String targetObject) {
		this.targetObject = targetObject;
		return this;
	}

	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}

	public MiniAppCommandEntity setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
		return this;
	}

	public String getInvokedBy() {
		return invokedBy;
	}

	public MiniAppCommandEntity setInvokedBy(String invokedBy) {
		this.invokedBy = invokedBy;
		return this;
	}

	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}

	public MiniAppCommandEntity setCommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
		return this;
	}

	@Override
	public String toString() {
		return "MiniAppCommandEntity{" +
				"commandId='" + commandId + '\'' +
				", command='" + command + '\'' +
				", targetObject='" + targetObject + '\'' +
				", invocationTimestamp=" + invocationTimestamp +
				", invokedBy='" + invokedBy + '\'' +
				", commandAttributes=" + commandAttributes +
				'}';
	}
}
