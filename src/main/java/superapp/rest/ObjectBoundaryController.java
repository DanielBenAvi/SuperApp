package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;
import superapp.logic.ObjectsServiceWithPaging;
import superapp.logic.boundaries.SuperAppObjectBoundary;


import java.util.List;

@RestController
public class ObjectBoundaryController {

	private ObjectsServiceWithPaging objectsService;



	@Autowired
	public void setObjectsService(ObjectsServiceWithPaging objectsService) {
		this.objectsService = objectsService;
	}

	/**
	 * Request specific Object Boundary
	 *
	 * @param superapp String
	 * @param internalObjectId String
	 * @return SuperAppObjectBoundary
	 */
	@GetMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary getObject(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId) {
		return objectsService
				.getSpecificObject(superapp, internalObjectId, userSuperapp, userEmail)
				.orElseThrow(() -> new RuntimeException("Could not find object by id: " + superapp + " " + internalObjectId));
	}

	/**
	 * Request all Object Boundary
	 *
	 * @return SuperAppObjectBoundary Array
	 */
	@GetMapping(path = {"/superapp/objects"},
				produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getAllObjects(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		SuperAppObjectBoundary[] ObjectBoundaryArray = new SuperAppObjectBoundary[] {};
		List<SuperAppObjectBoundary> ObjectBoundaryList
				= this.objectsService.getAllObjects(userSuperapp, userEmail, size, page);

		return ObjectBoundaryList.toArray(ObjectBoundaryArray);
	}

	/**
	 * Create new Object
	 *
	 * @param superAppObjectBoundary SuperAppObjectBoundary
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
	 * @param superapp String
	 * @param internalObjectId String
	 * @param updatedObject SuperAppObjectBoundary
	 */
	@PutMapping(path = {"/superapp/objects/{superapp}/{internalObjectId}"},
				consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(
			@RequestParam(name = "userSuperapp", required = true) String userSuperapp,
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@PathVariable("superapp") String superapp,
			@PathVariable("internalObjectId") String internalObjectId,
			@RequestBody SuperAppObjectBoundary updatedObject) {

		this.objectsService.updateObject(superapp, internalObjectId, updatedObject, userSuperapp, userEmail);
	}

}
