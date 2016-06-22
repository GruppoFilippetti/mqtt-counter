### Usage

    java -jar mqtt-counter-<ver>.jar -h localhost -t some_topic


### Zabbix configuration

#### User parameter config file

1. create /etc/zabbix/zabbix_agentd.d/userparameter_mqttcounter.conf
with this content:


    # mqtt counter user parameter
    UserParameter=mqtt.counter[*],java -jar /opt/smart-platform/mqtt-counter-1.0-SNAPSHOT.jar -h localhost -p $1 -t $2


2. restart zabbix agent


3. test with zabbix_get


    zabbix_get -s 127.0.0.1 -p 10050 -k mqtt.counter[1883,some_topic]


4. finalize the configuration from zabbix web console