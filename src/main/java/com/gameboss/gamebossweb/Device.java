/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;

/**
 *
 * @author veronica
 */
abstract public class Device {
    String id;
    String type;
    String host;
    
    public Device(String type, String id, String host){
 
        this.type = type;
        this.id = id;
        this.host = host;
    }
    
    public String toString(){
        return   " host: " + host + " id: " + id;
    }
    
    public boolean equals(Object other){
        Device d = (Device) other;
        return  this.type.equals(d.type) && this.id.equals(d.id) && this.host.equals(d.host);
    }
   
    abstract public String getType();
    
    public void setType(String type){
        this.type = type;
    }
    
    public String getId(){
        return id;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public String getHost(){
        return host;
    }
    
    public void setHost(String host){
        this.host = host;
    }
}
