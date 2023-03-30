package superapp;

import java.util.Date;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import groupMiniApp.Group;
import superAppsObjects.UserDetails;
import superapp.commandBoundary.CommandBoundary;

/**

 * @author Ido & Yosef

 */
@RestController
public class MiniAppCommandController {
	@PostMapping(path = {"/superapp/miniapp/{miniAppName}"},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Object command(@PathVariable("miniAppName") String miniAppName, @RequestBody CommandBoundary commandBoundary) {
		UUID uuid = UUID.randomUUID();
		commandBoundary.getCommandId().setInternalCommandId(uuid.toString());
		commandBoundary.getCommandId().setMiniapp(miniAppName);
		commandBoundary.setInvocationTimestamp(new Date());
		System.err.println(commandBoundary.toString());
		
		switch (commandBoundary.getCommand()) {
		case "return UserDetails": {
			return new UserDetails("Guy","050-0000000",null,"male",null);
		}
		case "CreateGroup": {
			return new Group();
		}
		default:
			System.err.println("Does not recognise this command");
		}
		return null;
	}

}
