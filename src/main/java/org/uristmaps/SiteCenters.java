package org.uristmaps;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SiteCenters {

    /**
     * Maps site types to structure objects that are associated with them.
     */
    private static Map<String, String> typeToStruct;

    static {
        typeToStruct = new HashMap<>();
        typeToStruct.put("hamlet", "village");
        typeToStruct.put("dark fortress", "castle");
        typeToStruct.put("dark pits", "castle");
        typeToStruct.put("tomb", "castle");
        typeToStruct.put("hillocks", "village");
        typeToStruct.put("town", "village");
        typeToStruct.put("forest retreat", "village");
    }
    public static void load() {


    }
}
