/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

import com.gameboss.gamebossweb.Device;
import java.util.ArrayList;

/**
 *
 * @author veronica
 */
public class Target extends Device {

    private int row;
    private int column;
    private Modes mode;
    private Scenarios scenario;
    private int gunId;

    public Target(String type, String id, String host, int row, int column) {
        super(type, id, host);
        this.row = row;
        this.column = column;
        this.mode = Modes.off;
        this.scenario = Scenarios.LOADING;
        this.gunId = 0;
    }

    public String getType() {
        return type;
    }

    public enum Modes {
        off(0), white(1), magenta(2), blue(3), green(4),
        sabotage(5), cWhite(6), cBlue(7), cGreen(8), cMagenta(9), hit(10);

        private int mValue;

        Modes(int mValue) { //new constructor
            this.mValue = mValue;
        }

        public int getValue() {
            return mValue;
        }

        //casting from int to enum
        public Modes intToEnum(int x) {
            if (x >= 0) {
                return Modes.values()[x];
            }
            return null;
        }

    }

    public enum Scenarios {
        LOADING(0), SINGLE(1), RANDOM(2);

        private int sValue;

        Scenarios(int sValue) {
            this.sValue = sValue;
        }

        public int getValue() {
            return sValue;
        }

        //casting from int to enum
        public Scenarios intToEnum(int x) {
            if (x >= 0) {
                return Scenarios.values()[x];
            }
            return null;
        }

    }

    public void startAnimation(Target.Modes mode, Target.Scenarios scenario) {

    }

    public String getHost() {
        return host;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * @return the mode
     */
    public Modes getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Modes mode) {
        this.mode = mode;
    }

    /**
     * @return the scenario
     */
    public Scenarios getScenario() {
        return scenario;
    }

    /**
     * @param scenario the scenario to set
     */
    public void setScenario(Scenarios scenario) {
        this.scenario = scenario;
    }

    /**
     * @return the gunId
     */
    public int getGunId() {
        return gunId;
    }

    /**
     * @param gunId the gunId to set
     */
    public void setGunId(int gunId) {
        this.gunId = gunId;
    }

    public boolean equals(Object o) {
        Target t = (Target) o;
        return this.host.equals(t.host);
    }
}
