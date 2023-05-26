package superapp.logic.utils.validators;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import superapp.logic.boundaries.UserBoundary;
import superapp.logic.boundaries.UserId;
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
    public void validateUserId(UserId userId){

        Set<ConstraintViolation<UserId>> violations = validator.validate(userId);

        if (!violations.isEmpty()) {

            String errorMessage = buildErrorMessage(violations);
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * This method validate UserBoundary object
     * NOTE : not validate nested Objects Attributes
     *
     * @param userBoundary UserBoundary
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

        if (!filteredViolations.isEmpty()) {
            String errorMessage = buildErrorMessage(filteredViolations);
            throw new BadRequestException(errorMessage);
        }
    }


    /**
     * This method build a nice message from violations set
     *
     * @param violations Set<ConstraintViolation<T>>
     * @return String - error message
     * @param <T>
     */
    private static <T> String buildErrorMessage(Set<ConstraintViolation<T>> violations) {

        return violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("\n"));
    }

}
