/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import com.gameboss.gamebossweb.Target;
import com.gameboss.gamebossweb.Game;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author veronica
 */
public class TargetJumper extends Game {

    public TargetJumper(String gameName, GameBossBean bean) {
        super(gameName, bean);
    }

    public void play(Target t) throws MqttException {
        //boolean check is used to make sure that all (magenta) targets are stopped
        if (t != null && (this.isIsStoped() || !t.getScenario().equals(Target.Scenarios.SINGLE))) {
            t.setMode(this.isIsStoped()? Target.Modes.hit: Target.Modes.off);
            t.setScenario(Target.Scenarios.SINGLE);
            bean.publishToTarget(t);
            return;
        }

        Target newTarget = bean.randomTarget();
        newTarget.setMode(Target.Modes.magenta);
        newTarget.setScenario(Target.Scenarios.SINGLE);
        bean.publishToTarget(newTarget);
    }

    public void start(int duration) throws MqttException {
        super.start(duration);
        bean.publishToAll(Target.Modes.off, Target.Scenarios.SINGLE);
        play(null);
    }

    public void stop() throws MqttException {
        super.stop();
        bean.publishToAll(Target.Modes.hit, Target.Scenarios.SINGLE);

    }
}
