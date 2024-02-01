/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author veronica
 */
public class CaptureTerritory extends Game {

    public CaptureTerritory(String name, GameBossBean bean) {
        super(name, bean);
    }

    public void play(Target t) throws MqttException {

        if (this.isIsStoped()) {
            if (t != null) {
                t.setMode(Target.Modes.hit);
                t.setScenario(Target.Scenarios.SINGLE);
                bean.publishToTarget(t);
            }
            return;
        }

        if (t == null) {
            return;
        }

        if (!t.getScenario().equals(Target.Scenarios.SINGLE)) {
            //device rebooted and came up in a random scenario, compensating for this
            t.setMode(Target.Modes.cWhite);
            t.setScenario(Target.Scenarios.SINGLE);
            bean.publishToTarget(t);
            return;
        }

        int gunId = t.getGunId();
        Target.Modes mode = Target.Modes.cWhite;
        if (gunId >= 0) {
            if (gunId % 2 == 0) {
                mode = Target.Modes.cBlue;
            }
            else{
                mode = Target.Modes.cGreen;
            }
        } 
        t.setMode(mode);
        bean.publishToTarget(t);
    }

    public void start(int duration) throws MqttException {
        super.start(duration);
        bean.publishToAll(Target.Modes.cWhite, Target.Scenarios.SINGLE);
    }

    public void stop() throws MqttException {
        super.stop();
        bean.publishToAll(Target.Modes.hit, Target.Scenarios.SINGLE);

    }

}
