package fingerprintcorev2.Views;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import fingerprintcorev2.Configs.Configs;
import com.sun.jna.NativeLibrary;
import fingerprintcorev2.Configs.Utils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.commons.codec.binary.*;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.apache.commons.codec.DecoderException;
import org.json.JSONException;
import org.json.JSONObject;

public class RescanUI extends javax.swing.JFrame implements fpLibrary {

    java.awt.Image img;
    private static Preferences prefs;
    private final Timer timer = new Timer();
    private final Timer buttonEnableTimer = new Timer();
    String dateLoggedIn;
    String dateLoggedInWithEmpCode;
    byte[] fingerPrintValueByte = new byte[255];
    byte[] fingerPrintValueByteEmpty = new byte[255];
    int fingerPrintValueByteSize;
    int fingerPrintValueByteSizeEmpty;
    private byte[] matbuf = new byte[100];
    private int[] matsize = new int[1];
    String usernameStored;
    String lastnameStored;
    String empCode;
    String username;
    javax.swing.JFrame frame;
    JPanel panel;
    JLabel lbl;
    JTextField txt;
    String image_hex_code;
    Boolean isSupervisor;
    ArrayList<String> imageList = new ArrayList();

    Configs configs = new Configs();
    private JButton connect_modem;
    private JTextField enterEmpCode;

    public RescanUI() {
        setUndecorated(true);
        setExtendedState(6);
        initComponents();
        setAlwaysOnTop(true);
        setDefaultCloseOperation(0);

        buttonEnableTimer.schedule(new TimerTask() {

            public void run() {  
                scanfinger.setEnabled(true);
                initialiseFingerScanner();
            }
        }, 500L);

        exitButton.setMnemonic(69);
        prefs = Preferences.userRoot().node("FingerprintClass");

        String baseURL = prefs.get("BASE_URL", "");
        System.out.print("base url " + baseURL);
        if (!baseURL.isEmpty()) {
            serverTextField.setText(baseURL);
            serverSetIpButton.setText("Update Server IP");
        }

        Configs.runShutDownService(empCode);
        dateLoggedIn = prefs.get("dateLoggedIn", "");
        dateLoggedInWithEmpCode = prefs.get("dateLoggedInWithEmpcode", "");

        if ((prefs.get("empCode", "").isEmpty()) || (prefs.get("empCode", "").equalsIgnoreCase(""))) {
            System.out.println("here, no emp Code " + prefs.get("empCode", ""));

            enterEmpCode.setEnabled(true);
        } else {
            empCode = prefs.get("empCode", "");
            enterEmpCode.setText(empCode);
            enterEmpCode.setEnabled(false);

            fingerPrintValueByte = prefs.getByteArray(empCode, fingerPrintValueByteEmpty);
            System.out.println("finger print byte after" + fingerPrintValueByte);

            if (fingerPrintValueByte.length < 1) {
                Configs.notifyUser(this, "Employee Doesnt exist");
            } else {
                System.out.println("before adding " + prefs.get("empCode", ""));
                prefs.put("empCode", empCode);
                System.out.println("after adding " + prefs.get("empCode", ""));
                usernameStored = prefs.get(Configs.username, "");
                lastnameStored = prefs.get(Configs.lastname, "");
                prefs.get("empBranch", "");

                fingerPrintValueByteSize = prefs.getInt("imageSize", fingerPrintValueByteSizeEmpty);
            }
        }

        int r = fpLibrary.INSTANCE.OpenDevice(0, 0, 0);

        if (r == 1) {
            if (fpLibrary.INSTANCE.LinkDevice() == 1) {
                statusMessage.setText("Device Ready!");
            } else {
                statusMessage.setText("Link Device Fail");
            }
        } else {
            statusMessage.setText("Failed to connect to device");
        }
    }

    private JLabel errorMessageServer;

    private JButton exitButton;

