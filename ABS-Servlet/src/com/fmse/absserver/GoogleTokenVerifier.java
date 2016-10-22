/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmse.absserver;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Afifun
 */
public class GoogleTokenVerifier {

    /**
     * @param token
     * @return 
     */
    
    private String CLIENT_ID = null;
    
    public GoogleTokenVerifier(){
        FileInputStream input = null;
        Properties prop = new Properties();
        
        try {
            
            input = new FileInputStream("auth.properties");
            
            // load a properties file
            prop.load(input);
            this.CLIENT_ID = prop.getProperty("client_id");

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
    
    public Payload verify(String token){
        try {
            String CLIENT_ID = this.CLIENT_ID;
            NetHttpTransport transport = new NetHttpTransport();
            List mClientIDs = Arrays.asList(CLIENT_ID);
            JsonFactory jsonFactory = new GsonFactory();
            GoogleIdTokenVerifier verifier;
            String mProblem = "Verification failed. (Time-out?)";
            String mAudience = this.CLIENT_ID;
            verifier = new GoogleIdTokenVerifier(transport, jsonFactory);
            Payload payload = null;
            GoogleIdToken idToken = GoogleIdToken.parse(jsonFactory, token);
            if (verifier.verify(idToken)) {
                GoogleIdToken.Payload tempPayload = idToken.getPayload();
                System.out.println(tempPayload.getAudience());
                System.out.println(tempPayload.getIssuee());
                System.out.println(tempPayload.getIssuer());
                System.out.println(tempPayload.get("email"));
                if (!tempPayload.getAudience().equals(mAudience)) {
                    mProblem = "Audience mismatch";
                } else if (!mClientIDs.contains(tempPayload.getIssuee())) {
                    mProblem = "Client ID mismatch";
                } else {
                    payload = tempPayload;
                }
            } else {
                System.out.println("Invalid ID token.");
            }
            return payload;
        } catch (GeneralSecurityException e) {
            System.out.println("Security issue: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("Network problem: " + e.getLocalizedMessage());
        }
        catch (IllegalArgumentException e){
            System.out.println("Token Problem: " + e.getLocalizedMessage());
        }
        catch (Exception e){
            System.out.println("Exception: " + e.getLocalizedMessage());
        }
        
        return null;
    }
}
