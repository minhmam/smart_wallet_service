package com.minhpt.smart_wallet_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmartWalletServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartWalletServiceApplication.class, args);
	}

}
