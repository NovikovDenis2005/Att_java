package com.tourplanner.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyLauncher {

    public static void main(String[] args) throws Exception {
        System.out.println("Запуск приложения Tour Planner...");

        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/tour-planner");
        context.setResourceBase("src/main/webapp");

        server.setHandler(context);
        server.start();

        System.out.println("Сервер запущен: http://localhost:8080/tour-planner");
        System.out.println("Остановка - Ctrl+C");

        server.join();
    }
}
