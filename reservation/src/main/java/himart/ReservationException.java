package himart;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "No Available stock!")
public class ReservationException extends RuntimeException {
    public ReservationException(String message) {
        super(message);
    }
}