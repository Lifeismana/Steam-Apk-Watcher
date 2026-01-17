package it.innove;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes3.dex */
public class UUIDHelper {
    public static final String UUID_BASE = "0000XXXX-0000-1000-8000-00805f9b34fb";

    public static UUID uuidFromString(String str) {
        if (str.length() == 4) {
            str = UUID_BASE.replace("XXXX", str);
        }
        return UUID.fromString(str);
    }

    public static String uuidToString(UUID uuid) {
        String string = uuid.toString();
        Matcher matcher = Pattern.compile("0000(.{4})-0000-1000-8000-00805f9b34fb", 2).matcher(string);
        return matcher.matches() ? matcher.group(1) : string;
    }
}
