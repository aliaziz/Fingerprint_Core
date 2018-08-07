/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fingerprintcorev2.Configs;

import fingerprintcorev2.Views.NetworkCalls;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

/**
 *
 * @author Admin
 */
public class Utils {
    private static final Timer loggedOutDurationTimer = new Timer();
    private static int hoursAway = 0;
    
    public static void loggedOutDuration() {
        loggedOutDurationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                hoursAway += 1;
                Preferences prefs = Preferences.userRoot().node("FingerprintClass");
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
          
                NetworkCalls.sendLogoutAlert(prefs, format.format(new Date()), 
                        String.valueOf(hoursAway));
            }

        },0L, 3600000L);
    }
    
    public static void cleanTimer() {
        loggedOutDurationTimer.cancel();
    }
}
