package com.example.team_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TeamManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamManagementApplication.class, args);
	}

}
