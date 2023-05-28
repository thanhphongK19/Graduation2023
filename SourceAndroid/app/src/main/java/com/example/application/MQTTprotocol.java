package com.example.application;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.os.Build;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import Database.zone1Database;

public interface MQTTprotocol {
    String host = "7c8f4430aabc40439e135b034a097278.s2.eu.hivemq.cloud";
    String username = "hivemq.webclient.1678716757601";
    String password = "5#HNeSZ9><4gV&Tdn3sp";

    // create an MQTT client
    Mqtt5BlockingClient client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .buildBlocking();

    static void setupConnectMQTT()
    {

        // connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");

        // subscribe to the topic "my/test/topic"
        client.subscribeWith()
                .topicFilter("my/test/topic")
                .send();

    }

    static void publishData(String topic, String data)
    {
        client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(data))
                .send();
    }




}
