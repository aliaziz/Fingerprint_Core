/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fingerprintcorev2.Views;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fingerprintcorev2.Configs.Configs;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author Admin
 */
public class NetworkCalls {

    /**
     * Sends alerts when user is logged out for more than an hour
     */
    static void sendLogoutAlert(Preferences pref, Long currentTimeInMills) {
        Unirest.put(pref.get("BASE_URL", "") + "/delayedActivation/"+ pref.get("empCode", "")
                +"/"+currentTimeInMills);
    }

    /**
     * Sends logout details for normal users.
     */
    static void sendUserLogoutTime(Preferences prefs, Long timeLoggedOut) {
        try {
            String firstName = prefs.get(Configs.username, "");
            String lastName = prefs.get(Configs.lastname, "");

            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/sessions")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("empCode", prefs.get("empCode", ""))
                    .field("isSuperVisor", false).asJson();

            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/timeAtWork")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("empCode", prefs.get("empCode", ""))
                    .field("emp_branch", prefs.get("empBranch", ""))
                    .field("isLoggedIn", false)
                    .field("isSuperVisor", false).asJson();
        } catch (UnirestException ex) {
            Logger.getLogger(NetworkCalls.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Sends supervisor logout time.
     */
    static void sendSupervisorLogoutTime(Preferences prefs, Long timeLoggedOut, String empCode) {
        try {
            String firstName = prefs.get(Configs.username, "");
            String lastName = prefs.get(Configs.lastname, "");
            
            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/sessions")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("empCode", empCode)
                    .field("isSuperVisor", Boolean.valueOf(true)).asJson();
            
            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/timeAtWorkSupervisor")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("empCode", empCode)
                    .field("emp_branch", prefs.get("empBranch", ""))
                    .field("isLoggedIn", Boolean.valueOf(false))
                    .field("isSuperVisor", Boolean.valueOf(true)).asJson();
        } catch (UnirestException ex) {
            Logger.getLogger(NetworkCalls.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
