# Environmental_Sensor

This projects is to	design a reactive	system that	includes:

### Warehouse	Service: 
Collects	data from	various	sensors (typically Temperature and Humidity) and sends it	to the Central Monitoring	Service.

### Central	Monitoring Service:	
Configured with	thresholds for Temperature ( Threshold:	35°C ) and Humidity (Threshold:	50% ). Raises	an alarm when	sensor	measurements cross these thresholds. The alarm message should	be visible in	the	logs/console.

This mono repo consists of two individual projects to support the above requirement.

### Tech Stack

- Spring Boot: For auto configurable production-ready Spring application.
- lombok: For reducing boiler plate codes.
- Mockito: For testing console output.
- Maven: For managing the project's build.
- Kafka: Message broker
- Java: Programming


### Running Instructions:

- Clone the Repo and perform docker-compose up -d. Ensure Zookeeper and Kafka are running locally.
- Run the individual services (warehouse-service and central-monitoring-service) from Intellij


### Testing Instructions

- Simulate a sensor measuremen through UDP such as 

``` echo -n "sensor_id=t1;value=30" | nc -u -w1 localhost 3344 ```

OUTPUT: ``` Temperature is normal: 30 ```

``` echo -n "sensor_id=h1;value=80" | nc -u -w1 localhost 3355 ```

OUTPUT: ``` WARNING: Humidity exceeded threshold value: 80 ```
