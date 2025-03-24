package com.example.centralmonitoring;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CentralConsumerService {

    @KafkaListener(topics = "sensor-temperature", groupId = "monitoring-group")
    public void consumeTemperature(String message) {
        processSensorData("Temperature", message, 35);
    }

    @KafkaListener(topics = "sensor-humidity", groupId = "monitoring-group")
    public void consumeHumidity(String message) {
        processSensorData("Humidity", message, 50);
    }

    private void processSensorData(String sensorType, String message, int threshold) {
        String[] parts = message.split(";");
        if (parts.length == 2 && parts[1].startsWith("value=")) {
            int value = Integer.parseInt(parts[1].split("=")[1]);
            if (value > threshold) {
                System.out.println("WARNING: " + sensorType + " exceeded threshold value: " + value);
            } else {
                System.out.println(sensorType + " is normal: " + value);
            }
        }
    }
}

