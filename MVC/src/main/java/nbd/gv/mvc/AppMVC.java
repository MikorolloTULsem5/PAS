package nbd.gv.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppMVC {

    public static void main(String[] args) {
        //Gdyby nie zaczytalo z .properties mozna odkomentowac
//        System.setProperty("server.servlet.context-path", "/api");
        SpringApplication.run(AppMVC.class, args);
    }
}
