package superapp.logic.mongo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConflictRequestException extends RuntimeException{

    // user conflict
    public ConflictRequestException() {}

    public ConflictRequestException(String message) {
        super(message);
    }

    public ConflictRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictRequestException(Throwable cause) {
        super(cause);
    }

}
