/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmse.absserver;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author Afifun
 */
public class Authentication {
    public String type; //specify authentication type or provider
    private String[] roles;
    public HashMap<String, String> allowedEmail = new HashMap<>();
   
    public Authentication(){
    }
    
    public Authentication(String[] roles){
        this.roles = roles;
        this.loadAllowedEmail(roles);
    }
    
    public void setRoles(String[] roles) {
        this.roles = roles;
        this.loadAllowedEmail(roles);
    }
    
    public Boolean authenticate(String token) throws Exception{
        Boolean result = false;
        
        if (token == null) {
            throw new TokenNullException("Token Is Needed");
        } 
        else if (token.length() > 0) {
            //verify to google oAuth
            
            if (this.type.equalsIgnoreCase("google")){
                //google authentication provider
                GoogleTokenVerifier verifier = new GoogleTokenVerifier();
                GoogleIdToken.Payload payload = verifier.verify(token);
                
                if (payload == null) {
                    throw new TokenInvalidException("Invalid Token");
                }

                if (this.allowedEmail.get(payload.getEmail()) == null) {
                    //if not aloowed email, reject
                    throw new NotPermittedException("You don't have permission to access this service");
                }
                else {
                    result = true;
                }
            }
            else {
                //another authentication (not implemented yet)
            }
            GoogleTokenVerifier verifier = new GoogleTokenVerifier();
            GoogleIdToken.Payload payload = verifier.verify(token);
        } else {
            //tolak jika tidak ada token
            throw new TokenEmptyException("Token Is Empty");
        }
        
        return result;
    }
    
    private void setType(String type){
        this.type = type;
    }
    
    private void loadAllowedEmail(String[] roles){
        
        FileInputStream input = null;
        Properties prop = new Properties();
        
        try {
            
            input = new FileInputStream("auth.properties");
            ArrayList<String> emailList = new ArrayList<>();
            
            // load a properties file
            prop.load(input);
            
            this.setType(prop.getProperty("auth").trim());
            
            String administrator_email = prop.getProperty("administrator");
            this.allowedEmail.put(administrator_email.trim(), administrator_email.trim());
            
            for (String role: roles){
                String[] temp  = prop.getProperty(role).split(",");
                for (String email: temp){
                    this.allowedEmail.put(email.trim(), email.trim());
                }
            }

        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

class TokenNullException extends Exception{
    public TokenNullException(){};
    public TokenNullException(String message){
        super(message);
    }
}

class TokenEmptyException extends Exception{
    public TokenEmptyException(){};
    public TokenEmptyException(String message){
        super(message);
    }
}

class TokenInvalidException extends Exception{
    public TokenInvalidException(){};
    public TokenInvalidException(String message){
        super(message);
    }
}

class NotPermittedException extends Exception{
    public NotPermittedException(){};
    public NotPermittedException(String message){
        super(message);
    }
}
