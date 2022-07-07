package relevant_craft.vento.r_launcher.minecraft.auth.exceptions;

import relevant_craft.vento.r_launcher.minecraft.auth.responses.ErrorResponse;

/**
 * Thrown when the provided token is invalid or expired.
 */
public class InvalidTokenException extends RequestException {

    public InvalidTokenException(ErrorResponse error) {
        super(error);
    }

}
