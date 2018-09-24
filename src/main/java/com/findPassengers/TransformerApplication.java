package com.findPassengers;

import com.findPassengers.mvc.service.TransformerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;


@SpringBootApplication()
@EnableAutoConfiguration(exclude = { WebMvcAutoConfiguration.class})
public class TransformerApplication  implements CommandLineRunner {

    @Autowired
    private TransformerService service;

    public static void main(String[] args) {
        SpringApplication.run(TransformerApplication.class, args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        service.work();
    }

}
