package fingerprintcorev2.Configs;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fingerprintcorev2.Views.HomeUI;
import fingerprintcorev2.Views.fpLibrary;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.json.JSONException;

public class Configs
        extends JFrame {

    public static String username = "username";
    public static String SupervisorCode = "SupervisorCode";
    public static String lastname = "lastname";
    public static String image = "image";
    public static final String imageSize = "imageSize";
    public static final String empCode = "empCode";
    public static final String isLoggedIn = "isLoggedIn";
    public static final String dateLoggedIn = "dateLoggedIn";
    public static final String dateLoggedInWithEmpCode = "dateLoggedInWithEmpcode";
    private static Preferences prefs;
    public static final String empBranch = "empBranch";
    public static final String BASE_URL = "BASE_URL";
    public static String isSupervisor = "isSupervisor";

    public Configs() {
        prefs = Preferences.userRoot().node("FingerprintUI");
    }

    public void DrawImage(JLabel fingerprintpanel, Image img) {
        int w = 256;
        int h = 288;
        byte[] imageraw = new byte[w * h];
        int[] imagesize = new int[1];
        int[] rawpic = new int[w * h];

        fpLibrary.INSTANCE.GetImage(imageraw, imagesize);
        for (int i = 0; i < w * h; i++) {
            int m = imageraw[i] & 0xFF;
            rawpic[i] = (m | m << 8 | m << 16 | 0xFF000000);
        }
        img = createImage(new MemoryImageSource(w, h, rawpic, 0, w));
        System.out.print(img + " after being created");
        fingerprintpanel.setIcon(new ImageIcon(img));
    }

    public static boolean checkInternet() {
        boolean connected = false;
        try {
            URL url = new URL("http://www.instanceofjava.com");

            URLConnection connection = url.openConnection();
            connection.connect();

            System.out.println("Internet Connected");
            connected = true;
        } catch (Exception e) {
            System.out.println("Sorry, No Internet Connection");
            connected = false;
        }
        return connected;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void runShutDownService(String empCode) {
        prefs = Preferences.userRoot().node("FingerprintClass");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                Configs.loggedInFalse(empCode);
                Configs.prefs.get("badShutdown", "");

                Configs.prefs.put("badShutdown", "yes");

                Configs.prefs.putInt("userinitCout", 0);
                System.out.println("shutting down---");
            }
        }));
    }

    /**
     * Returns time logged in
     * @return 
     */
    public String timeLoggedIn() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        return formatter.format(date);
    }
    
    /**
     * Returns date logged In
     * @return 
     */
    public static String dateLoggedIn() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    public static void notifyUser(Component component, String message) {
        JOptionPane.showMessageDialog(component, message);
    }

    private static void loggedInFalse(String empCode) {
        try {
            HttpResponse<JsonNode> postData = Unirest.post(prefs.get("BASE_URL", "")
                    + "/fingerprintCore/userLoginDetails")
                    .field("empCode", empCode)
                    .field("isLoggedIn", Boolean.valueOf(false)).asJson();

            if (((JsonNode) postData.getBody()).getObject().getBoolean("success")) {
                prefs.put(username, "");
                prefs.put("isLoggedIn", "no");
            }
        } catch (UnirestException ex) {
            Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
