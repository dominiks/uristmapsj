package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import spark.Spark;

/**
 * Created by dominik on 29.05.2015.
 */
public class WebServer {

    public static void start() {
        Log.info("WebServer", "Starting");

        Spark.port(Uristmaps.conf.get("Web", "port", Integer.class));
        Spark.staticFileLocation(Uristmaps.conf.fetch("Paths", "output"));
        Spark.get("/stop", (request, response) -> {
                Spark.stop();
                return null;
                });
    }
}
