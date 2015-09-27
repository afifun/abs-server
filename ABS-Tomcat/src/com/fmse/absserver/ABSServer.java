/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmse.absserver;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 *
 * @author Kandito Agung
 */
public class ABSServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ABSServer server = new ABSServer();
        server.run();
    }

    public void run() {

        String webappDirLocation = "external";
        Tomcat tomcat = new Tomcat();

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8081";
        }
        tomcat.setPort(Integer.valueOf(webPort));

        Context ctx;
        try {
            ctx = tomcat.addWebapp("", new File(webappDirLocation).getAbsolutePath());
            Tomcat.addServlet(ctx, "absservlet", new ABSServlet());
            ctx.addServletMapping("", "absservlet");
            ctx.addServletMapping("*.abs", "absservlet");
            System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());
            
            try {
                tomcat.start();
            } catch (LifecycleException ex) {
                Logger.getLogger(ABSServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            tomcat.getServer().await();
        } catch (ServletException ex) {
            Logger.getLogger(ABSServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
