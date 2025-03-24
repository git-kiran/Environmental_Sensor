package com.example.warehouse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.DatagramSocket;

@Configuration
public class UdpWareHouseConfig {

    @Bean
    public DatagramSocket temperatureSocket() throws Exception {
        return new DatagramSocket(3344); // Inject socket for temperature
    }

    @Bean
    public DatagramSocket humiditySocket() throws Exception {
        return new DatagramSocket(3355); // Inject socket for humidity
    }
}
