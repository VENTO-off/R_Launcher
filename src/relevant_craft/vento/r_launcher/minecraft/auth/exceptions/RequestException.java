package relevant_craft.vento.r_launcher.minecraft.auth.exceptions;

import relevant_craft.vento.r_launcher.minecraft.auth.responses.ErrorResponse;

/**
 * Main class, extended by every library's exception.
 *
 * @see AuthenticationUnavailableException
 * @see InvalidCredentialsException
 * @see InvalidTokenException
 * @see UserMigratedException
 */
public class RequestException extends Exception {

    private ErrorResponse error;

    public RequestException(ErrorResponse error) {
        this.error = error;
    }

    public ErrorResponse getResponse() {
        return this.error;
    }

    public String getError() {
        return this.error.getError();
    }

    public String getErrorMessage() {
        return this.error.getErrorMessage();
    }

    public String getErrorCause() {
        return this.error.getCause();
    }

}
