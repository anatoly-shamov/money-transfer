package info.ashamov.moneytransfer.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import info.ashamov.moneytransfer.exception.InternalException;

import java.io.IOException;
import java.util.Properties;

import static java.util.Optional.ofNullable;

public class PropertyUtil {
    private static Logger log = LogManager.getLogger(PropertyUtil.class);

    private static Properties properties = new Properties();

    public static void initialize(String fileName) {
        try {
            properties.load(PropertyUtil.class.getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            String errorMsg = "Can't load properties from file: " + fileName;
            log.fatal(errorMsg, e);
            throw new InternalException(errorMsg, e);
        }
    }

    public static String getProperty(String name) {
        return ofNullable(properties.getProperty(name))
                .orElse(System.getProperty(name));
    }

    public static Integer getPropertyAsInteger(String name) {
        String value = ofNullable(properties.getProperty(name))
                .orElse(System.getProperty(name));
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            String errorMsg = "Can't convert property to Integer: " + value;
            log.fatal(errorMsg, e);
            throw new InternalException(errorMsg, e);
        }
    }
}
