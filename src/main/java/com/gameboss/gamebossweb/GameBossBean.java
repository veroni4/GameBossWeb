/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package com.gameboss.gamebossweb;

//import TragetMQTTListener;
import java.beans.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author veronica
 */
public class GameBossBean extends java.beans.Beans implements Serializable {
  
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";

    private PropertyChangeSupport propertySupport;
    private String broker;
    private String user;
    private String psw;
    private ArrayList<Gun> guns;
    private ArrayList<Target> targets;
    private ArrayList<Game> games;
    private String pathToJson;
    private int columnsNum;
    private boolean isInitialized;
    private boolean isConnected;
    private MqttClient client;
    private Game currentGame;
    public static final String targetTopic = "LaserGame/Target"; //targets publish hits
    public static final String targetControlTopic = "LaserGame/TargetControl/"; //host name appended @ end. Topic to control particular target. Target sub. to its own topic
    public static final String targetControlAllTopic = "LaserGame/TargetControl/All"; //Topic to control all targets. All targets sub. 
    public static final String targetDisplayTopic = "LaserGame/TargetDisplay";
    public static final String GunTopic = "LaserGame/Gun/";
    public static final String sysTopic = "LaserGame/sys";

    public GameBossBean() {
        propertySupport = new PropertyChangeSupport(this);
        this.isInitialized = false;
        this.isConnected = false;
        client = null;
        currentGame = null;
        games = new ArrayList<>();
        games.add(new Deathmatch("Deathmatch", this));
        games.add(new TargetJumper("TargetJumper", this));
        games.add(new KillSnake("KillSnake", this));
        games.add(new CaptureTerritory("CaptureTerritory", this));
        games.add(new Virus("Virus", this));
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public String startGame(String gameName, int duration) {
        String msg = "";
        try {
            if (currentGame != null) {
                msg = currentGame.getGameName() + " already started";
                return msg;
            }
            Game game = getGameByName(gameName);
            if (game == null) {
                msg = "Invalid " + game.toString();
                return msg;
            }
            if (duration <= 0) {
                msg = "Invalid duration: " + duration;
                return msg;
            }
            setCurrentGame(game);
            game.start(duration);
            msg = "Game " + gameName + " was started, will run for " + duration + " min";

        } catch (MqttException ex) {
            Logger.getLogger("splunkLogger").log(Level.SEVERE, null, ex);
            msg = "Mqtt Exception occured, trying to run " + gameName;
        }
        return msg;
    }

    public String endGame() {
        String msg = "";
        try {
            if (currentGame == null) {
                msg = "game has already been stoped";
            } else if (!currentGame.isIsStoped()) {
                currentGame.stop();
                msg = currentGame.getGameName() + " has been stoped";
                currentGame = null;

            }
        } catch (MqttException ex) {
            Logger.getLogger("splunkLogger").log(Level.SEVERE, null, ex);
            msg = "Mqtt Exception occured, trying to end " + currentGame;
        }
        return msg;
    }

    public Game getGameByName(String name) {
        Game selectedGame = null;
        for (Game g : games) {
            if (g.getGameName().equals(name)) {
                selectedGame = g;
                break;
            }
        }
        return selectedGame;
    }

    public ArrayList<String> getGamesbyName() {
        ArrayList<String> a = new ArrayList<>();
        for (Game g : games) {
            a.add(g.getGameName());
        }
        return a;
    }

    public String setAllTargets(String mode, String scenario) {
        //set targets
        return "Targets have been set to mode: <b>" + mode + "</b> and scenario: <b>" + scenario + "</b>";
    }

    public void initialize(String pathToJson) {
        try {
            pathToJson += "/GameBoss.json";
            setPathToJson(pathToJson);
            //read gameBoss.json file
            //      InputStream stream = getClass().getResourceAsStream(pathToJson); //Input Stream
            //get broker from MQTTConfig
            String strJSON = new String(Files.readAllBytes(Paths.get(pathToJson)));
  //          System.out.println("<<<<<" + strJSON + ">>>>>");
           
            JSONObject jobj = new JSONObject(strJSON);

            JSONObject mqttConfig = jobj.getJSONObject("MQTTConfig");
            broker = mqttConfig.getString("broker");
            user = mqttConfig.getString("user");
            psw = mqttConfig.getString("password");

            //Guns
            guns = new ArrayList<Gun>();
            JSONObject jguns = jobj.getJSONObject("Guns");
            JSONArray gDevices = jguns.getJSONArray("Devices");
            for (int i = 0; i < gDevices.length(); i++) {
                JSONObject gun = gDevices.getJSONObject(i);
                String host = gun.getString("host");
                String id = gun.getString("id");
                guns.add(new Gun("", id, host));
            }
            //Targets
            targets = new ArrayList<Target>();
            JSONObject jtargets = jobj.getJSONObject("Targets");
            int columns = jtargets.getInt("Columns");
            columnsNum = columns;
            JSONArray tDevices = jtargets.getJSONArray("Devices");
            for (int i = 0; i < tDevices.length(); i++) {
                JSONObject target = tDevices.getJSONObject(i);
                String host = target.getString("host");
                String id = target.getString("id");
                int row = i / columnsNum;
                int column = i % columnsNum;
                targets.add(new Target("", id, host, row, column));
            }

            isInitialized = true;

        } catch (IOException ex) {
            Logger.getLogger(GameBossBean.class
                    .getName()).log(Level.SEVERE, null, ex);
            isInitialized = false;
        }
    }

    public void updateMqttConfig(String broker, String user, String psw) throws IOException {
        //1. read json file into json object
        //2. Get mqtt field as json object
        //3. jsonobj.put("broker", "borkerValue");

        String strJSON = new String(Files.readAllBytes(Paths.get(getPathToJson())));
   //     System.out.println("<<<< Old JSON\npath: " + getPathToJson() + " \n JSON:\n" + strJSON + ">>>>" );
        JSONObject jobj = new JSONObject(strJSON);
        JSONObject mqttConfig = jobj.getJSONObject("MQTTConfig");
        mqttConfig.put("broker", broker);
        mqttConfig.put("user", user);
        mqttConfig.put("password", psw); 
      //  System.out.println("<<<< New JSON\npath: " + getPathToJson() + " \n JSON:\n" + jobj.toString() + ">>>>" );
        setBroker(broker); //update broker
        setUser(user);
        setPsw(psw);
        
        FileWriter file = new FileWriter(getPathToJson());
     //   Files.w.write(Paths.get(getPathToJson()), jobj.toString());
        file.write(jobj.toString());
        file.close();
    }

    //Connecting to MQTT broker
    public void Connect() {
        try {
            if (client != null) {
                //   client.isConnected()
                client.disconnect();
                client = null;
            }
            isConnected = true;
            client = new MqttClient(broker, "", new MemoryPersistence());

            client.setCallback(new TragetMQTTListener(this));
            //MQTT connection options
            //mqttConnectOptions control how the client connects to a server
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("");
            connOpts.setPassword("".toCharArray());
            client.connect(connOpts); //CONNECT
            client.subscribe(targetTopic); //SUBSCRIBE

        } catch (MqttException ex) {
            Logger.getLogger(GameBossBean.class.getName()).log(Level.SEVERE, null, ex);
            isConnected = false;
        }

    }

    public void MqttTargetProcess(JSONObject jobj) throws MqttException {
        // 1. find mathcing device in target list using host of jobj 
        String host = jobj.getString("host"); //find position of host, move it to the right, publish message back to topic with new host(updated position), mode, scenario
        JSONObject metrics = jobj.getJSONObject("metrics");
        Target target = getTargetByHost(host);
        if (target == null) {
            //TODO: MSG IN FOOTER
            //frame.message("Target: " + host + " is not configured. add it to the json config file.");
            return;
        }
        //setting mode and scenario from JSON file to Target object
        int mode = metrics.getInt("mode");
        int scenario = metrics.getInt("scenario");
        int gunId = metrics.getInt("gunId");
        target.setGunId(gunId);
        target.setMode(Target.Modes.values()[mode]);
        target.setScenario(Target.Scenarios.values()[scenario]);
        if (currentGame != null) {
            currentGame.play(target);
            //frame.message("Game processed with target: " + host);
        }

    }

    public void publishToTarget(Target target) throws MqttException {
        String topic = targetControlTopic + target.getHost();
        JSONObject jo = new JSONObject();
        jo.put("mode", target.getMode().toString());
        jo.put("scenario", target.getScenario().toString());
        String strPayload = jo.toString();
        MqttMessage msg = new MqttMessage(strPayload.getBytes());
        //frame.message("published to " + topic + "\n payload: " +strPayload );
        System.out.println("published to " + topic + "\n payload: " + strPayload);
        client.publish(topic, msg);
    }

    //publishes to all targets the mode and scenario that they have to be set to
    public void publishToAll(Target.Modes mode, Target.Scenarios scenario) throws MqttException {
        for (Target t : targets) {
            t.setMode(mode);
            t.setScenario(scenario);
        }
        String topic = targetControlAllTopic;
        JSONObject jobj = new JSONObject();
        jobj.put("mode", mode);
        jobj.put("scenario", scenario);
        MqttMessage msg = new MqttMessage(jobj.toString().getBytes());
        String strPayload = jobj.toString();
        //frame.message("published to " + topic + "\n payload: " +strPayload );
        System.out.println("published to " + topic + "\n payload: " + strPayload);
        client.publish(topic, msg);
    }
    
        //called every second
    public void publishToDisplay (MqttMessage mqttMsg) throws MqttException {
        client.publish(targetDisplayTopic, mqttMsg);
    }

    public Target getTargetByHost(String givenHost) {
        for (Target target : targets) {
            if (target.getHost().equals(givenHost)) {
                return target;
            }
        }
        return null;
    }

    public Target randomTarget() {
        Random rand = new Random();
        int upperbound = this.getTargets().size();
        return targets.get(rand.nextInt(upperbound));
    }

    //called every second
//    public void onGameSecondChange(Game game) throws MqttException {
//        DisplayControl dc = new DisplayControl("timer",game.getCurrentGameSecondsRemStr());
//        client.publish(targetDisplayTopic, dc.toMqttMessage());
//    }

    /**
     * @return the broker
     */
    public String getBroker() {
        return broker;
    }

    /**
     * @param broker the broker to set
     */
    public void setBroker(String broker) {
        this.broker = broker;
    }

    public int getColumnsNum() {
        return columnsNum;
    }

    public int getRowsNum() {
        int rows = targets.size() / getColumnsNum();
        if (targets.size() % getColumnsNum() > 0) {
            rows++;
        }
        return rows;
    }

    public Target getTargetAt(int column, int row) {
        return targets.get(columnsNum * row + column);
    }

    /**
     * @return the isInitialized
     */
    public boolean isIsInitialized() {
        return isInitialized;
    }

    /**
     * @return the isConnected
     */
    public boolean isIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * @return the targets
     */
    public ArrayList<Target> getTargets() {
        return targets;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * @param currentGame the currentGame to set
     */
    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    /**
     * @return the games
     */
    public ArrayList<Game> getGames() {
        return games;
    }

    /**
     * @return the pathToJson
     */
    public String getPathToJson() {
        return pathToJson;
    }

    /**
     * @param pathToJson the pathToJson to set
     */
    public void setPathToJson(String pathToJson) {
        this.pathToJson = pathToJson;
    }
    
    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the psw
     */
    public String getPsw() {
        return psw;
    }

    /**
     * @param psw the psw to set
     */
    public void setPsw(String psw) {
        this.psw = psw;
    }

}


