package superapp.logic.mongo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedRequestException extends RuntimeException {

    // user error
    public UnauthorizedRequestException() {}

    public UnauthorizedRequestException(String message) {
        super(message);
    }

    public UnauthorizedRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedRequestException(Throwable cause) {
        super(cause);
    }

}
