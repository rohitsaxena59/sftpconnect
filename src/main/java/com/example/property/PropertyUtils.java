package com.example.property;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

    public static final Properties properties;

    static {
        try {
            properties = getProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties getProperties() throws IOException {
        InputStream input = PropertyUtils.class.getClassLoader().getResourceAsStream("env.properties");
        Properties prop = new Properties();
        prop.load(input);
        return prop;
    }
}
