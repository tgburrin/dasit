package net.tgburrin.dasit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class TestSetup implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private static boolean started = false;

    private DataSource dataSource;
    //https://stackoverflow.com/questions/56904620/junit-5-inject-spring-components-to-extension-beforeallcallback-afterallcall/62504238#62504238

    @SuppressWarnings("resource")
	protected static String getDatabaseSetup() throws SQLException, IOException, URISyntaxException {
		return new String(Files.readAllBytes(Paths.get(App.class.getResource("/schema.sql").toURI())));
	}

    public void buildDatabase() throws SQLException, IOException, URISyntaxException {
    	try (Connection conn = dataSource.getConnection()) {
    		Statement stmt = conn.createStatement();
    		stmt.executeUpdate(getDatabaseSetup());
    		stmt.close();
    		conn.close();
    	}
    }

    @Override
    public void beforeAll(ExtensionContext context) throws SQLException, IOException, URISyntaxException {
        synchronized (TestSetup.class) {
            if (!started) {
            	started = true;
            	// Your "before all tests" startup logic goes here

            	ApplicationContext springContext = SpringExtension.getApplicationContext(context);
            	dataSource = springContext.getBean(DataSource.class);

            	buildDatabase();

            	// The following line registers a callback hook when the root test context is shut down
            	context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("TestDataSetup-started", this);
            }
        }
    }

    @Override
    public void close() throws SQLException {
    	synchronized (TestSetup.class) {
        	try (Connection conn = dataSource.getConnection()) {
        		Statement stmt = conn.createStatement();
        		stmt.executeUpdate("drop schema dasit cascade");
        		stmt.close();
        		conn.close();
        	}
    	}
    }
}