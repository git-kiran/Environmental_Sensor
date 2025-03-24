package com.example.warehouse;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Service
public class UdpWareHouseListenerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private DatagramSocket temperatureSocket;
    private DatagramSocket humiditySocket;

    public UdpWareHouseListenerService(KafkaTemplate<String, String> kafkaTemplate,
                                       DatagramSocket temperatureSocket,
                                       DatagramSocket humiditySocket) {
        this.kafkaTemplate = kafkaTemplate;

        // Start UDP listeners only if the port is correct (3344 for temperature, 3355 for humidity)
        if (temperatureSocket.getLocalPort() == 3344) {
            startUdpWareHouseListener(temperatureSocket, "sensor-temperature");
        }
        if (humiditySocket.getLocalPort() == 3355) {
            startUdpWareHouseListener(humiditySocket, "sensor-humidity");
        }
    }

    void startUdpWareHouseListener(DatagramSocket socket, String topic) {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("Listening for UDP messages on port " + socket.getLocalPort());

                while (true) {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Received: " + message);
                    if (!message.isEmpty())
                        kafkaTemplate.send(topic, message);
                    System.out.println("Published to the message to topic " + topic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopUdpWareHouseListener() {
        if (temperatureSocket != null && !temperatureSocket.isClosed()) {
            temperatureSocket.close();
        }
        if (humiditySocket != null && !humiditySocket.isClosed()) {
            humiditySocket.close();
        }
    }
}

