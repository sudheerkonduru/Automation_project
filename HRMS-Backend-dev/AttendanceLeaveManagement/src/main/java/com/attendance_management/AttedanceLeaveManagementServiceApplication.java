package com.attendance_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AttedanceLeaveManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttedanceLeaveManagementServiceApplication.class, args);
	}

}
