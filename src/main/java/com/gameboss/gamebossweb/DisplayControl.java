/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import static com.gameboss.gamebossweb.GameBossBean.targetDisplayTopic;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 *
 * @author veronica
 */
public class DisplayControl {

    private String mode;
    private String msg;
    
    public DisplayControl(String mode, String msg){
        this.mode = mode;
        this.msg = msg;
    }
    
    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public JSONObject toJSON (){
        JSONObject jobj = new JSONObject();
        jobj.put("mode", "timer");
        jobj.put("msg", msg);
        return jobj;
        
    }
    public MqttMessage toMqttMessage(){
        return new MqttMessage(this.toJSON().toString().getBytes());
        
    }
    
}