    private void initComponents() {
        jPanel1 = new JPanel();
        scanfinger = new JButton();
        fingerprintscan = new JPanel();
        fingerprintimage = new JLabel();
        jPanel2 = new JPanel();
        exitButton = new JButton();
        jPanel3 = new JPanel();
        statusMessage = new JLabel();
        connect_modem = new JButton();
        resetEmpCode = new JButton();
        enterEmpCode = new JTextField();
        setEmpCode = new JButton();
        jPanel4 = new JPanel();
        serverTextField = new JTextField();
        serverSetIpButton = new JButton();
        errorMessageServer = new JLabel();
        welcomeMessage = new JLabel();

        setDefaultCloseOperation(3);

        jPanel1.setBorder(BorderFactory.createTitledBorder(null, "Login", 0, 0, null, new Color(51, 102, 255)));

        scanfinger.setText("Scan fingerprint");
        scanfinger.setEnabled(false);
        scanfinger.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                scanfingerActionPerformed(evt);
            }

        });
        fingerprintscan.setBorder(BorderFactory.createTitledBorder(""));
        fingerprintscan.setMaximumSize(new java.awt.Dimension(200, 200));

        GroupLayout fingerprintscanLayout = new GroupLayout(fingerprintscan);
        fingerprintscan.setLayout(fingerprintscanLayout);
        fingerprintscanLayout.setHorizontalGroup(fingerprintscanLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fingerprintimage, GroupLayout.Alignment.TRAILING, -1, 274, 32767));

        fingerprintscanLayout.setVerticalGroup(fingerprintscanLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fingerprintimage, GroupLayout.Alignment.TRAILING, -1, 303, 32767));

        jPanel2.setBorder(BorderFactory.createTitledBorder(""));

        exitButton.setPreferredSize(new java.awt.Dimension(1, 1));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }

        });
        jPanel3.setBorder(BorderFactory.createTitledBorder(""));

        statusMessage.setFont(new java.awt.Font("Myriad Hebrew", 1, 18));
        statusMessage.setForeground(new Color(255, 51, 51));
        statusMessage.setHorizontalAlignment(0);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(statusMessage, -1, 286, 32767).addContainerGap()));

        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(statusMessage, -2, 27, -2).addContainerGap(-1, 32767)));

        connect_modem.setText("Connect Modem");
        connect_modem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                connect_modemActionPerformed(evt);
            }

        });
        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(jPanel3, -2, -1, -2).addGroup(GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup().addComponent(connect_modem).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(exitButton, -2, 85, -2))).addContainerGap(-1, 32767)));

        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(jPanel3, -2, 50, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(exitButton, -2, -1, -2).addComponent(connect_modem)).addContainerGap()));

        resetEmpCode.setText("Reset Employee Code");
        resetEmpCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetEmpCodeActionPerformed(evt);
            }

        });
        enterEmpCode.setEnabled(false);
        enterEmpCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                enterEmpCodeActionPerformed(evt);
            }

        });
        setEmpCode.setText("Submit Emp Code");
        setEmpCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setEmpCodeActionPerformed(evt);
            }

        });
        jPanel4.setBorder(BorderFactory.createTitledBorder(null, "Server Information", 0, 0, null, Color.blue));

        serverTextField.setToolTipText("Enter Server ip");

        serverSetIpButton.setText("Set Server IP");
        serverSetIpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                serverSetIpButtonActionPerformed(evt);
            }

        });
        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup().addContainerGap().addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(errorMessageServer, -1, -1, 32767).addComponent(serverTextField, GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup().addComponent(serverSetIpButton).addGap(0, 0, 32767))).addContainerGap()));

        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addComponent(serverTextField, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(serverSetIpButton).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(errorMessageServer, -1, 23, 32767).addContainerGap()));

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(fingerprintscan, -1, -1, 32767).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(jPanel2, -1, -1, 32767).addComponent(enterEmpCode, -2, 255, -2).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(setEmpCode, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(resetEmpCode, GroupLayout.Alignment.LEADING, -1, -1, 32767)).addComponent(jPanel4, -1, -1, 32767))).addComponent(scanfinger, -1, -1, 32767)).addContainerGap()));

        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(6, 6, 6).addComponent(scanfinger, -2, 33, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fingerprintscan, -2, -1, -2).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jPanel2, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(enterEmpCode, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(setEmpCode).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(resetEmpCode).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel4, -2, -1, -2))).addContainerGap(-1, 32767)));

        welcomeMessage.setFont(new java.awt.Font("SansSerif", 0, 14));
        welcomeMessage.setHorizontalAlignment(0);
        welcomeMessage.setText("Welcome username");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jPanel1, -1, -1, 32767).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(welcomeMessage, -1, -1, 32767).addContainerGap()))));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(welcomeMessage).addGap(18, 18, 18).addComponent(jPanel1, -1, -1, 32767).addContainerGap()));

        pack();
    }

    private void exitButtonActionPerformed(ActionEvent evt) {
        setDefaultCloseOperation(3);
        String password = javax.swing.JOptionPane.showInputDialog(this, "Enter Admin passcode", "Enter passcode");
        if (!"K!nG5B3tt".equals(password)) {
            Configs.notifyUser(this, "Enter valid passcode");
        } else {
            System.exit(0);
        }
    }

    void initialiseCounter() {
        Utils.loggedOutDuration();
    }
    
    private void scanfingerActionPerformed(ActionEvent evt) {
        initialiseFingerScanner();
    }
    
    private void initialiseFingerScanner() {
        if (!prefs.get("BASE_URL", "").isEmpty()) {
            if ((empCode != null) && (!empCode.isEmpty())) {

                if (prefs.get("badShutdown", "").equalsIgnoreCase("yes")) {
                    setUserFalseLoggedIn();
                } else {
                    System.out.println("stored finger byte " + fingerPrintValueByte + " " + fingerPrintValueByteSize);

                    getFingerprintFromServer(empCode);
                    fpLibrary.INSTANCE.GenFpChar();
                }

            } else {
                Configs.notifyUser(this, "Provide employee code");
            }
        } else {
            Configs.notifyUser(this, "Please enter server ip or contact admin");
        }
    }

    private void connect_modemActionPerformed(ActionEvent evt) {
        try {
            Runtime.getRuntime().exec("C:\\ndial.bat", null, new java.io.File("C:\\"));
        } catch (IOException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetEmpCodeActionPerformed(ActionEvent evt) {
        prefs.put("empCode", "");
        timer.cancel();
        Configs.notifyUser(this, "Re enter employee code!");

        dispose();
        IntroPage main = new IntroPage();
        main.setVisible(true);
    }

    private void enterEmpCodeActionPerformed(ActionEvent evt) {
    }

    private void setEmpCodeActionPerformed(ActionEvent evt) {
        empCode = enterEmpCode.getText();
        System.out.println("empcode " + empCode);
        try {
            System.out.println("in trynow ");
            HttpResponse<JsonNode> request = Unirest.get(prefs.get("BASE_URL", "") + "/fingerprintCore/fingerprint/" + empCode).header("Content-Type", "application/json").asJson();

            if (((JsonNode) request.getBody()).getObject().getBoolean("success")) {
                String image_hex = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("image_hex");
                System.out.println("image hex from server " + image_hex);

                image_hex_code = image_hex;
                enterEmpCode.setEnabled(false);

                prefs.put("empCode", empCode);

                System.out.println("image hex " + image_hex_code);

                username = (((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("first_name") + " " + ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("last_name") + " Code:" + ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("employeeCode"));

                welcomeMessage.setText("Welcome " + username);

            } else {

                System.out.println(((JsonNode) request.getBody()).getObject().getString("message"));

                Configs.notifyUser(this, "User doesnt exist on server!");

                dispose();
                IntroPage main = new IntroPage();
                main.setVisible(true);
            }
        } catch (UnirestException ex) {
            Configs.notifyUser(this, "Check server connection!");
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void serverSetIpButtonActionPerformed(ActionEvent evt) {
        if (serverTextField.getText().isEmpty()) {
            errorMessageServer.setText("Please enter server IP or contact Supervisor");
            errorMessageServer.setForeground(Color.red);
        } else {
            prefs.get("BASE_URL", "");
            prefs.put("BASE_URL", "http://" + serverTextField.getText().toString() + ":3000");
            System.out.println("ip address " + prefs.get("BASE_URL", ""));
            errorMessageServer.setText("Successfully set IP");
            errorMessageServer.setForeground(Color.green);
        }
    }

    public static void main(String[] args) {
    }

    public void start() {
        timer.schedule(new TimerTask() {

            public void run() {
                fpsmessage();
            }
        }, 0L, 100L);
    }

    private void fpsmessage() {
        int fpsmsg = fpLibrary.INSTANCE.GetWorkMsg();
        int retmsg = fpLibrary.INSTANCE.GetRetMsg();

        switch (fpsmsg) {
            case 1:
                statusMessage.setText("Please Connect Device");
                break;
            case 3:
                statusMessage.setText("Please Lift Finger");
                break;
            case 2:
                statusMessage.setText("Please Place Finger");
                break;
            case 5:
                if (retmsg == 1) {
                    fpLibrary.INSTANCE.GetFpCharByGen(matbuf, matsize);

                    int ret = fpLibrary.INSTANCE.MatchTemplateOne(matbuf, fingerPrintValueByte, fingerPrintValueByteSize);
                    System.out.println("Match Val : " + String.valueOf(ret));

                    if (ret < 50) {
                        statusMessage.setText("Finger print doesnt exist");
                    } else {
                        statusMessage.setText("Finger match found!");

                        checkIfComputerHasUserLoggedInToday();
                    }
                } else {
                    statusMessage.setText("Capture Fail");
                }
                break;
            case 7:
                configs.DrawImage(fingerprintimage, img);

                System.out.println("returned image for drawing " + String.valueOf(img));
                break;
            case 8:
                statusMessage.setText("Time Out");
        }

    }

    private JLabel fingerprintimage;

    private JPanel fingerprintscan;

    private JPanel jPanel1;

    private JPanel jPanel2;

    private void getFingerprintFromServer(String employeeCode) {
        try {
            HttpResponse<JsonNode> request
                    = Unirest.get(prefs.get("BASE_URL", "")
                            + "/fingerprintCore/fingerprint/" + employeeCode)
                            .header("Content-Type", "application/json").asJson();

            if (((JsonNode) request.getBody()).getObject().getBoolean("success")) {
                String image_hex = ((JsonNode) request.getBody())
                        .getObject().getJSONObject("content").getString("image_hex");
                System.out.println("image hex from server " + image_hex);
                fingerPrintValueByte = Hex.decodeHex(image_hex.toCharArray());
                System.out.println("image byte after conversion server " + fingerPrintValueByte);
                usernameStored = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("first_name");
                lastnameStored = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("last_name");
                isSupervisor = Boolean.valueOf(((JsonNode) request.getBody()).getObject().getJSONObject("content").getBoolean("isSuperVisor"));
                prefs.get(Configs.isSupervisor, "");
                if (isSupervisor.booleanValue()) {
                    prefs.put(Configs.isSupervisor, "yes");
                } else {
                    prefs.put(Configs.isSupervisor, "no");
                }
                prefs.put(Configs.username, usernameStored);
                prefs.put(Configs.lastname, lastnameStored);
                prefs.put("empBranch", ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("emp_branch"));

                start();
            } else {
                Configs.notifyUser(this, "User doesnt exist on server!");
                prefs.put("empCode", "");
                dispose();
                IntroPage main = new IntroPage();
                main.setVisible(true);
            }
        } catch (UnirestException ex) {
            Configs.notifyUser(this, "Check server connection!");
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
            Configs.notifyUser(this, "User fingerprint doesnt exist!");
            prefs.put("empCode", "");
            dispose();
            IntroPage main = new IntroPage();
            main.setVisible(true);
        } catch (JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DecoderException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendLogInTime() {
        try {
            HttpResponse<JsonNode> sessionTimeRenew = Unirest.post(prefs.get("BASE_URL", "")
                    + "/fingerprintCore/timeAtWork")
                    .field("first_name", usernameStored)
                    .field("last_name", lastnameStored)
                    .field("empCode", empCode)
                    .field("loginTime", configs.timeLoggedIn())
                    .field("logoutTime", 0)
                    .field("isLoggedIn", Boolean.valueOf(true))
                    .field("emp_branch", prefs.get("empBranch", ""))
                    .field("isSuperVisor", isSupervisor).asJson();

            
            if (((JsonNode) sessionTimeRenew.getBody()).getObject().getBoolean("success")) {
                postUserDetails();
            } else {
                Configs.notifyUser(this, "Failed to login.Contact Admin");
            }

            System.out.print("Session from server " + ((JsonNode) sessionTimeRenew.getBody()).getObject() + " " + prefs.get("BASE_URL", ""));
        } catch (UnirestException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getUserLoginDetails() {
        try {
            HttpResponse<JsonNode> getData = Unirest.get(prefs.get("BASE_URL", "") + "/fingerprintCore/userLoginDetails/" + empCode).asJson();

            if (((JsonNode) getData.getBody()).getObject().getBoolean("success")) {
                if (((JsonNode) getData.getBody()).getObject().getJSONObject("content").getBoolean("isLoggedIn")) {

                    Configs.notifyUser(this, "This user is already logged in to another machine!");
                } else {
                    prefs.put("isLoggedIn", "yes");

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    System.out.println(dateFormat.format(date) + " " + String.valueOf(dateFormat.format(date)));
                    prefs.put("dateLoggedIn", dateFormat.format(date));
                    prefs.put("dateLoggedInWithEmpcode", dateFormat.format(date) + ":" + empCode);
                    System.out.println(prefs.get("dateLoggedIn", "") + " ====" + prefs.get("dateLoggedInWithEmpcode", ""));

                    sendLogInTime();
                }
            } else {
                Configs.notifyUser(this, "failed to login");
            }
        } catch (UnirestException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void postUserDetails() {
        try {
            HttpResponse<JsonNode> postData = Unirest.post(prefs.get("BASE_URL", "") 
                    + "/fingerprintCore/userLoginDetails")
                    .field("empCode", empCode)
                    .field("isLoggedIn", Boolean.valueOf(true)).asJson();

            if (((JsonNode) postData.getBody()).getObject().getBoolean("success")) {
                goToWelcomepage();
            } else {
                Configs.notifyUser(this, "Failed to login.Contact Admin");
            }
        } catch (UnirestException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setUserFalseLoggedIn() {
        try {
            HttpResponse<JsonNode> postData = Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/userLoginDetails").field("empCode", empCode).field("isLoggedIn", Boolean.valueOf(false)).asJson();
            HttpResponse<JsonNode> sessionTimeRenew = Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/timeAtWork").field("first_name", usernameStored).field("last_name", lastnameStored).field("empCode", empCode).field("loginTime", configs.timeLoggedIn()).field("isLoggedIn", Boolean.valueOf(false)).field("emp_branch", prefs.get("empBranch", "")).asJson();
            if (((JsonNode) postData.getBody()).getObject().getBoolean("success")) {
                start();
                System.out.println("sending loggin as false");
                getFingerprintFromServer(empCode);
                fpLibrary.INSTANCE.GenFpChar();
            } else {
                Configs.notifyUser(this, "Failed to login.Contact Admin");
            }
        } catch (UnirestException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
            Configs.notifyUser(this, "Check server connection");
        } catch (JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkIfComputerHasUserLoggedInToday() {
        sendLoginAgainTime();
//        if ((dateLoggedIn == null) || (dateLoggedIn.isEmpty())) {
//
//            getUserLoginDetails();
//            System.out.println("*** comp has no prefs ***");
//        } else {
//            try {
//                System.out.println(prefs.get("dateLoggedIn", ""));
//                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Date date = new Date();
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                Date savedDate = sdf.parse(prefs.get("dateLoggedIn", ""));
//                Date dateToday = sdf.parse(dateFormat.format(date));
//
//                System.out.println(sdf.format(savedDate));
//                System.out.println(sdf.format(dateToday));
//
//                if (savedDate.compareTo(dateToday) == 0) {
//                    System.out.println("*** date is today ***");
//                    System.out.println("Date1 is equal to Date2");
//                    String userWantingToLogin = dateFormat.format(date) + ":" + empCode;
//                    String userAlreadyLoggedIn = prefs.get("dateLoggedInWithEmpcode", "");
//                    System.out.println(userAlreadyLoggedIn + " " + userWantingToLogin + " " + prefs.get("dateLoggedIn", "") + " ====" + prefs.get("dateLoggedInWithEmpcode", ""));
//
//                    if (userAlreadyLoggedIn.equalsIgnoreCase(userWantingToLogin)) {
//                        sendLoginAgainTime();
//                    } else {
//                        Configs.notifyUser(this, "Computer already being used by someone");
//                    }
//                } else {
//                    getUserLoginDetails();
//                }
//            } catch (java.text.ParseException ex) {
//                Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    private void sendLoginAgainTime() {
        try {
            HttpResponse<JsonNode> sessionTimeRenew = 
                    Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/timeAtWork")
                            .field("first_name", usernameStored)
                            .field("last_name", lastnameStored)
                            .field("empCode", empCode)
                            .field("loginTime", configs.timeLoggedIn())
                            .field("logoutTime", 0)
                            .field("isLoggedIn", Boolean.valueOf(true))
                            .field("emp_branch", prefs.get("empBranch", ""))
                            .field("isSuperVisor", isSupervisor).asJson();

             Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/userLoginDetails")
                     .field("empCode", empCode)
                     .field("isLoggedIn", Boolean.valueOf(true)).asJson();

             System.out.println((JsonNode) sessionTimeRenew.getBody());
            if (((JsonNode) sessionTimeRenew.getBody()).getObject().getBoolean("success")) {
                goToWelcomepage();
            } else {
                Configs.notifyUser(this, "Failed to login.Contact Admin");
            }
        } catch (UnirestException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void goToWelcomepage() {
        timer.cancel();
        performaltTab();
        dispose();
        HomeUI welcome = new HomeUI();
        welcome.setVisible(true);
    }

    private void performaltTab() {
        Timer wait = new Timer();
        wait.schedule(new TimerTask() {
            public void run() {
                try {
                    Runtime.getRuntime().exec("D:\\alt.bat", null, null);
                } catch (IOException ex) {
                    Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 3000L);
    }

    private JPanel jPanel3;

    private JPanel jPanel4;

    private JButton resetEmpCode;

    private JButton scanfinger;

    private JButton serverSetIpButton;

    private JTextField serverTextField;

    private JButton setEmpCode;

    private JLabel statusMessage;

    private JLabel welcomeMessage;

    public int OpenDevice(int comnum, int nbaud, int style) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int LinkDevice() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int CloseDevice() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int DevicePrint(byte[] buffer, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int GetImage(byte[] imagedata, int[] size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void GenFpChar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void EnrolFpChar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int GetWorkMsg() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int GetRetMsg() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int GetFpCharByGen(byte[] tpbuf, int[] tpsize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int GetFpCharByEnl(byte[] fpbuf, int[] fpsize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int ChangeTemplateType(int type, byte[] input, byte[] output) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int MatchTemplateOne(byte[] pSrcData, byte[] pDstData, int nDstSize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int MatchTemplate(byte[] pSrcData, byte[] pDstData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void Handler(String libname, Class interfaceClass, Map options) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public NativeLibrary getNativeLibrary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getLibraryName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Class getInterfaceClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object invoke(Object proxy, Method method, Object[] inArgs) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
