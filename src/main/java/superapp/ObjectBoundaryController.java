package superapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import superapp.logic.boundaries.CreatedBy;
import superapp.logic.boundaries.SuperAppObjectBoundary;
import superapp.logic.boundaries.ObjectId;
import superapp.logic.boundaries.UserID;

@RestController
public class ObjectBoundaryController {
	
	/**
	 * Request specific Object Boundary
	 * @return Object Boundary
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary retriveObject(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId){
		SuperAppObjectBoundary ob = new SuperAppObjectBoundary();
		// hardCode
		ob.setObjectId(new ObjectId());
		ob.getObjectId().setInternalObjectId(internalObjectId);
		ob.getObjectId().setSuperApp(superapp);
		ob.setType("dummyType");
		ob.setAlias("demo instance");
		ob.setActive(true);
		ob.setCreateTimeStamp(new Date());
		UserID ud = new UserID("jill@demo.org",superapp);
		CreatedBy cb = new CreatedBy();
		cb.setUserId(ud);
		ob.setCreatedBy(cb);
		ob.setObjectDetails(new Date());
		// hardCode
		return ob;
	}
	
	/**
	 * Request all Object Boundary
	 * @return List of Object Boundary
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Map<String, SuperAppObjectBoundary> getAllObjects(){
		Map<String, SuperAppObjectBoundary> objectBoundaryHash = new HashMap<>();
//		hard code
		objectBoundaryHash.put("1", new SuperAppObjectBoundary());
		objectBoundaryHash.put("2", new SuperAppObjectBoundary());
		
		return objectBoundaryHash;
	}
	
	/**
	 * Create new Object
	 * @return Object Boundary
	 * @param superAppObjectBoundary as ObjectBoundary
	 * 
	 * @author Daniel & Yaniv
	 */
	@RequestMapping(
			path = {"/superapp/objects"},
			method = {RequestMethod.POST},
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary createObject(@RequestBody SuperAppObjectBoundary superAppObjectBoundary) {
//		hard code
		superAppObjectBoundary.setObjectId(new ObjectId());
		superAppObjectBoundary.getObjectId().setSuperApp("2023b.liorAriely");
		System.err.println(superAppObjectBoundary.getObjectId().getInternalObjectId());
		return superAppObjectBoundary;
	}
	
	
	/**
	 * updateObject 
	 * @param superapp name of the superapp
	 * @param internalObjectId the object id
	 * @param updatedObject
	 */
	@RequestMapping(
			path = {"/superapp/objects/{superapp}/{internalObjectId}"},
			method = {RequestMethod.PUT},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId,
			@RequestBody SuperAppObjectBoundary updatedObject) {
		
		if (updatedObject.getObjectId() == null || 
				updatedObject.getObjectId().getInternalObjectId() == null) {
			System.err.println("ObjectId or internalObjectId is null");
		}
		else {
		System.err.println("Updating object #" + internalObjectId + " using: " + updatedObject);
		}
		
	}
	
}
