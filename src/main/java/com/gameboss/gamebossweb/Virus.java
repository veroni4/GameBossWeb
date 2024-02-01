/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import com.gameboss.gamebossweb.Target;
import com.gameboss.gamebossweb.Game;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author veronica
 */
public class Virus extends Game {

    protected Timer virusTimer;
    private int initialShotCounter = 0;

    public class VirusGameTask extends TimerTask {

        private Virus game;

        public VirusGameTask(Virus game) {
            this.game = game;
        }

        public void run() {
            try {
                this.game.nextMove();
            } catch (MqttException ex) {
                Logger.getLogger("splunkLogger").log(Level.SEVERE, null, ex);
            }
        }
    }

    public Virus(String name, GameBossBean bean) {
        super(name, bean);
        this.virusTimer = null;

    }

    public void play(Target t) throws MqttException {

        if (this.isIsStoped()) {
            if (t != null) {
                t.setMode(Target.Modes.off);
                t.setScenario(Target.Scenarios.SINGLE);
                bean.publishToTarget(t);
            }
            return;
        }

        if (t == null) {
            return;
        }
        if (t.getGunId() >= 0) {
            if (allTargetsAreOff()) {
                if (initialShotCounter == 0) {
                    initialShotCounter++;
                    for (int i = 0; i < 2; i++) {
                        Target newTarget = bean.randomTarget();
                        newTarget.setMode(Target.Modes.cMagenta);
                        newTarget.setScenario(Target.Scenarios.SINGLE);
                        bean.publishToTarget(newTarget);
                    }
                }
                else{
                    stop();
                }
            }
        }
        return;
    }

    public void nextMove() throws MqttException {
        ArrayList<Target> infectionCandidates = new ArrayList<Target>();
        for (int i = 0; i < bean.getTargets().size(); i++) {
            Target t = bean.getTargets().get(i);
            if (t.getMode().equals(Target.Modes.cMagenta)) {//found infected
                // it is infcted, lets get all it's neighors and infect them
                ArrayList<int[]> deltas = getNextDeltas(t.getColumn(), t.getRow());
                for (int d = 0; d < deltas.size(); d++) {
                    int col = t.getColumn() + deltas.get(d)[0];
                    int row = t.getRow() + deltas.get(d)[1];
                    Target temp = bean.getTargetAt(col, row);
                    if (temp.getMode() == Target.Modes.off) {
                        // we are infecting it
                        if (!infectionCandidates.contains(temp)) {
                            infectionCandidates.add(temp);
                        }
                    }
                }
            }
        }
        for (int cand = 0; cand < infectionCandidates.size(); cand++) {
            infectionCandidates.get(cand).setScenario(Target.Scenarios.SINGLE);
            infectionCandidates.get(cand).setMode(Target.Modes.cMagenta);
            bean.publishToTarget(infectionCandidates.get(cand));
        }

    }

    public void start(int duration) throws MqttException {
        super.start(duration);
        bean.publishToAll(Target.Modes.off, Target.Scenarios.SINGLE);
        initialShotCounter = 0;
        Target newTarget = bean.randomTarget();
        newTarget.setMode(Target.Modes.cMagenta);
        newTarget.setScenario(Target.Scenarios.SINGLE);
        bean.publishToTarget(newTarget);
        this.virusTimer = new Timer();
        this.virusTimer.scheduleAtFixedRate(new VirusGameTask(this), 0, 5000);

    }

    //TODO: update publish command with right mode and scenario
    public void stop() throws MqttException {
        super.stop();
        if (this.virusTimer != null) {
            this.virusTimer.cancel();
            this.virusTimer.purge();
            this.virusTimer = null;
        }
        bean.publishToAll(Target.Modes.hit, Target.Scenarios.SINGLE);
    }
}
