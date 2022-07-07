package relevant_craft.vento.r_launcher.minecraft.auth_elyby;

import relevant_craft.vento.r_launcher.minecraft.auth.JsonUtils;
import relevant_craft.vento.r_launcher.minecraft.auth.Profile;
import relevant_craft.vento.r_launcher.minecraft.auth.exceptions.*;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.AuthenticationResponse;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.ErrorResponse;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.RefreshResponse;
import relevant_craft.vento.r_launcher.minecraft.auth.responses.RequestResponse;
import relevant_craft.vento.r_launcher.minecraft.auth_elyby.exceptions.ForbiddenOperationException;
import relevant_craft.vento.r_launcher.minecraft.auth_elyby.exceptions.IllegalArgumentException;

import java.net.Proxy;
import java.net.URL;

import static relevant_craft.vento.r_launcher.minecraft.auth.Authenticator.sendJsonPostRequest;

public class AuthenticatorElyby {

    /**
     * Authenticates an user with an username and a password.
     */
    public static AuthenticationResponse authenticate(String username, String password, String clientToken, Proxy proxy) throws AuthenticationUnavailableException, IllegalArgumentException, ForbiddenOperationException {
        RequestResponse result = sendJsonPostRequest(getRequestUrl("/auth/authenticate"), JsonUtils.credentialsToJson(username, password, clientToken), proxy);
        if (result.isSuccessful()) {
            String accessToken = (String) result.getData().get("accessToken");
            String rClientToken = (String) result.getData().get("clientToken");
            Profile selectedProfile = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData().get("selectedProfile")), Profile.class);
            Profile[] availableProfiles = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData().get("availableProfiles")), Profile[].class);
            return new AuthenticationResponse(accessToken, rClientToken, selectedProfile, availableProfiles);
        } else {
            ErrorResponse errorResponse = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData()), ErrorResponse.class);
            if (result.getData().get("error") != null && ((String) (result.getData().get("error"))).equalsIgnoreCase("IllegalArgumentException")) {
                throw new IllegalArgumentException(errorResponse);
            } else {
                throw new ForbiddenOperationException(errorResponse);
            }
        }
    }

    /**
     * Authenticates an user with an username and a password.
     */
    public static AuthenticationResponse authenticate(String username, String password, String clientToken) throws RequestException, AuthenticationUnavailableException {
        return authenticate(username, password, clientToken, null);
    }

    /**
     * Refreshes the given access token.
     */
    public static RefreshResponse refresh(String accessToken, String clientToken, Proxy proxy) throws RequestException, AuthenticationUnavailableException {
        RequestResponse result = sendJsonPostRequest(getRequestUrl("/auth/refresh"), JsonUtils.tokenToJson(accessToken, clientToken), proxy);
        if (result.isSuccessful()) {
            String newAccessToken = (String) result.getData().get("accessToken");
            String rClientToken = (String) result.getData().get("clientToken");
            Profile selectedProfile = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData().get("selectedProfile")), Profile.class);
            return new RefreshResponse(newAccessToken, rClientToken, selectedProfile);
        } else {
            ErrorResponse errorResponse = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData()), ErrorResponse.class);
            throw new InvalidTokenException(errorResponse);
        }
    }

    /**
     * Refreshes the given access token.
     */
    public static RefreshResponse refresh(String accessToken, String clientToken) throws RequestException, AuthenticationUnavailableException {
        return refresh(accessToken, clientToken, null);
    }

    /**
     * Validates the given access token.
     */
    public static boolean validate(String accessToken, Proxy proxy) throws RequestException, AuthenticationUnavailableException {
        RequestResponse result = sendJsonPostRequest(getRequestUrl("/auth/validate"), JsonUtils.tokenToJson(accessToken, null), proxy);
        if (result.isSuccessful()) {
            return true;
        } else {
            ErrorResponse errorResponse = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData()), ErrorResponse.class);
            throw new InvalidTokenException(errorResponse);
        }
    }

    /**
     * Validates the given access token.
     */
    public static boolean validate(String accessToken) throws RequestException, AuthenticationUnavailableException {
        return validate(accessToken, null);
    }

    /**
     * Invalidates the given access token.
     */
    public static boolean invalidate(String accessToken, String clientToken, Proxy proxy) throws RequestException, AuthenticationUnavailableException {
        RequestResponse result = sendJsonPostRequest(getRequestUrl("/auth/invalidate"), JsonUtils.tokenToJson(accessToken, clientToken), proxy);
        if (result.isSuccessful()) {
            return true;
        } else {
            ErrorResponse errorResponse = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData()), ErrorResponse.class);
            throw new InvalidTokenException(errorResponse);
        }
    }

    /**
     * Invalidates the given access token.
     */
    public static boolean invalidate(String accessToken, String clientToken) throws RequestException, AuthenticationUnavailableException {
        return invalidate(accessToken, clientToken, null);
    }

    /**
     * Invalidates every access token for an user.
     */
    public static boolean signout(String username, String password, Proxy proxy) throws RequestException, AuthenticationUnavailableException {
        RequestResponse result = sendJsonPostRequest(getRequestUrl("/auth/signout"), JsonUtils.credentialsToJson(username, password, null), proxy);
        if (result.isSuccessful()) {
            return true;
        } else {
            ErrorResponse errorResponse = JsonUtils.gson.fromJson(JsonUtils.gson.toJson(result.getData()), ErrorResponse.class);
            if (result.getData().get("error") != null && ((String) (result.getData().get("error"))).equalsIgnoreCase("IllegalArgumentException")) {
                throw new IllegalArgumentException(errorResponse);
            } else {
                throw new ForbiddenOperationException(errorResponse);
            }
        }
    }

    /**
     * Invalidates every access token for an user.
     */
    public static boolean signout(String username, String password) throws RequestException, AuthenticationUnavailableException {
        return signout(username, password, null);
    }

    private static URL getRequestUrl(String request) {
        try {
            return new URL("https://authserver.ely.by" + request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
