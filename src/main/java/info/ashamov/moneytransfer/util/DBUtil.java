package info.ashamov.moneytransfer.util;

import info.ashamov.moneytransfer.exception.InternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static Logger log = LogManager.getLogger(DBUtil.class);

    private static final String DB_URL = PropertyUtil.getProperty("db.url");
    private static final String DB_SCRIPT = PropertyUtil.getProperty("db.script");

    public static void initialize() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            RunScript.execute(connection,
                              new InputStreamReader(DBUtil.class.getClassLoader().getResourceAsStream(DB_SCRIPT)));
        } catch (Exception e) {
            String errorMsg = "DB creation failed, source: " + DB_SCRIPT;
            log.fatal(errorMsg, e);
            throw new InternalException(errorMsg, e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new InternalException("Unable to obtain DB connection", e);
        }
    }
}
