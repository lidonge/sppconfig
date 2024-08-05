/**
 * @author lidong@date 2024-08-01@version 1.0
 */
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import free.servpp.config.IConfigurable;
import free.servpp.config.hocon.HoconConfigLoader;
import free.servpp.config.hocon.IConfigurableBuilder;

import java.io.IOException;
import java.util.List;

public class ConfigExample {
    public static void main(String[] args) throws IOException {
        // Load default configuration
        Config config = ConfigFactory.parseResources("config/application-dev.conf");

        // Load configuration from a resource file
        Config config1 = ConfigFactory.parseResources("config/application-prod.conf");

        // Merge configurations: config1 will override values in config
        Config mergedConfig = config1.withFallback(config);

        // Access values from the merged configuration
        String appName = mergedConfig.getString("app.name");
        String host = mergedConfig.getString("app.server.host");
        int serverPort = mergedConfig.getInt("app.server.port");

        System.out.println("Application Name: " + appName);
        System.out.println("Server Host: " + host);
        System.out.println("Server Port: " + serverPort);
    }
}