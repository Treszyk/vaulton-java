package dev.vaulton.vaultonapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class VaultonapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaultonapiApplication.class, args);
	}

}
