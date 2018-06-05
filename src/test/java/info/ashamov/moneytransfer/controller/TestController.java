package info.ashamov.moneytransfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import info.ashamov.moneytransfer.util.DBUtil;
import info.ashamov.moneytransfer.util.PropertyUtil;
import info.ashamov.moneytransfer.util.ServerUtil;

public class TestController {
    protected static Server server;
    protected static CloseableHttpClient client;
    protected static URIBuilder uriBuilder;
    protected static ObjectMapper mapper;

    @BeforeClass
    public static void beforeClass() {
        PropertyUtil.initialize("application.properties");
        server = ServerUtil.configureServer();
        client = HttpClients.createDefault();
        uriBuilder = new URIBuilder()
                .setScheme("http")
                .setHost(PropertyUtil.getProperty("server.host"))
                .setPort(PropertyUtil.getPropertyAsInteger("server.port"));
        mapper = new ObjectMapper();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterClass() {
        HttpClientUtils.closeQuietly(client);
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        server.destroy();
    }

    @Before
    public void beforeTest() {
        DBUtil.initialize();
    }
}
