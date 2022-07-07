package relevant_craft.vento.r_launcher.minecraft.auth.exceptions;

import relevant_craft.vento.r_launcher.minecraft.auth.responses.ErrorResponse;

/**
 * Thrown when the provided username/password pair is wrong or empty.
 */
public class InvalidCredentialsException extends RequestException {

    public InvalidCredentialsException(ErrorResponse error) {
        super(error);
    }

}
