package fingerprintcorev2.Views;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fingerprintcorev2.Configs.Configs;
import fingerprintcorev2.Configs.Utils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.codec.binary.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import org.apache.commons.codec.DecoderException;
import org.json.JSONException;

public class RescanUI extends javax.swing.JFrame {

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
    private final byte[] matbuf = new byte[255];
    private final int[] matsize = new int[1];
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
    ArrayList<String> imageList = new ArrayList<>();
    private final ArrayList<String> returnedIps = new ArrayList<>();
    private final ArrayList<String> ipNames = new ArrayList<>();
    private ArrayList<String> ipAddresses = new ArrayList<>();
    private final String fileName = "ConfigFingerPrint.txt";
    private ArrayList<String> ipsFromFile = new ArrayList<>();

    Configs configs = new Configs();
    private JButton connect_modem;
    private JTextField enterEmpCode;
   
    public RescanUI() {
//        setUndecorated(true);
//        setExtendedState(6);
        initComponents();
//        setAlwaysOnTop(true);
//        setDefaultCloseOperation(0);

        buttonEnableTimer.schedule(new TimerTask() {

            @Override
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
            serverTextField.setVisible(false);
            serverTextField.setText(baseURL);
        }

        readIps();
        serverSetIpButton.addActionListener((ActionEvent e) -> {
            int ipIndex = serverSetIpButton.getSelectedIndex();
            prefs.get("BASE_URL", "");
            prefs.put(Configs.BASE_URL, "http://" + ((String) ipAddresses.get(ipIndex)).replaceAll(" ", "") + ":3000");
        });

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

            if (fingerPrintValueByte.length < 1) {
                Configs.notifyUser(RescanUI.this, "Employee Doesnt exist");
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

    private void addToComboBox(ArrayList<String> ipNames) {
        for (int x = 0; x < ipNames.size(); x++) {
            String newIp = ipNames.get(x);
            serverSetIpButton.addItem(newIp);
        }
    }

    private void WriteToFile(String ipName, String ipAddress) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                bufferedWriter.newLine();
                bufferedWriter.write(ipName + " : " + ipAddress);
            }
        } catch (IOException e) {
        }
    }

    private ArrayList<String> readFromFile() {
        try {
            File yourFile = new File(fileName);

            if (!yourFile.exists()) {
                yourFile.createNewFile();
            } else if ((yourFile.exists()) && (!yourFile.isDirectory())) {
                try (FileReader reader = new FileReader(fileName)) {
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println("Reads from files " + line);
                        returnedIps.add(line);
                    }
                }
            }
        } catch (IOException e) {
        }
        return returnedIps;
    }

    private void readIps() {
        try {
            ipsFromFile = readFromFile();

            if (ipsFromFile.size() > 0) {

                for (int x = 0; x < ipsFromFile.size(); x++) {
                    if (!((String) ipsFromFile.get(x)).isEmpty()) {
                        System.out.println("file ips " + (String) ipsFromFile.get(x));
                        String[] ipAddSplit = ((String) ipsFromFile.get(x)).split(":");
                        ipNames.add(ipAddSplit[0]);
                        ipAddresses.add(ipAddSplit[1].replaceAll(" ", ""));
                    }
                }

                addToComboBox(ipNames);

            } else {
                String ip
                        = JOptionPane.showInputDialog(this, "Enter default ip", "Default Ip");
                String oneTimeIp = "http://" + ip + ":3000";
                getIpsAndSave(oneTimeIp);
            }

        } catch (Exception e) {
            WriteToFile("", "");
        }
    }

    private void getIpsAndSave(String oneTimeIp) {
        try {
            HttpResponse<JsonNode> request = Unirest.get(oneTimeIp + "/fingerprintCore/getIps")
                    .header("Content-Type", "application/json").asJson();

            if (((JsonNode) request.getBody()).getObject().getBoolean("status")) {

                for (int x = 0; x < ((JsonNode) request.getBody()).getObject().getJSONArray("content").length(); x++) {
                    WriteToFile(((JsonNode) request.getBody()).getObject().getJSONArray("content").getJSONObject(x).getString("serverName"), ((JsonNode) request.getBody()).getObject().getJSONArray("content").getJSONObject(x).getString("serverIp"));
                }
                addIpsToView();
            } else {
                JOptionPane.showMessageDialog(this, "Error, please contact admin");
            }
        } catch (UnirestException ex) {
            JOptionPane.showMessageDialog(this, "Check server connection!");

            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException e) {
        }
    }

    private void addIpsToView() {
        serverSetIpButton.removeAllItems();
        ipNames.clear();
        ipAddresses.clear();
        ArrayList<String> fileRead = readFromFile();
        for (int x = 0; x < fileRead.size(); x++) {
            if (!((String) fileRead.get(x)).isEmpty()) {
                System.out.println("file ips " + (String) fileRead.get(x));
                String[] ipAddSplit = ((String) fileRead.get(x)).split(":");
                ipNames.add(ipAddSplit[0]);
                ipAddresses.add(ipAddSplit[1].replaceAll(" ", ""));
            }
        }

        addToComboBox(ipNames);
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
        serverSetIpButton = new JComboBox();
        errorMessageServer = new JLabel();
        welcomeMessage = new JLabel();

        setDefaultCloseOperation(3);

        jPanel1.setBorder(BorderFactory.createTitledBorder(null, "Login", 0, 0, null, new Color(51, 102, 255)));

        scanfinger.setText("Scan fingerprint");
        scanfinger.setEnabled(false);
        scanfinger.addActionListener((ActionEvent evt) -> {
            scanfingerActionPerformed(evt);
        });
        fingerprintscan.setBorder(BorderFactory.createTitledBorder(""));
        fingerprintscan.setMaximumSize(new java.awt.Dimension(200, 200));

        GroupLayout fingerprintscanLayout = new GroupLayout(fingerprintscan);
        fingerprintscan.setLayout(fingerprintscanLayout);
        fingerprintscanLayout.setHorizontalGroup(fingerprintscanLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fingerprintimage, GroupLayout.Alignment.TRAILING, -1, 274, 32767));

        fingerprintscanLayout.setVerticalGroup(fingerprintscanLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fingerprintimage, GroupLayout.Alignment.TRAILING, -1, 303, 32767));

        jPanel2.setBorder(BorderFactory.createTitledBorder(""));

        exitButton.setPreferredSize(new java.awt.Dimension(1, 1));
        exitButton.addActionListener((ActionEvent evt) -> {
            exitButtonActionPerformed(evt);
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
        connect_modem.addActionListener((ActionEvent evt) -> {
            connect_modemActionPerformed(evt);
        });
        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(jPanel3, -2, -1, -2).addGroup(GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup().addComponent(connect_modem).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(exitButton, -2, 85, -2))).addContainerGap(-1, 32767)));

        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(jPanel3, -2, 50, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(exitButton, -2, -1, -2).addComponent(connect_modem)).addContainerGap()));

        resetEmpCode.setText("Reset Employee Code");
        resetEmpCode.addActionListener((ActionEvent evt) -> {
            resetEmpCodeActionPerformed(evt);
        });
        enterEmpCode.setEnabled(false);

        setEmpCode.setText("Submit Emp Code");
        setEmpCode.addActionListener((ActionEvent evt) -> {
            setEmpCodeActionPerformed(evt);
        });
        jPanel4.setBorder(BorderFactory.createTitledBorder(null, "Server Information", 0, 0, null, Color.blue));

        serverTextField.setToolTipText("Enter Server ip");

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
                    getFingerprintFromServer(empCode);
