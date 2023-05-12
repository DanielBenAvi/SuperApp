package superapp.logic.mongo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DeprecatedRequestException extends RuntimeException {


    // user error
    public DeprecatedRequestException() {}

    public DeprecatedRequestException(String message) {
        super(message);
    }

    public DeprecatedRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeprecatedRequestException(Throwable cause) {
        super(cause);
    }

}
