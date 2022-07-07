package relevant_craft.vento.r_launcher.minecraft.auth.exceptions;

import relevant_craft.vento.r_launcher.minecraft.auth.responses.ErrorResponse;

/**
 * Thrown when a nickname is used as username instead of an email address and the user has a Mojang account.
 */
public class UserMigratedException extends RequestException {

    public UserMigratedException(ErrorResponse error) {
        super(error);
    }

}
