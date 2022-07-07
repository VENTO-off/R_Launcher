package relevant_craft.vento.r_launcher.manager.cheat;

import java.util.Collections;
import java.util.List;

public class CheatSorter {

    protected static List<CheatVersion> ShellSort(List<CheatVersion> list, int index) {
        int i, j, k;
        for (i = list.size() / 2; i > 0; i = i / 2) {
            for (j = i; j < list.size(); j++) {
                for (k = j - i; k >= 0; k = k - i) {
                    if (numberFromVersion(list.get(k + 1).getVersion(), index) <= numberFromVersion(list.get(k).getVersion(), index)) {
                        break;
                    } else {
                        Collections.swap(list, k, k + 1);
                    }
                }
            }
        }

        return list;
    }

    private static int numberFromVersion(String version, int index) {
        String[] numbers = version.split("\\.");
        try {
            return Integer.valueOf(numbers[index]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

}
