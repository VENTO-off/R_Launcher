package relevant_craft.vento.r_launcher.minecraft.auth.responses;

import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.RequestException;

import java.io.Serializable;

/**
 * Response generated when an exception is thrown.
 * Intended for internal use only.
 * Call getError() and getErrorMessage() on a RequestException object to get more info about the error.
 *
 * @see RequestException
 */
public class ErrorResponse implements Serializable {

    private String error;
    private String errorMessage;
    private String cause;

    public String getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCause() {
        return cause;
    }

}
