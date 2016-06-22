package it.filippetti.monitoring.commands;

import java.util.UUID;
import org.apache.commons.cli.*;

public class CliManager {
    private String[] cliArgs = null;
    private Options cliOptions = new Options();

    // Command line args
    private static String[]         MANDATORY_PARAMS = {"h","t"};

    // Logging
    private static final boolean    LOGS_ENABLED = false;

    // MQTT
    private static final String     PROTOCOL = "tcp";
    private static final String     PORT = "1883";
    private static final boolean    CLEAN_SESSION = true;
    private static final int        KEEPALIVE = 30;
    private static final long       DURATION = 60; // Subscription time span (seconds)

    // Options
    private boolean loggingEnabled;
    private String  mqttUri;
    private String  mqttClient;
    private boolean mqttCleanSession;
    private int     mqttKeepAlive;
    private String  mqttUserName;
    private char[]  mqttPassword;
    private String  mqttTopic;
    private long    mqttDuration;

    public CliManager(String[] args) {
        this.cliArgs = args;
        cliOptions.addOption("H", "help", false, "Show help.");
        cliOptions.addOption("h", "host", true, "Hostname of the MQTT broker to connect to. This field is mandatory.");
        cliOptions.addOption("p", "port", true, "Port number of the MQTT broker to connect to. Default is " + PORT + ".");
        cliOptions.addOption("k", "keepalive", true, "MQTT keepalive, in seconds. Default is " + KEEPALIVE + ".");
        cliOptions.addOption("u", "username", true, "Username for MQTT broker authentication.");
        cliOptions.addOption("P", "password", true, "Password for connection to the MQTT broker.");
        cliOptions.addOption("t", "topic", true, "MQTT topic to publish/subscribe to. This field is mandatory.");
        cliOptions.addOption("d", "duration", true, "Duration of the test (i.e. subscription time span), in seconds. Default is " + DURATION + ".");
        cliOptions.addOption("l", "logging", false, "Enable logging. Default is " + LOGS_ENABLED + ".");
    }


    public boolean parse() {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(cliOptions, cliArgs);
        } catch (ParseException e) {
            help();
            return false;
        }

        boolean mandatorySet = checkMandatoryParams(cmd, MANDATORY_PARAMS);
        if (!mandatorySet) {
            help();
            return false;
        }

        if (cmd.hasOption("H"))
            help();

        // Options creation
        String mqttProtocol = PROTOCOL;
        String mqttHost = cmd.getOptionValue("h");
        String mqttPort = cmd.hasOption("p") ? cmd.getOptionValue("p") : PORT;
        String mqttURI = mqttProtocol + "://" + mqttHost + ":" + mqttPort;
        this.mqttUri = mqttURI;
        this.mqttClient = "COUNTER_" + UUID.randomUUID().toString();
        this.mqttCleanSession = CLEAN_SESSION;
        try {
            this.mqttKeepAlive = cmd.hasOption("k") ? Integer.parseInt(cmd.getOptionValue("k")) : KEEPALIVE;
        } catch (NumberFormatException e) {
            return false;
        }
        if (cmd.hasOption("u"))
            this.mqttUserName = cmd.getOptionValue("u");
        if (cmd.hasOption("P"))
            this.mqttPassword = cmd.getOptionValue("P").toCharArray();
        this.mqttTopic = cmd.getOptionValue("t");
        try {
            this.mqttDuration = cmd.hasOption("d") ? Long.parseLong(cmd.getOptionValue("d")) : DURATION;
        } catch (NumberFormatException e) {
            return false;
        }
        this.loggingEnabled = cmd.hasOption("l") ? true : LOGS_ENABLED;

        return true;
    }

    private void help() {
        // This prints out some help
        new HelpFormatter().printHelp("Counter", cliOptions);
    }

    private boolean checkMandatoryParams(CommandLine cmd, String[] params) {
        for (String p : params) {
            if (!cmd.hasOption(p)) {
                return false;
            }
        }
        return true;
    }


    public String getMqttURI() {
        return mqttUri;
    }
    public String getClient() {
        return mqttClient;
    }
    public boolean getCleanSession() {
        return mqttCleanSession;
    }
    public int getKeepAlive() {
        return mqttKeepAlive;
    }
    public String getUserName() {
        return mqttUserName;
    }
    public char[] getPassword() {
        return mqttPassword;
    }
    public String getTopic() {
        return mqttTopic;
    }
    //public String getMessage() {return mqttMessage;}
    //public long getWaiting() {return mqttWait;}
    public long getDuration() {
        return mqttDuration;
    }
    public boolean getLoggingEnabled() {
        return loggingEnabled;
    }
}