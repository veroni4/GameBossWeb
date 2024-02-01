package com.gameboss.gamebossweb;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author veronica
 */
public class TragetMQTTListener implements MqttCallback {
    private GameBossBean bean;
     
    public TragetMQTTListener(GameBossBean bean){
        this.bean = bean;
    }

    public void messageArrived(String topic, MqttMessage message) {
        
        switch (topic) {
            case GameBossBean.targetTopic:
                JSONObject jmsg = new JSONObject(new String(message.getPayload()));
                System.out.println("Received message: " + jmsg);
            
                try {
                    bean.MqttTargetProcess(jmsg);
                } catch (MqttException ex) {
                    Logger.getLogger(TragetMQTTListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (Exception ex) {
                    Logger.getLogger(TragetMQTTListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

            case GameBossBean.GunTopic:
                break;
            case GameBossBean.sysTopic:
                break;
            case GameBossBean.targetControlTopic:
                break;
            case GameBossBean.targetControlAllTopic:
                break;
        }
    }

    public void connectionLost(Throwable thrwbl) {
        System.out.println("MQTT Connection lost");
        bean.setIsConnected(false);
    }

    public void deliveryComplete(IMqttDeliveryToken imdt) {
        System.out.println("MQTT Delivery Complete: " + imdt.isComplete());
    }
}
