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
import com.mashape.unirest.request.HttpRequestWithBody;
import fingerprintcorev2.Configs.Configs;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.json.JSONException;

/**
 *
 * @author Admin
 */
public class NetworkCalls {

    /**
     * Sends alerts when user is logged out for more than an hour
     * @param pref
     * @param currentDay
     * @param hoursAway
     */
    public static void sendLogoutAlert(Preferences pref, String currentDay, String hoursAway) {
        try {
             Unirest.put(pref.get(Configs.BASE_URL, "")
                    + "/fingerprintCore/fingerprint/delayedActivation/"
                    + pref.get("empCode", "n")+"/"+currentDay+"/"+hoursAway)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(NetworkCalls.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends logout details for normal users.
     */
    static void sendUserLogoutTime(Preferences prefs, String timeLoggedOut) {
        try {
            String firstName = prefs.get(Configs.username, "");
            String lastName = prefs.get(Configs.lastname, "");

            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/sessions")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("loginTime", 0)
                    .field("empCode", prefs.get("empCode", ""))
                    .field("isSuperVisor", false).asJson();

            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/timeAtWork")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("loginTime", 0)
                    .field("dateLoggedIn", Configs.currentSystemDate())
                    .field("empCode", prefs.get("empCode", ""))
                    .field("emp_branch", prefs.get("empBranch", ""))
                    .field("isLoggedIn", false)
                    .field("isSuperVisor", false).asJson();
        } catch (UnirestException ex) {
            Logger.getLogger(NetworkCalls.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Sends user login time and session time
     * @param prefs
     * @param loginTime
     * @param isLoggedIn
     * @return 
     */
    static Boolean sendUserLoginTime(Preferences prefs, String loginTime,
            Boolean isLoggedIn) {
        
        try {
            String firstName = prefs.get(Configs.username, "");
            String lastName = prefs.get(Configs.lastname, "");
            String empCode = prefs.get("empCode", "");
            
            HttpResponse<JsonNode> postData = Unirest.post(prefs.get("BASE_URL", "")
                    + "/fingerprintCore/userLoginDetails")
                    .field("empCode", empCode)
                    .field("isLoggedIn", isLoggedIn).asJson();

            HttpResponse<JsonNode> sessionTimeRenew
                    = Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/timeAtWork")
                            .field("first_name", firstName)
                            .field("last_name", lastName)
                            .field("empCode", empCode)
                            .field("loginTime", loginTime)
                            .field("logoutTime", 0)
                            .field("dateLoggedIn", Configs.currentSystemDate())
                            .field("isLoggedIn", isLoggedIn)
                            .field("emp_branch", prefs.get("empBranch", "")).asJson();
            if (((JsonNode) sessionTimeRenew.getBody()).getObject().getBoolean("success")) {
                sendLateDetails(empCode, prefs, loginTime);
                return ((JsonNode) postData.getBody()).getObject().getBoolean("success");
            } else {
                return false;
            }
        } catch (UnirestException | JSONException ex) {
            Logger.getLogger(NetworkCalls.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Sends supervisor logout time.
     */
    static void sendSupervisorLogoutTime(Preferences prefs, String timeLoggedOut, String empCode) {
        try {
            String firstName = prefs.get(Configs.username, "");
            String lastName = prefs.get(Configs.lastname, "");
            
            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/sessions")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("loginTime", 0)
                    .field("empCode", empCode)
                    .field("isSuperVisor", true).asJson();
            
            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/timeAtWorkSupervisor")
                    .field("first_name", firstName)
                    .field("last_name", lastName)
                    .field("logoutTime", timeLoggedOut)
                    .field("loginTime", 0)
                    .field("empCode", empCode)
                    .field("dateLoggedIn", Configs.currentSystemDate())
                    .field("emp_branch", prefs.get("empBranch", ""))
                    .field("isLoggedIn", false)
                    .field("isSuperVisor", true).asJson();
        } catch (UnirestException ex) {
            Logger.getLogger(NetworkCalls.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Sends details about if employee is late or not
     * @param empCode
     * @param prefs
     * @param timeLoggedIn 
     */
    private static void sendLateDetails(String empCode, Preferences prefs, String timeLoggedIn) {
        System.out.println("sending late details...");
        try {
            Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/fingerprint/lateEmployees")
                    .field("isLate", false)
                    .field("time", timeLoggedIn)
                    .field("date", Configs.currentSystemDate())
                    .field("empCode", empCode).asJson();
        } catch (UnirestException ex) {
            Logger.getLogger(NetworkCalls.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
