package superapp.logic.utils.validators;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import superapp.logic.boundaries.*;
import superapp.logic.mongo.BadRequestException;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BoundaryValidator {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @Autowired
    public BoundaryValidator() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    /**
     * This method validate UserId object
     *
     * @param userId UserId
     */
    public void validateUserId(UserId userId) {

        Set<ConstraintViolation<UserId>> violations = validator.validate(userId);

        if (!violations.isEmpty()) {

            String errorMessage = buildErrorMessage(violations);
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * This method validate ObjectId object
     *
     * @param objectId ObjectId
     */
    public void validateObjectId(ObjectId objectId) {

        Set<ConstraintViolation<ObjectId>> violations = validator.validate(objectId);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));

    }

    /**
     * This method validate CommandId object
     *
     * @param commandId CommandId
     */
    public void validateCommandId(CommandId commandId) {

        Set<ConstraintViolation<CommandId>> violations = validator.validate(commandId);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));

    }

    /**
     * This method validate UserBoundary object
     * NOTE : not validate nested Objects Attributes
     *
     * @param userBoundary      UserBoundary
     * @param ignoredProperties Set<String>
     */
    public void validateUserBoundary(UserBoundary userBoundary, Set<String> ignoredProperties) {

        if (userBoundary == null || ignoredProperties == null)
            throw new RuntimeException();

        Set<ConstraintViolation<UserBoundary>> filteredViolations;

        filteredViolations = validator
                .validate(userBoundary)
                .stream()
                .filter(violation -> !ignoredProperties.contains(violation.getPropertyPath().toString()))
                .collect(Collectors.toSet());

        if (!filteredViolations.isEmpty())
            throw new BadRequestException(buildErrorMessage(filteredViolations));

    }


    /**
     * This method validate MiniAppCommandBoundary object
     *
     * @param commandBoundary MiniAppCommandBoundary
     */
    public void validateCommandBoundary(MiniAppCommandBoundary commandBoundary) {
        this.validateCommandId(commandBoundary.getCommandId());
        Set<ConstraintViolation<MiniAppCommandBoundary>> violations = validator.validate(commandBoundary);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));

    }

    /**
     * This method validate InvokedBy object
     *
     * @param invokedBy InvokedBy
     */
    public void validateInvokedBy(InvokedBy invokedBy) {

        Set<ConstraintViolation<InvokedBy>> violations = validator.validate(invokedBy);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));

        this.validateUserId(invokedBy.getUserId());
    }

    /**
     * This method validate TargetObject object
     *
     * @param targetObject TargetObject
     */
    public void validateTargetObject(TargetObject targetObject) {

        Set<ConstraintViolation<TargetObject>> violations = validator.validate(targetObject);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));

        this.validateObjectId(targetObject.getObjectId());
    }

    /**
     * This method validate SuperAppObjectBoundary object
     *
     * @param objectBoundary SuperAppObjectBoundary
     */
    public void validateObjectBoundary(SuperAppObjectBoundary objectBoundary) {

        Set<ConstraintViolation<SuperAppObjectBoundary>> violations = validator.validate(objectBoundary);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));
    }

    /**
     * This method validate CreatedBy object
     *
     * @param createdBy CreatedBy
     */
    public void validateCreatedBy(CreatedBy createdBy) {
        Set<ConstraintViolation<CreatedBy>> violations = validator.validate(createdBy);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));
        this.validateUserId(createdBy.getUserId());
    }

    /**
     * This method validate Location object
     * and init values of lat lng to (0.0, 0.0) if it is null
     *
     * @param location Location
     * @return Location
     */
    public Location validateLocation(Location location) {

        if (location == null)
            return new Location().setLat(0).setLng(0);

        if (location.getLng() == null)
            location.setLng(0);

        if (location.getLat() == null)
            location.setLat(0);

        Set<ConstraintViolation<Location>> violations = validator.validate(location);

        if (!violations.isEmpty())
            throw new BadRequestException(buildErrorMessage(violations));

        return location;
    }


    /**
     * This method build a nice message from violations set
     *
     * @param violations Set<ConstraintViolation<T>>
     * @return String error message
     */
    private <T> String buildErrorMessage(Set<ConstraintViolation<T>> violations) {

        return violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("\n"));
    }

}
