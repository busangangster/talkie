package com.e104.mqtt_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;

@IntegrationComponentScan
@SpringBootApplication
public class MqttTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttTestApplication.class, args);
    }

}
