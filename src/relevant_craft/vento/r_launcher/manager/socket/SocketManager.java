package relevant_craft.vento.r_launcher.manager.socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import relevant_craft.vento.r_launcher.VENTO;
import relevant_craft.vento.r_launcher.utils.DnsUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SocketManager {

    private static String socketIp = null;

    private static void getSocketIp() {
        String ipViaDns = getIpViaDns();
        if (ipViaDns != null) {
            socketIp = ipViaDns;
            return;
        }

        socketIp = getIpViaWeb();
    }

    private static String getIpViaDns() {
        String domain = VENTO.SITE.replace("http://", "").replace("https://", "").replace("www.", "").replace("/", "");
        List<String> txtRecords = DnsUtils.getDnsRecords(domain, "TXT");
        if (txtRecords.isEmpty()) {
            return null;
        }

        for (String record : txtRecords) {
            if (record.startsWith("r_socket")) {
                List<String> ip = Arrays.asList(record.split(" ")[1].split(";"));
                return ip.get(new Random().nextInt(ip.size()));
            }
        }

        return null;
    }

    private static String getIpViaWeb() {
        String response = "";
        try {
            URL url = new URL(VENTO.WEB + "socket.php");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            response = bufferedreader.readLine();
            bufferedreader.close();
        } catch (IOException e) {
            return null;
        }

        List<String> ip = Arrays.asList(response.split(";"));
        return ip.get(new Random().nextInt(ip.size()));
    }

    public static SocketAnswer connectSocket(JsonObject request) {
        if (socketIp == null) {
            getSocketIp();
        }

        Socket socket = null;
        Scanner in = null;
        PrintWriter out = null;
        try {
            String[] ip = socketIp.split(":");
            socket = new Socket(ip[0], Integer.parseInt(ip[1]));
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            Gson gson = new Gson();
            out.println(gson.toJson(request));

            String response = in.nextLine();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response);
            JsonObject json = element.getAsJsonObject();

            SocketAnswer answer = new SocketAnswer();
            if (json.has("error")) {
                boolean error = json.get("error").getAsBoolean();

                if (error) {
                    answer.setError(true);
                    answer.setResponse(json.get("errorMessage").getAsString());
                } else {
                    answer.setError(false);
                    answer.setResponse(json.get("response").getAsString());
                }

                return answer;
            }
        } catch (ConnectException e) {
            SocketAnswer answer = new SocketAnswer();
            answer.setError(true);
            answer.setResponse("Скачивание модов/текстур/модпаков временно не доступно.\nПопробуйте, пожалуйста, позже.");
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

}
