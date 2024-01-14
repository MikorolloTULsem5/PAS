package nbd.gv.mvc;

import jakarta.faces.webapp.FacesServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppMVC {

    public static void main(String[] args) {
        SpringApplication.run(AppMVC.class, args);
    }

    @Bean
    public ServletRegistrationBean<?> servletRegistrationBean() {
        FacesServlet servlet = new FacesServlet();
        return new ServletRegistrationBean<>(servlet, "*.jsf");
    }
}
