package relevant_craft.vento.r_launcher.minecraft.auth.responses;

import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.RequestException;

/**
 * Thrown when a licence hasn't bought on Mojang account.
 */
public class LicenceNotBoughtException extends RequestException {

    public LicenceNotBoughtException() {
        super(null);
    }

    public LicenceNotBoughtException(ErrorResponse error) {
        super(error);
    }
}
