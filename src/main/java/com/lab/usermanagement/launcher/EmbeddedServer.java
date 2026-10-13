package com.lab.usermanagement.launcher;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * Embedded Tomcat 10.1 Server Launcher.
 * Enables running the application instantly with:
 *     mvn exec:exec
 * without requiring manual installation or configuration of an external Tomcat server.
 */
public class EmbeddedServer {

    private static final Logger LOGGER = Logger.getLogger(EmbeddedServer.class.getName());
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = getAvailablePort(DEFAULT_PORT);

        File baseDir = Files.createTempDirectory("tomcat-temp-").toFile();
        baseDir.deleteOnExit();

        File webappDir = new File("target/UserManagementSystem");
        if (!webappDir.exists() || !new File(webappDir, "WEB-INF/web.xml").exists()) {
            webappDir = new File("src/main/webapp");
        }

        LOGGER.info("Starting Apache Tomcat 10.1 Embedded Server...");
        LOGGER.info("Webapp Directory: " + webappDir.getAbsolutePath());
        LOGGER.info("Server Port: " + port);

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default HTTP connector

        Context ctx = tomcat.addWebapp("/UserManagementSystem", webappDir.getAbsolutePath());

        // Ensure compiled classes and resources in target/classes are loaded
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        // Add root context "/" redirecting directly to /UserManagementSystem/users
        Context rootCtx = tomcat.addContext("", baseDir.getAbsolutePath());
        Tomcat.addServlet(rootCtx, "RootRedirectServlet", new jakarta.servlet.http.HttpServlet() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
                    throws IOException {
                resp.sendRedirect("/UserManagementSystem/users");
            }
        });
        rootCtx.addServletMappingDecoded("/*", "RootRedirectServlet");

        tomcat.start();

        System.out.println("\n==================================================================");
        System.out.println("  User Management System - Tomcat 10.1 Server Running Successfully");
        System.out.println("==================================================================");
        System.out.println("  Access the application at:");
        System.out.println("  -> http://localhost:" + port + "/");
        System.out.println("  -> http://localhost:" + port + "/UserManagementSystem/");
        System.out.println("  -> http://localhost:" + port + "/UserManagementSystem/users");
        System.out.println("==================================================================\n");

        tomcat.getServer().await();
    }

    private static int getAvailablePort(int preferredPort) {
        try (ServerSocket socket = new ServerSocket(preferredPort)) {
            return preferredPort;
        } catch (IOException e) {
            LOGGER.warning("Port " + preferredPort + " is already in use. Falling back to port 8081.");
            return 8081;
        }
    }
}
