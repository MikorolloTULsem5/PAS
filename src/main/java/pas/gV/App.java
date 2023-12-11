package pas.gV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class App {

    public static void main(String[] args) {
        //Gdyby nie zaczytalo z .properties mozna odkomentowac
//        System.setProperty("server.servlet.context-path", "/api");
        SpringApplication.run(App.class, args);
    }
}
