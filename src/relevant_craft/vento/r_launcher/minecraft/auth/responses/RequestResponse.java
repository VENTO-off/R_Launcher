package relevant_craft.vento.r_launcher.minecraft.auth.responses;

import java.util.Map;

/**
 * Base class for API responses.
 */
public class RequestResponse {

    private int responseCode = -1;
    private Map<String, Object> data;

    public RequestResponse(int responseCode, Map<String, Object> data) {
        this.responseCode = responseCode;
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean isSuccessful() {
        return responseCode == 200 || responseCode == 204;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
