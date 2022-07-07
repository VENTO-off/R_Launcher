package relevant_craft.vento.r_launcher.manager.socket;

public class SocketAnswer {

    private String response;
    private boolean error;

    public SocketAnswer() {}

    public String getResponse() {
        return response;
    }

    public boolean isError() {
        return error;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
