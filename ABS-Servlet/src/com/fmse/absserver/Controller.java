/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmse.absserver;

import java.util.Arrays;

/**
 *
 * @author Afifun
 */
public class Controller {
    
    public String controllerURI = "";
    
    public Controller(String route){
        this.controllerURI = route;
    }
    
    public String[] getRoles(){
        
        String[] result = new String[0];
        
        String[] temp = this.controllerURI.split(":");
        String roles = null;
        
        if (temp.length > 1){
            roles = temp[0];
            String[] role_list = roles.split(",");
            
            for (int ii=0; ii < role_list.length; ii++){
                role_list[ii] = role_list[ii].trim();
            }
            result = role_list;
        }
        
        return result;
    }
    
    public String getControllerName(){
        String controllerName = null;
        
        String[] temp = this.controllerURI.split(":");
        String controller = null;
        if (temp.length > 1){
            controller = temp[1];
        }
        else if(temp.length == 1){
            if (temp[0].contains("@")){
                controller = temp[0];
            }
        }
        else {
            //do nothing
        }
        
        if (controller != null){
            controllerName = controller.split("@")[0] + "_c";
        }
        
        return controllerName;
    }
    
    public String getMethodName(){
        String methodName = null;
        
        String[] temp = this.controllerURI.split(":");
        String controller = null;
        if (temp.length > 1){
            controller = temp[1];
        }
        else if(temp.length == 1){
            if (temp[0].contains("@")){
                controller = temp[0];
            }
        }
        else {
            //do nothing
        }
        
        if(controller != null){
            try{
                methodName = controller.split("@")[1];
            }
            catch (ArrayIndexOutOfBoundsException e){
                //methodName is not defined
            }
            catch (Exception e){
                //set methodName is null for all exceptions
            }
        }
        return methodName;
    }
    
    @Override
    public String toString(){
        return "Roles : " + Arrays.toString(this.getRoles()) + ", " + 
                "Controller Name : " + this.getControllerName() + ", " + 
                "Method Name : " + this.getMethodName();
    }
}
