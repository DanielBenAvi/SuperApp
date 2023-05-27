package superapp.logic.utils.validators;

import superapp.logic.mongo.BadRequestException;

public class ValidatorHelper {

    public static void validatePage(int pageNumber) {
        if (pageNumber < 0)
            throw new BadRequestException("Page number must be positive");
    }

    public static void validateSize(int size) {
        if (size <= 0)
            throw new BadRequestException("Size must be positive");
    }
}
