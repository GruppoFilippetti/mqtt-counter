package it.filippetti.monitoring;

import it.filippetti.monitoring.commands.CliManager;
import it.filippetti.monitoring.listeners.MessagesListener;
import it.filippetti.monitoring.mqtt.MQTTManager;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by Francesco on 14/06/2016.
 */
public class Counter implements MessagesListener {

    private static final Logger logger = Logger.getLogger(Counter.class.getName());
    private MQTTManager mqttManager;
    private int count = 0;

    /**
     * Constructor.
     */
    public Counter() {
        mqttManager = new MQTTManager();
        mqttManager.addListener(this);
    }

    /**
     * Callback on message received. Add 1 to the total count.
     *
     * @param timestamp The time at which the message was received
     */
    public void messageReceived(long timestamp) {
        logger.log(Level.SEVERE, "Message received!");
        count++;
    }

    // Do test
    public void makeTest(String[] args) {

        // Parse input arguments
        CliManager cli = new CliManager(args);
        boolean parsed = cli.parse();

        // Disable logs unless otherwise specified
        if (!cli.getLoggingEnabled())
            LogManager.getLogManager().reset();

        // Parsing ended up unexpectedly?
        if (!parsed) {
            logger.log(Level.SEVERE, "Parsing ended unexpectedly.");
            System.out.println(-1);
            System.exit(-1);
        }

        // Create client
        String mqttURI = cli.getMqttURI();
        String clientName = cli.getClient();
        boolean created = mqttManager.createClient(mqttURI, clientName);
        if (!created) {
            logger.log(Level.SEVERE, "Could not create client");
            System.out.println(-1);
            System.exit(-1);
        }

        // Build MQTT options for client connection
        final MqttConnectOptions mqttOptions = new MqttConnectOptions();
        mqttOptions.setCleanSession(cli.getCleanSession());
        mqttOptions.setKeepAliveInterval(cli.getKeepAlive());
        if (cli.getUserName()!=null)
            mqttOptions.setUserName(cli.getUserName());
        if (cli.getPassword()!=null)
            mqttOptions.setPassword(cli.getPassword());

        try {

            // Connect client
            boolean connected = mqttManager.connectClient(mqttOptions);
            if (!connected) {
                logger.log(Level.SEVERE, "Could not connect to client");
                System.out.println(-1);
                System.exit(-1);
            }

            // Subscribe to topic
            boolean subscribed = mqttManager.subscribeToTopic(cli.getTopic());
            if (!subscribed) {
                logger.log(Level.SEVERE, "Could not subscribe to topic");
                System.out.println(-1);
                System.exit(-1);
            }

            // Sleep for the given duration of the test, then disconnect
            Thread.sleep(cli.getDuration()*1000);
            logger.log(Level.INFO, "Test is finished. Number of messages received: " + count);

            mqttManager.unsubscribeFromTopic(cli.getTopic());
            mqttManager.disconnectClient();
            System.out.println(count);
            System.exit(0);

        } catch (MqttException e) {
            logger.log(Level.SEVERE, "MQTT Exception", e);
            System.out.println(-1);
            System.exit(-1);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted.", e);
            System.out.println(-1);
            System.exit(-1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unknown error.", e);
            System.out.println(-1);
            System.exit(-1);
        }

    }


    public static void main(String[] args) {
        new Counter().makeTest(args);
    }

}
