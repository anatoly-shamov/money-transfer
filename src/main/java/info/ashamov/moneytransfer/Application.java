package info.ashamov.moneytransfer;

import info.ashamov.moneytransfer.util.ServerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import info.ashamov.moneytransfer.util.DBUtil;
import info.ashamov.moneytransfer.util.PropertyUtil;

public class Application {
    private static Logger log = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        PropertyUtil.initialize("application.properties");
        DBUtil.initialize();
        Server server = ServerUtil.configureServer();
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            log.fatal(e);
        } finally {
            server.destroy();
        }
    }
}
