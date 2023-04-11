package superapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;
import superapp.logic.ObjectsService;
import superapp.logic.boundaries.SuperAppObjectBoundary;

import java.util.List;

/**
 * @author Daniel & Yaniv create this class
 */

@RestController
public class ObjectBoundaryController {

	private ObjectsService objectsService; // objectsService interface


	@Autowired
	public void setObjectsService(ObjectsService objectsService) {
		this.objectsService = objectsService;
	}

	/**
	 * Request specific Object Boundary
	 *
	 * @param superapp
	 * @param internalObjectId
	 * @return SuperAppObjectBoundary
	 */
	@GetMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary getObject(@PathVariable("superapp") String superapp,
											@PathVariable("internalObjectId") String internalObjectId) {

		return objectsService.getSpecificObject(superapp, internalObjectId);
	}

	/**
	 * Request all Object Boundary
	 *
	 * @return SuperAppObjectBoundary Array
	 */
	@GetMapping(path = {"/superapp/objects"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getAllObjects() {

		SuperAppObjectBoundary[] ObjectBoundaryArray = new SuperAppObjectBoundary[] {};
		List<SuperAppObjectBoundary> ObjectBoundaryList = this.objectsService.getAllObjects();

		return ObjectBoundaryList.toArray(ObjectBoundaryArray);
	}

	/**
	 * Create new Object
	 *
	 * @param superAppObjectBoundary
	 * @return SuperAppObjectBoundary
	 */
	@PostMapping(path = {"/superapp/objects"},
				 produces = {MediaType.APPLICATION_JSON_VALUE},
				 consumes = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary createObject(@RequestBody SuperAppObjectBoundary superAppObjectBoundary) {
		return this.objectsService.createObject(superAppObjectBoundary);
	}
	
	

	/**
	 * Update exist object, updated object included only updated attr
	 *
	 * @param superapp
	 * @param internalObjectId
	 * @param updatedObject
	 */
	@PutMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}"},
				consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(@PathVariable("superapp") String superapp,
							 @PathVariable("internalObjectId") String internalObjectId,
							 @RequestBody SuperAppObjectBoundary updatedObject) {

		this.objectsService.updateObject(superapp, internalObjectId, updatedObject);
	}

}
