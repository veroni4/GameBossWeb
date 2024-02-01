/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import com.gameboss.gamebossweb.Game;
import org.eclipse.paho.client.mqttv3.MqttException;


/**
 *
 * @author veronica
 */
public class Deathmatch extends Game {
    
    Deathmatch(String gameName, GameBossBean bean) {
        super(gameName, bean);
       
    }

    public void play(Target t) throws MqttException {
            
    }

    public void start(int duration) throws MqttException {
        super.start(duration);
        bean.publishToAll(Target.Modes.off, Target.Scenarios.RANDOM);
    }

    public void stop() throws MqttException {
        super.stop();
        bean.publishToAll(Target.Modes.hit, Target.Scenarios.SINGLE);
        
    }
   
    
}
