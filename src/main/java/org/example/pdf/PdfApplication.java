package org.example.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class PdfApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfApplication.class, args);
    }

}
