/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import com.gameboss.gamebossweb.Game;
import java.util.ArrayList;
import java.util.LinkedList;
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
//IDEA: random direction but checking current position of head and making sure it is moving around
public class KillSnake extends Game {

    public final static int milliTime = 3000;
    int deltaRow; //row increment
    int deltaColumn; // column increment 
    int nextRow;
    int nextColumn;

    protected Timer snakeTimer;
    protected LinkedList<Target> snake;

    public class SnakeGameTask extends TimerTask {

        private KillSnake game;

        public SnakeGameTask(KillSnake game) {
            this.game = game;
        }

        public void run() {
            try {
                this.game.nextMove(false);
            } catch (MqttException ex) {
                Logger.getLogger("splunkLogger").log(Level.SEVERE, null, ex);
            }
        }
    }

    public KillSnake(String name, GameBossBean bean) {
        super(name, bean);
        this.deltaRow = 1;
        this.deltaColumn = 1;
        this.nextRow = 0;
        this.nextColumn = 0;
        this.snake = new LinkedList<>();
    }

    public void updateColour() throws MqttException {

        for (int i = 0; i < snake.size(); i++) {
            Target t = snake.get(i);
            t.setMode(i==0?Target.Modes.cWhite:Target.Modes.cGreen);
            t.setScenario(Target.Scenarios.SINGLE);
            bean.publishToTarget(t);
        }
    }

    public void play(Target t) throws MqttException {
        if (this.isIsStoped()) {
            return;
        }
        //check if t is in snake list
        if (snake.contains(t) || (snake.size()==1 && t.getGunId()>-1 && t.getScenario().equals(Target.Scenarios.SINGLE)) ) {
            Target last = snake.pollLast();
            last.setMode(Target.Modes.off);
            bean.publishToTarget(last);
        } else {
            //device rebot due to internl exception
            t.setMode(Target.Modes.off);
            t.setScenario(Target.Scenarios.SINGLE);
            bean.publishToTarget(t);
        }
    }
    
//true = building snake(start())  false = moving (run()) 
    public void nextMove(boolean flag) throws MqttException {
        if(snake.size()==0){
            stop();
        }
        Target head = snake.getFirst();
        //snake NOT at the border
        if (head.getColumn() > 0 && head.getColumn() < bean.getColumnsNum() - 1 && head.getRow() > 0 && head.getRow() < bean.getRowsNum() - 1) {
            int col = head.getColumn() + deltaColumn;
            int row = head.getRow() + deltaRow;
            Target temp = bean.getTargetAt(col, row);
            snake.addFirst(temp);
            updateColour();

        } else {
            //snake is at border
            ArrayList<int[]> deltas = getNextDeltas(head.getColumn(), head.getRow());
            for (int d = deltas.size() - 1; d >= 0; d--) {
                int col = head.getColumn() + deltas.get(d)[0]; 
                int row = head.getRow() + deltas.get(d)[1];
                Target temp = bean.getTargetAt(col, row);
                //avoid snake going backwards
                if (snake.contains(temp)) {
                    deltas.remove(d);
                }
            }
            //snake stuck, will slowly die
            if (!deltas.isEmpty()) {
                //choosig next deltas randomly
                Random rand = new Random();
                int randomDelta = rand.nextInt(deltas.size());
                deltaColumn = deltas.get(randomDelta)[0];
                deltaRow = deltas.get(randomDelta)[1];
                int col = head.getColumn() + deltaColumn;
                int row = head.getRow() + deltaRow;
                Target t = bean.getTargetAt(col, row);
                snake.addFirst(t);
                updateColour();
            }

        }
        //removing tail
        if (!flag) {
            Target tail = snake.pollLast();
            tail.setMode(Target.Modes.off);
            bean.publishToTarget(tail);
        }

    }

    public void start(int duration) throws MqttException {
        super.start(duration);
        Target head = bean.randomTarget();
        snake.addFirst(head);
        updateColour();
        ArrayList<int[]> deltas = getNextDeltas(head.getColumn(), head.getRow());
        Random rand = new Random();
        int randomDelta = rand.nextInt(deltas.size());
        deltaColumn = deltas.get(randomDelta)[0];
        deltaRow = deltas.get(randomDelta)[1];
        for (int i = 0; i < 3; i++) {
            nextMove(true);
        }
        this.snakeTimer = new Timer();
        this.snakeTimer.scheduleAtFixedRate(new KillSnake.SnakeGameTask(this), 0, milliTime);

    }

    public void stop() throws MqttException {
        super.stop();
        if (this.snakeTimer != null) {
            this.snakeTimer.cancel();
            this.snakeTimer.purge();
            this.snakeTimer = null;
        }
        bean.publishToAll(Target.Modes.hit, Target.Scenarios.SINGLE);

    }

}
