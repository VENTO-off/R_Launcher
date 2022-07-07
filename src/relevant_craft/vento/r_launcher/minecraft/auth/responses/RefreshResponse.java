package relevant_craft.vento.r_launcher.minecraft.auth.responses;

import relevant_craft.vento.r_launcher.minecraft.auth.Profile;

/**
 * Response generated when refresh is successful.
 *
 * @see LoginResponse
 */
public class RefreshResponse extends LoginResponse {

    public RefreshResponse(String accessToken, String clientToken, Profile selectedProfile) {
        super(accessToken, clientToken, selectedProfile);
    }

}
