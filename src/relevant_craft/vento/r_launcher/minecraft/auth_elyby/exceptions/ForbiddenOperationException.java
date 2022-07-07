package relevant_craft.vento.r_launcher.minecraft.auth_elyby.exceptions;

import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.RequestException;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.ErrorResponse;

public class ForbiddenOperationException extends RequestException {

    public ForbiddenOperationException(ErrorResponse error) {
        super(error);
    }
}
