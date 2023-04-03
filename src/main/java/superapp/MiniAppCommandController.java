package superapp;

import java.util.Date;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import superapp.data.groupMiniApp.GroupEntity;
import superapp.data.UserDetails;
import superapp.logic.boundaries.MiniAppCommandBoundary;

/**

 * @author Ido & Yosef

 */
@RestController
public class MiniAppCommandController {
	@PostMapping(path = {"/superapp/miniapp/{miniAppName}"},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Object command(@PathVariable("miniAppName") String miniAppName, @RequestBody MiniAppCommandBoundary miniAppCommandBoundary) {
		UUID uuid = UUID.randomUUID();
		miniAppCommandBoundary.getCommandId().setInternalCommandId(uuid.toString());
		miniAppCommandBoundary.getCommandId().setMiniapp(miniAppName);
		miniAppCommandBoundary.setInvocationTimestamp(new Date());
		System.err.println(miniAppCommandBoundary.toString());
		
		switch (miniAppCommandBoundary.getCommand()) {
		case "return UserDetails": {
			return new UserDetails("Guy","050-0000000",null,"male",null);
		}
		case "CreateGroup": {
			return new GroupEntity();
		}
		default:
			System.err.println("Does not recognise this command");
		}
		return null;
	}

}
