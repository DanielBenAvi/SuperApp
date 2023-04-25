package superapp.logic.excptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserBadRequestException extends RuntimeException {

    public UserBadRequestException() {}

    public UserBadRequestException(String message) {
        super(message);
    }

    public UserBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBadRequestException(Throwable cause) {
        super(cause);
    }

}
