/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gameboss.gamebossweb;


/**
 *
 * @author veronica
 */
public class Gun extends Device{
    
    public Gun (String type, String id, String host){
        super(type, id, host);
    }

    
    public String getType() {
        return type;
    }
}
