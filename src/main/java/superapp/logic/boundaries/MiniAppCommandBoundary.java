package superapp.logic.boundaries;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.Map;


public class MiniAppCommandBoundary {

	@NotNull
	private CommandId commandId;
	@NotNull @NotEmpty
	private String command;
	@NotNull
	private TargetObject targetObject;
	private Date invocationTimestamp;
	@NotNull
	private InvokedBy invokedBy;
	private Map<String,Object> commandAttributes;


	public MiniAppCommandBoundary() {}

	public CommandId getCommandId() {
		return commandId;
	}

	public MiniAppCommandBoundary setCommandId(CommandId commandId) {
		this.commandId = commandId;
		return this;
	}

	public String getCommand() {
		return command;
	}

	public MiniAppCommandBoundary setCommand(String command) {
		this.command = command;
		return this;
	}

	public TargetObject getTargetObject() {
		return targetObject;
	}

	public MiniAppCommandBoundary setTargetObject(TargetObject targetObject) {
		this.targetObject = targetObject;
		return this;
	}

	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}

	public MiniAppCommandBoundary setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
		return this;
	}

	public InvokedBy getInvokedBy() {
		return invokedBy;
	}

	public MiniAppCommandBoundary setInvokedBy(InvokedBy invokedBy) {
		this.invokedBy = invokedBy;
		return this;
	}

	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}

	public MiniAppCommandBoundary setCommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
		return this;
	}

	@Override
	public String toString() {
		return "MiniAppCommandBoundary{" +
				"commandId=" + commandId +
				", command='" + command + '\'' +
				", targetObject=" + targetObject +
				", invocationTimestamp=" + invocationTimestamp +
				", invokedBy=" + invokedBy +
				", commandAttributes=" + commandAttributes +
				'}';
	}
}
