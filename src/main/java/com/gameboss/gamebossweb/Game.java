/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

/**
 *
 * @author veronica
 */
abstract public class Game {

    protected String gameName;
    protected GameBossBean bean;
    private boolean isStoped;
    protected LocalTime startTime;
    protected int gameDuration;
    private int currentGameSecondsRem;
    protected Timer timer;

    public class GameTask extends TimerTask {

        private Game game;

        public GameTask(Game game) {
            this.game = game;
        }

        public void run() {
            try {
                this.game.onTimerTick();
            } catch (MqttException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Game(String gameName, GameBossBean bean) {
        this.gameName = gameName;
        this.bean = bean;
        this.isStoped = false; //running
        this.startTime = null;
        this.gameDuration = 0; //in min
        this.timer = null;
    }

    public String getGameName() {
        return gameName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getGameDuration() {
        return gameDuration;
    }

    public void setGameDuration(int gameDuration) {
        this.gameDuration = gameDuration;
    }

    //epoche time - epoche starting time
    public String getGameID() {
        return Long.toString(startTime.toEpochSecond(LocalDate.now(), ZoneOffset.UTC));
    }

    abstract public void play(Target t) throws MqttException;

    //duration in minutes
    public void start(int duration) throws MqttException {
        setGameDuration(duration);
        setCurrentGameSecondsRem(duration);
        startTime = LocalTime.now();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new GameTask(this), 0, 1000);
        this.isStoped = false;
        DisplayControl dc = new DisplayControl("start","start");
        bean.publishToDisplay(dc.toMqttMessage());
        System.out.println("game was started"); 
        JSONObject jobj = new JSONObject();
        jobj.put("action", "Game started");
        jobj.put("game", getGameName());
        jobj.put("id", getGameID());
        jobj.put("duration", duration);
        Logger loger = Logger.getLogger("splunkLogger");
        loger.info(jobj.toString());
    }

    public void stop() throws MqttException {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        } else {
            this.isStoped = true;
        }
        this.isStoped = true;
        DisplayControl dc = new DisplayControl("stop","stop");
        bean.publishToDisplay(dc.toMqttMessage());
        JSONObject jobj = new JSONObject();
        jobj.put("action", "Game stoped");
        jobj.put("game", getGameName());
        jobj.put("id", getGameID());
        Logger logger = Logger.getLogger("splunkLogger");
        logger.info(jobj.toString());
    }

    public void reset() throws MqttException {
        bean.publishToAll(Target.Modes.off, Target.Scenarios.SINGLE);
    }

    public boolean expired() {
        //current time - starting time
        if (startTime.until(LocalTime.now(), ChronoUnit.MINUTES) < gameDuration) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.gameName;
    }

    public void onTimerTick() throws MqttException{
        if (expired()) {
            try {
                stop();
                this.bean.setCurrentGame(null);
            } catch (MqttException ex) {
                Logger.getLogger("splunkLogger").log(Level.SEVERE, null, ex);
            }

        }
        this.setCurrentGameSecondsRem((int)(gameDuration * 60 - startTime.until(LocalTime.now(), ChronoUnit.SECONDS)));
        DisplayControl dc = new DisplayControl("timer",this.getCurrentGameSecondsRemStr());
        bean.publishToDisplay(dc.toMqttMessage());
    }

    public ArrayList<int[]> getNextDeltas(int curCol, int curRow) {
        ArrayList<int[]> options = new ArrayList<int[]>();
        for (int rowDelta = -1; rowDelta <= 1; rowDelta++) {
            if ((curRow == 0 && rowDelta != -1)
                    || (curRow == this.bean.getRowsNum() - 1 && rowDelta != 1)
                    || (curRow != 0 && curRow != this.bean.getRowsNum() - 1)) {
                for (int colDelta = -1; colDelta <= 1; colDelta++) {
                    if ((colDelta != 0 || rowDelta != 0)
                            && ((curCol == 0 && colDelta != -1)
                            || (curCol == this.bean.getColumnsNum() - 1 && colDelta != 1)
                            || (curCol != 0 && curCol != this.bean.getColumnsNum() - 1))) {
                        int[] pair = new int[2];
                        pair[0] = colDelta;
                        pair[1] = rowDelta;
                        options.add(pair);
                    }
                }
            }
        }

        return options;
    }
    //checking if ALL targets are off
    public boolean allTargetsAreOff(){
        ArrayList<Target> tempTargets = bean.getTargets();
        for(Target target : tempTargets){
            if(!target.getMode().equals(Target.Modes.off)){
                return false;
            }
        }
        return true;
    }
    //checking if ALL targets are on
    public boolean allTargetsAreOn(){
        ArrayList<Target> tempTargets = bean.getTargets();
        for(Target target : tempTargets){
            if(target.getMode().equals(Target.Modes.off)){
                return false;
            }
        }
        return true;
    }
    /**
     * @return the isStoped (getter)
     */
    public boolean isIsStoped() {
        return isStoped;
    }
    /**
     * @return the currentGameSecondsRem
     */
    public int getCurrentGameSecondsRem() {
        return currentGameSecondsRem;
    }

    /**
     * @param currentGameSecondsRem the currentGameSecondsRem to set
     */
    public void setCurrentGameSecondsRem(int currentGameSecondsRem) {
        this.currentGameSecondsRem = currentGameSecondsRem;
    }
    public String getCurrentGameSecondsRemStr() {
        int minutes = (int) currentGameSecondsRem / 60;
        int seconds = (int) currentGameSecondsRem % 60;
        return minutes + ":" + seconds;
    }

 
}
