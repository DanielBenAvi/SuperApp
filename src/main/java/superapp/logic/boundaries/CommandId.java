package superapp.logic.boundaries;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CommandId {

	@NotNull @NotEmpty
	private String superapp;
	@NotNull @NotEmpty
	private String miniapp;
	@NotNull @NotEmpty
	private String internalCommandId;
	
	
	public CommandId() {}

	public CommandId(String superapp, String miniapp, String internalCommandId) {
		this.superapp = superapp;
		this.miniapp = miniapp;
		this.internalCommandId = internalCommandId;
	}

	public String getSuperapp() {
		return superapp;
	}

	public CommandId setSuperapp(String superapp) {
		this.superapp = superapp;
		return this;
	}

	public String getMiniapp() {
		return miniapp;
	}

	public CommandId setMiniapp(String miniapp) {
		this.miniapp = miniapp;
		return this;
	}

	public String getInternalCommandId() {
		return internalCommandId;
	}

	public CommandId setInternalCommandId(String internalCommandId) {
		this.internalCommandId = internalCommandId;
		return this;
	}

	@Override
	public String toString() {
		return "CommandId [superapp=" + superapp + ", miniapp=" + miniapp + ", internalCommandId=" + internalCommandId
				+ "]";
	}
}
