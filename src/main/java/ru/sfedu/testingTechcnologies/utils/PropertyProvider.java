package ru.sfedu.testingTechcnologies.utils;

import ru.sfedu.testingTechcnologies.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration utility. Allows to get configuration properties from the
 * default configuration file
 *
 * @author Boris Jmailov
 */
public class PropertyProvider {

    private static final String DEFAULT_CONFIG_PATH = "./src/main/resources/environment.properties";
    private static final Properties PROPERTIES = new Properties();
    /**
     * Hides default constructor
     */
    private PropertyProvider() {
    }

    private static Properties getConfiguration() throws IOException {
        if (PROPERTIES.isEmpty()) {
            loadProperty();
        }
        return PROPERTIES;
    }

    /**
     * Loads configuration from <code>DEFAULT_CONFIG_PATH</code>
     *
     * @throws IOException In case of the configuration file read failure
     */
    private static void loadProperty() throws IOException {
        File propertyFile = new File(System.getProperty(Constants.CONFIG_PATH) != null
                ? System.getProperty(Constants.CONFIG_PATH)
                : DEFAULT_CONFIG_PATH);
        try (InputStream in = new FileInputStream(propertyFile)) {
            PROPERTIES.load(in);
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Gets configuration entry value
     *
     * @param key Entry key
     * @return Entry value by key
     * @throws IOException In case of the configuration file read failure
     */
    public static String getProperty(String key) throws IOException {
        return getConfiguration().getProperty(key);
    }

}