//                    fpLibrary.INSTANCE.GenFpChar();
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

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
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

                    if (ret < 50) {
                        statusMessage.setText("Fingerprint doesnt match");
                    } else {
                        statusMessage.setText("Finger match found!");

                        checkIfComputerHasUserLoggedInToday();
                    }
                } else {
                    statusMessage.setText("Capture Fail");
                }
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
                Boolean isEnabled = ((JsonNode) request.getBody())
                        .getObject().getJSONObject("content").getBoolean("isEnabled");
                String terminationReason = ((JsonNode) request.getBody())
                        .getObject().getJSONObject("content").getString("terminationReason");
                if (!isEnabled) {
                    JOptionPane.showMessageDialog(this, "Terminated: " + terminationReason);
                } else {
                    decodeFingerprint(image_hex, request);
                }

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
        }
    }

    private void decodeFingerprint(String image_hex, HttpResponse<JsonNode> request) {
        try {
            fingerPrintValueByte = Hex.decodeHex(image_hex.toCharArray());
            usernameStored = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("first_name");
            lastnameStored = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("last_name");
            isSupervisor = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getBoolean("isSuperVisor");
            prefs.get(Configs.isSupervisor, "");
            if (isSupervisor) {
                prefs.put(Configs.isSupervisor, "yes");
            } else {
                prefs.put(Configs.isSupervisor, "no");
            }
            prefs.put(Configs.username, usernameStored);
            prefs.put(Configs.lastname, lastnameStored);
            prefs.put("empBranch", ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("emp_branch"));

            start();
            fpLibrary.INSTANCE.GenFpChar();
        } catch (DecoderException | JSONException ex) {
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendLogInTime() {
        if (NetworkCalls.sendUserLoginTime(prefs, configs.currentSystemTime(), true)) {
            goToWelcomepage();
        } else {
            Configs.notifyUser(this, "Failed to login.Contact Admin");
        }
    }

    private void setUserFalseLoggedIn() {
        if (NetworkCalls.sendUserLoginTime(prefs, configs.currentSystemTime(), false)) {
            start();
            getFingerprintFromServer(empCode);
            fpLibrary.INSTANCE.GenFpChar();
        } else {
            Configs.notifyUser(this, "Failed to login.Contact Admin");
        }
    }

    private void checkIfComputerHasUserLoggedInToday() {
        sendLogInTime();
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
//                        sendLogInTime();
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
            @Override
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

    private JComboBox serverSetIpButton;

    private JTextField serverTextField;

    private JButton setEmpCode;

    private JLabel statusMessage;

    private JLabel welcomeMessage;
}
