package relevant_craft.vento.r_launcher.utils;

import javax.naming.Context;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DnsUtils {

    public static List<String> getDnsRecords(String domain, String attrType) {
        List<String> records = new ArrayList<>();

        try {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
            InitialDirContext idc = new InitialDirContext(env);

            Attributes attrs = idc.getAttributes(domain, new String[]{attrType});
            Attribute attr = attrs.get(attrType);

            if (attr != null) {
                for (int i = 0; i < attr.size(); i++) {
                    String currentAttr = (String) attr.get(i);
                    records.add(currentAttr.replace("\"", ""));
                }
            }
        } catch (Exception ignored) {}

        return records;
    }
}
