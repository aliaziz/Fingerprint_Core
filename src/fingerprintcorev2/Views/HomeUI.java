//<editor-fold defaultstate="collapsed" desc="comment">
package fingerprintcorev2.Views;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fingerprintcorev2.Configs.Configs;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import org.apache.commons.codec.binary.*;
import org.json.JSONException;

public class HomeUI extends javax.swing.JFrame {

    private static Preferences prefs;
    private Timer timer;
    String usernameStored;
    String lastnameStored;
    String isLoggedIn;
    String usernameSupervisor;
    String lastnameSupervisor;
    java.awt.Image img;
    String dateLoggedIn;
    String dateLoggedInWithEmpCode;
    byte[] fingerPrintValueByte = new byte[100];
    byte[] fingerPrintValueByteEmpty = new byte[100];
    int fingerPrintValueByteSize;
    int fingerPrintValueByteSizeEmpty;
    Boolean isSupervisor = true;
    private byte[] matbuf;
    private int[] matsize;

    int[] randomeTime = {3600000};
    Configs configs = new Configs();
    List<String> supervisorBranchesList = new java.util.ArrayList();
    private JLabel fingeprintImage;

    public HomeUI() {
        this.matsize = new int[1];
        this.matbuf = new byte[100];
        this.timer = new Timer();
        initComponents();
        prefs = Preferences.userRoot().node("FingerprintClass");
        usernameStored = prefs.get(Configs.username, "");
        lastnameStored = prefs.get(Configs.lastname, "");
        isLoggedIn = prefs.get("isLoggedIn", "");
        Configs.runShutDownService(prefs.get("empCode", ""));

        if (!usernameStored.equalsIgnoreCase("username")) {
            topMessage.setText("Welcome");
        }

        startSessionCount();
        setDefaultCloseOperation(2);
    }

    private void startSessionCount() {
        timer.schedule(new java.util.TimerTask() {
            public void run() {
                System.out.print("running....");
                dispose();
                HomeUI.this.sendLogoutTime();

                timer.cancel();
            }
        }, getRandomTime(randomeTime));
    }

    private JLabel fingerprintStatusLabel;

    private int getRandomTime(int[] array) {
        int rnd = new java.util.Random().nextInt(array.length);
        System.out.println(array[rnd] + " .... seconds");
        return array[rnd];
    }

    private JPanel jPanel1;
    private JPanel jPanel2;
    private JButton scanSupervisorFinger;
    private JTextField supervisorCode;
    private JLabel topMessage;
    private JLabel welcomeMessage;

