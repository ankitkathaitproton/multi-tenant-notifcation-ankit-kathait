package com.example.multi_tenant_notifcation_ankit_kathait;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiTenantNotifcationAnkitKathaitApplication {

	public static void main(String[] args) {
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
		SpringApplication.run(MultiTenantNotifcationAnkitKathaitApplication.class, args);
	}

}
