package com.porapipat.porapipat_api;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;

@Log4j2
@SpringBootApplication
public class PorapipatApiApplication {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(PorapipatApiApplication.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("\u001B[36m╔═══════════════════════════════════════════════════════════════╗\u001B[0m");
		log.info("\u001B[36m║             PorapipatApiApplication Initializing              ║\u001B[0m");
		log.info("\u001B[36m╚═══════════════════════════════════════════════════════════════╝\u001B[0m");

		log.info("\u001B[33mActive profile: {}\u001B[0m", env.getActiveProfiles()[0]);
		log.info("\u001B[33mApplication name: {}\u001B[0m", env.getProperty("spring.application.name"));

		try {
			log.info("\u001B[32mDatabase URL: {}\u001B[0m", dataSource.getConnection().getMetaData().getURL());
			log.info("\u001B[32mDatabase connection established successfully\u001B[0m");
		} catch (SQLException e) {
			log.error("\u001B[31mFailed to establish database connection\u001B[0m", e);
		}

		String jwtSecret = env.getProperty("security.jwt.secret-key");
		if (jwtSecret != null && !jwtSecret.isEmpty()) {
			log.info("\u001B[32mJWT secret key is configured\u001B[0m");
		} else {
			log.warn("\u001B[33mJWT secret key is not configured or empty\u001B[0m");
		}
	}

	@PreDestroy
	public void cleanup() {
		log.info("\u001B[36m╔═══════════════════════════════════════════════════════════════╗\u001B[0m");
		log.info("\u001B[36m║             PorapipatApiApplication Shutting Down             ║\u001B[0m");
		log.info("\u001B[36m╚═══════════════════════════════════════════════════════════════╝\u001B[0m");

		try {
			if (dataSource instanceof AutoCloseable) {
				((AutoCloseable) dataSource).close();
				log.info("\u001B[32mDatabase connection closed\u001B[0m");
			}
		} catch (Exception e) {
			log.error("\u001B[31mError closing database connection\u001B[0m", e);
		}

		if (applicationContext != null && applicationContext.isActive()) {
			applicationContext.close();
			log.info("\u001B[32mApplicationContext closed\u001B[0m");
		}
	}
}