    private void initComponents() {
        jPanel1 = new JPanel();
        welcomeMessage = new JLabel();
        topMessage = new JLabel();
        jPanel2 = new JPanel();
        scanSupervisorFinger = new JButton();
        supervisorCode = new JTextField();
        fingerprintStatusLabel = new JLabel();
        fingeprintImage = new JLabel();

        setDefaultCloseOperation(3);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Session Active", 0, 0, null, new Color(0, 51, 255)));

        welcomeMessage.setFont(new Font("Myriad Hebrew", 0, 24));
        welcomeMessage.setForeground(new Color(0, 204, 51));
        welcomeMessage.setHorizontalAlignment(0);
        welcomeMessage.setText("You are logged In");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(welcomeMessage, -1, 613, 32767).addContainerGap()));

        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(20, 20, 20).addComponent(welcomeMessage).addContainerGap(45, 32767)));

        topMessage.setFont(new Font("Tahoma", 0, 14));
        topMessage.setHorizontalAlignment(0);
        topMessage.setText("Welcome username");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Supervisor partial login", 0, 0, null, new Color(0, 102, 204)));

        scanSupervisorFinger.setText("Supervisor fingerprint scan");
        scanSupervisorFinger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                HomeUI.this.scanSupervisorFingerActionPerformed(evt);
            }

        });
        supervisorCode.setText("Enter code");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(supervisorCode).addGap(18, 18, 18).addComponent(scanSupervisorFinger, -2, 201, -2).addGap(19, 19, 19)));

        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(supervisorCode).addComponent(scanSupervisorFinger, -1, 34, 32767)).addContainerGap(-1, 32767)));

        fingerprintStatusLabel.setFont(new Font("Futura Bk BT", 0, 14));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(fingeprintImage, -2, 198, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(fingerprintStatusLabel, -1, -1, 32767)).addComponent(topMessage, -1, -1, 32767).addComponent(jPanel1, -1, -1, 32767).addComponent(jPanel2, -1, -1, 32767)).addContainerGap()));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(topMessage).addGap(18, 18, 18).addComponent(jPanel1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel2, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fingeprintImage, -2, 214, -2).addComponent(fingerprintStatusLabel, -2, 42, -2)).addContainerGap(23, 32767)));

        pack();
    }

    private void scanSupervisorFingerActionPerformed(ActionEvent evt) {
        String empCode = supervisorCode.getText().toString();
        if ((empCode != null) && (!empCode.isEmpty())) {
            System.out.println("stored finger byte " + fingerPrintValueByte + " " + fingerPrintValueByteSize);
            getSupervisorFingerprintFromServer(empCode);
            fpLibrary.INSTANCE.GenFpChar();
        } else {
            Configs.notifyUser(this, "Please enter employee code");
        }
    }

    public void start() {
        timer.schedule(new java.util.TimerTask() {

            public void run() {
                HomeUI.this.fpsmessage();
            }
        }, 0L, 100L);
    }

    private void fpsmessage() {
        int fpsmsg = fpLibrary.INSTANCE.GetWorkMsg();
        int retmsg = fpLibrary.INSTANCE.GetRetMsg();

        switch (fpsmsg) {
            case 1:
                fingerprintStatusLabel.setText("Please Connect Device");
                break;
            case 3:
                fingerprintStatusLabel.setText("Please Lift Finger");
                break;
            case 2:
                fingerprintStatusLabel.setText("Please Place Finger");
                break;
            case 5:
                if (retmsg == 1) {
                    fpLibrary.INSTANCE.GetFpCharByGen(matbuf, matsize);

                    int ret = fpLibrary.INSTANCE.MatchTemplateOne(matbuf, fingerPrintValueByte, fingerPrintValueByteSize);
                    System.out.println("Match Val : " + String.valueOf(ret));

                    if (ret < 50) {
                        fingerprintStatusLabel.setText("Finger print doesnt exist");
                    } else {
                        System.out.print("fingerprint in hex " + Hex.encodeHex(fingerPrintValueByte));
                        fingerprintStatusLabel.setText("Finger match found!");

                        sendLogInTime();
                    }
                } else {
                    fingerprintStatusLabel.setText("Capture Fail");
                }
                break;
            case 7:
                configs.DrawImage(fingeprintImage, img);

                System.out.println("returned image for drawing " + String.valueOf(img));
                break;
            case 8:
                fingerprintStatusLabel.setText("Time Out");
        }
    }

    private void sendLogInTime() {
        try {
            HttpResponse<JsonNode> sessionTimeRenew = Unirest.post(prefs.get("BASE_URL", "")
                    + "/fingerprintCore/timeAtWorkSupervisor")
                    .field("first_name", usernameSupervisor)
                    .field("last_name", lastnameSupervisor)
                    .field("empCode", supervisorCode.getText().toString())
                    .field("loginTime", configs.currentSystemTime())
                    .field("logoutTime", 0)
                    .field("isLoggedIn", Boolean.valueOf(true))
                    .field("emp_branch", prefs.get("empBranch", ""))
                    .field("isSuperVisor", Boolean.valueOf(true)).asJson();

            if (((JsonNode) sessionTimeRenew.getBody()).getObject().getBoolean("success")) {
                postUserDetails();
            } else {
                Configs.notifyUser(this, "Failed to login.Contact Admin");
            }

            System.out.print("Session from server " + ((JsonNode) sessionTimeRenew.getBody()).getObject() + " " + prefs.get("BASE_URL", ""));
        } catch (UnirestException ex) {
            Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void postUserDetails() {
        try {
            HttpResponse<JsonNode> postData = Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/userLoginDetails").field("empCode", supervisorCode.getText().toString()).field("isLoggedIn", Boolean.valueOf(true)).asJson();

            if (((JsonNode) postData.getBody()).getObject().getBoolean("success")) {
                fingerprintStatusLabel.setText("Supervisor logged in");
            } else {
                Configs.notifyUser(this, "Failed to login.Contact Admin");
            }
        } catch (UnirestException ex) {
            Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getSupervisorFingerprintFromServer(String employeeCode) {
        try {
            HttpResponse<JsonNode> request = Unirest.get(prefs.get("BASE_URL", "") + "/fingerprintCore/fingerprint/supervisorByCode/" + employeeCode).header("Content-Type", "application/json").asJson();

            if (((JsonNode) request.getBody()).getObject().getBoolean("success")) {
                String image_hex = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("image_hex");
                System.out.println("image hex from server " + image_hex);
                fingerPrintValueByte = Configs.hexStringToByteArray(image_hex);
                System.out.println("image byte after conversion server " + fingerPrintValueByte);
                usernameSupervisor = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("first_name");
                lastnameSupervisor = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("last_name");
                org.json.JSONArray branches = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getJSONArray("supervisedBranches");
                String y = prefs.get("empBranch", "");

                for (int x = 0; x < branches.length(); x++) {
                    supervisorBranchesList.add(branches.getString(x));
                }
                if (supervisorBranchesList.contains(y)) {
                    start();
                } else {
                    Configs.notifyUser(this, "You are not an authorized supervisor for this branch");
                }
            } else {
                Configs.notifyUser(this, "User is not a supervisor!");
            }
        } catch (UnirestException ex) {
            Configs.notifyUser(this, "Check server connection!");
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
            Configs.notifyUser(this, "User is not a supervisor!");
            prefs.put("empCode", "");
            dispose();
            
            RescanUI login = new RescanUI();
            login.setVisible(true);
        } catch (JSONException ex) {
            Logger.getLogger(HomeUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendLogoutTime() {

        NetworkCalls.sendUserLogoutTime(prefs, configs.currentSystemTime());

        if (supervisorCode.getText().contains("EMP")) {
            NetworkCalls.sendSupervisorLogoutTime(prefs, configs.currentSystemTime(), supervisorCode.getText());
        }

        timer.cancel();
        prefs.put(Configs.username, "");
        prefs.put("isLoggedIn", "no");
        dispose();

        RescanUI login = new RescanUI();
        login.initialiseCounter();
        login.setVisible(true);
    }
}
//</editor-fold>
