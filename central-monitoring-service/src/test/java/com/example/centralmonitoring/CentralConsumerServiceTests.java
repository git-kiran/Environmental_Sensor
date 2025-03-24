package com.example.centralmonitoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CentralConsumerServiceTests {

    @InjectMocks
    private CentralConsumerService centralConsumerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumeTemperature_AlarmTriggered() {
        String message = "sensor_id=t1;value=40";

        // Set up the ByteArrayOutputStream to capture System.out.println output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Act: Simulate consuming the message
        centralConsumerService.consumeTemperature(message);

        // Capture the output printed to the console
        String capturedOutput = outputStream.toString();

        // Verify that the expected alarm message was printed
        assertTrue(capturedOutput.contains("WARNING: Temperature exceeded threshold value"));
    }

    @Test
    void testConsumeHumidity_NormalCondition() {
        String message = "sensor_id=h1;value=40";

        // Set up the ByteArrayOutputStream to capture System.out.println output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);  // Redirect System.out to capture the output

        // Act: Simulate consuming the message
        centralConsumerService.consumeHumidity(message);

        // Capture the output printed to the console
        String capturedOutput = outputStream.toString();

        // Verify that the normal message was printed
        assertTrue(capturedOutput.contains("is normal:"));
    }
}
