package relevant_craft.vento.r_launcher.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AntivirusUtils {

    public static String getAntivirus() {
        String antivirus = null;

        if (OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS) {
            List<String> antivirusList = AntivirusUtils.getAntivirusList();
            if (antivirusList.size() == 1) {
                antivirus = antivirusList.get(0);
            } else if (antivirusList.size() > 1) {
                if (antivirusList.get(0).equals("Windows Defender")) {
                    antivirus = antivirusList.get(antivirusList.size() - 1);
                } else {
                    antivirus = antivirusList.get(0);
                }
            }
        }

        return antivirus;
    }

    private static List<String> getAntivirusList() {
        try {
            Process p = Runtime.getRuntime().exec("cmd /c " + "WMIC /Node:localhost /Namespace:\\\\root\\SecurityCenter2 Path AntiVirusProduct Get displayName /Format:List");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));

            List<String> result = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    result.add(line.replace("displayName=", "").trim());
                }
            }

            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
