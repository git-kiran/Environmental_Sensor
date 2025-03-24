package com.example.warehouse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class UdpWareHouseListenerServiceTests {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private DatagramSocket mockTemperatureSocket;

    @Mock
    private DatagramSocket mockHumiditySocket;

    @InjectMocks
    private UdpWareHouseListenerService udpWareHouseListenerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() { udpWareHouseListenerService.stopUdpWareHouseListener();}

    @Test
    void testReceiveUdpAndSendToKafka() throws Exception {
        // Simulate UDP message
        String testMessage = "{\"sensor_id\": \"t1\", \"value\": 30}";
        byte[] messageBytes = testMessage.getBytes();

        // Assign different ports to the mock sockets
        when(mockTemperatureSocket.getLocalPort()).thenReturn(3344);  // Temperature port
        when(mockHumiditySocket.getLocalPort()).thenReturn(3355);     // Humidity port

        // Mock socket behavior
        doAnswer(invocation -> {
            DatagramPacket packet = invocation.getArgument(0);
            if (packet.getSocketAddress().toString().contains("3344")) {
                packet.setData(messageBytes);
                packet.setLength(messageBytes.length);
            }
            return null;
        }).doThrow(new RuntimeException("Stop Infinite loop")).when(mockTemperatureSocket).receive(any(DatagramPacket.class));

        // Mock behavior for the humidity socket ensure it does nothing
        doNothing().when(mockHumiditySocket).receive(any(DatagramPacket.class));

        // Start UDP listener with the mock temperature socket and topic
        udpWareHouseListenerService.startUdpWareHouseListener(mockTemperatureSocket, "sensor-temperature");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ensure no interaction with the "sensor-humidity" topic
        verify(kafkaTemplate, never()).send("sensor-humidity", testMessage);
    }

    @Test
    void testReceiveMultipleUdpSendToKafka() throws Exception {
        // Sample UDP messages
        String temperatureMessage = "{\"sensor_id\": \"t1\", \"value\": 25}";
        String humidityMessage = "{\"sensor_id\": \"h1\", \"value\": 40}";

        // Create CompletableFutures to stop the listener threads after processing
        CompletableFuture<Void> temperatureFuture = new CompletableFuture<>();
        CompletableFuture<Void> humidityFuture = new CompletableFuture<>();

        // Simulate the behavior of each socket
        doAnswer(invocation -> {
            DatagramPacket packet = invocation.getArgument(0);
            packet.setData(temperatureMessage.getBytes());
            packet.setLength(temperatureMessage.length());
            temperatureFuture.complete(null);  // Stop after processing one message
            return null;
        }).doThrow(new RuntimeException("Stop Infinite loop")).when(mockTemperatureSocket).receive(any(DatagramPacket.class));

        doAnswer(invocation -> {
            DatagramPacket packet = invocation.getArgument(0);
            packet.setData(humidityMessage.getBytes());
            packet.setLength(humidityMessage.length());
            humidityFuture.complete(null);  // Stop after processing one message
            return null;
        }).doThrow(new RuntimeException("Stop Infinite loop")).when(mockHumiditySocket).receive(any(DatagramPacket.class));

        // Start the listeners
        udpWareHouseListenerService.startUdpWareHouseListener(mockTemperatureSocket, "sensor-temperature");
        udpWareHouseListenerService.startUdpWareHouseListener(mockHumiditySocket, "sensor-humidity");

        // Wait for the messages to be processed
        temperatureFuture.join();
        humidityFuture.join();

        // Verify the messages were sent only once to the correct topics
        verify(kafkaTemplate).send("sensor-temperature", temperatureMessage);
        verify(kafkaTemplate).send("sensor-humidity", humidityMessage);
    }

    @Test
    void testEmptyUdpMessage() throws Exception {
        // Simulate an empty UDP message
        String testMessage = "";
        byte[] messageBytes = testMessage.getBytes();

        // Mock socket behavior
        doAnswer(invocation -> {
            DatagramPacket receivedPacket = invocation.getArgument(0);
            receivedPacket.setData(messageBytes);
            return null;
        }).doThrow(new RuntimeException("Stop Infinite loop")).when(mockTemperatureSocket).receive(any(DatagramPacket.class));

        try {
            udpWareHouseListenerService.startUdpWareHouseListener(mockTemperatureSocket, "sensor-temperature");
        } catch (RuntimeException ignored) {
        }

        // Verify that Kafka send was not called with an empty message
        verify(kafkaTemplate, times(0)).send("sensor-temperature", testMessage);
    }

}
