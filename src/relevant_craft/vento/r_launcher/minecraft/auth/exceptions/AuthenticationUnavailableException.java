package relevant_craft.vento.r_launcher.minecraft.auth.exceptions;

import relevant_craft.vento.r_launcher.minecraft.auth.responses.ErrorResponse;

/**
 * Thrown when the authentication servers are unreachable.
 */
public class AuthenticationUnavailableException extends Exception {

    public AuthenticationUnavailableException(ErrorResponse error) {

    }

}
