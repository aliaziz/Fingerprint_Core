package fingerprintcorev2.Views;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import fingerprintcorev2.Configs.Configs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import org.json.JSONException;

public class IntroPage extends javax.swing.JFrame {

    String supervisorCode;
    String employeeCode;
    java.awt.Image img;
    private static Preferences prefs;
    private final Timer timer = new Timer();
    byte[] fingerPrintValueByte = new byte[100];
    byte[] fingerPrintValueByteEmpty = new byte[100];
    int fingerPrintValueByteSize;
    int fingerPrintValueByteSizeEmpty;
    private byte[] matbuf = new byte[100];
    private int[] matsize = new int[1];
    Configs configs = new Configs();
    int userinitCount;
    int userinitCountHolder;

    public IntroPage() {
        setUndecorated(true);
        setExtendedState(6);
        initComponents();
        setAlwaysOnTop(true);
        setDefaultCloseOperation(0);

        exitButton.setMnemonic(69);
        prefs = Preferences.userRoot().node("FingerprintClass");
        Configs.runShutDownService(employeeCode);
        userinitCount = prefs.getInt("userinitCout", userinitCountHolder);
        System.out.println(userinitCount);
        if (userinitCount == 0) {
            try {
                Runtime.getRuntime().exec("C:\\Windows\\system32\\userinit.exe", null, new java.io.File("C:\\Windows\\system32\\"));
            } catch (IOException ex) {
                Logger.getLogger(IntroPage.class.getName()).log(Level.SEVERE, null, ex);
            }
            prefs.putInt("userinitCout", 1);
        }

        int r = fpLibrary.INSTANCE.OpenDevice(0, 0, 0);

        if (r == 1) {
            if (fpLibrary.INSTANCE.LinkDevice() == 1) {
                fingerprint.setText("....");
            } else {
                fingerprint.setText("Link Device Fail");
            }
        } else {
            fingerprint.setText("Failed to connect to device");
        }
    }

    private JButton absent;

    private JButton exitButton;

    private JLabel fingerprint;

    private void initComponents() {
        jPanel1 = new JPanel();
        login = new JButton();
        absent = new JButton();
        jPanel2 = new JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        supervisorcomment = new JTextArea();
        submitComment = new JButton();
        fingerprint = new JLabel();
        exitButton = new JButton();

        setDefaultCloseOperation(3);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Welcome", 0, 0, null, new java.awt.Color(0, 51, 255)));

        login.setText("Login ");
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                IntroPage.this.loginActionPerformed(evt);
            }

        });
        absent.setText("Absent");
        absent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                IntroPage.this.absentActionPerformed(evt);
            }

        });
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Supervisor Comment", 0, 0, null, new java.awt.Color(0, 51, 255)));

        supervisorcomment.setColumns(20);
        supervisorcomment.setRows(5);
        supervisorcomment.setToolTipText("Only field in if you are admin");
        supervisorcomment.setEnabled(false);
        jScrollPane1.setViewportView(supervisorcomment);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPane1));

        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addGap(0, 11, 32767).addComponent(jScrollPane1, -2, -1, -2)));

        submitComment.setText("Submit Comment");
        submitComment.setEnabled(false);
        submitComment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                IntroPage.this.submitCommentActionPerformed(evt);
            }

        });
        fingerprint.setFont(new java.awt.Font("Myriad Hebrew", 0, 12));
        fingerprint.setHorizontalAlignment(0);

        exitButton.setPreferredSize(new java.awt.Dimension(1, 1));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                IntroPage.this.exitButtonActionPerformed(evt);
            }

        });
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(login, -1, -1, 32767).addComponent(absent, GroupLayout.Alignment.TRAILING, -1, -1, 32767).addComponent(jPanel2, -1, -1, 32767).addComponent(submitComment, -1, -1, 32767).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGap(0, 268, 32767).addComponent(fingerprint, -2, 289, -2).addGap(94, 94, 94).addComponent(exitButton, -2, -1, -2))).addContainerGap()));

        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(login, -2, 43, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(absent, -2, 43, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel2, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(submitComment, -2, 34, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(fingerprint, -1, 103, 32767).addContainerGap()).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGap(0, 0, 32767).addComponent(exitButton, -2, -1, -2)))));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jPanel1, -1, -1, 32767).addContainerGap()));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jPanel1, -1, -1, 32767).addContainerGap()));

        pack();
    }

    private void absentActionPerformed(ActionEvent evt) {
        superVisorDialog();
    }

    private void superVisorDialog() {
        supervisorCode = JOptionPane.showInputDialog(this, "Supervisor Code", "Please enter Supervisor Code");

        if (supervisorCode.isEmpty()) {
            Configs.notifyUser(this, "Please enter supervisor code!!!");
        } else {
            fingerPrintValueByteSize = 512;

            String code = JOptionPane.showInputDialog(this, "Supervisor PIN", "Enter Supervisor PIN", 2);

            if (code.equalsIgnoreCase("12345")) {
                executeSupervisorCheck(supervisorCode);
            } else {
                Configs.notifyUser(this, "Wrong supervisor password");
            }
        }
    }

    private void submitCommentActionPerformed(ActionEvent evt) {
        if (supervisorcomment.getText().isEmpty()) {
            Configs.notifyUser(this, "Please enter comment");
        } else {
            employeeCode = JOptionPane.showInputDialog(this, "Employee Code", "Please enter employee code");

            if (employeeCode.isEmpty()) {
                Configs.notifyUser(this, "Please enter employee code");
            } else {
                String comment = supervisorcomment.getText().toString();
                submitComment(comment, employeeCode, supervisorCode);

                supervisorcomment.setEnabled(false);
                submitComment.setEnabled(false);
            }
        }
    }

    private void executeSupervisorCheck(String supervisorCode) {
        try {
            HttpResponse<JsonNode> request = com.mashape.unirest.http.Unirest.get(prefs.get("BASE_URL", "") + "/fingerprintCore/fingerprint/" + supervisorCode).header("Content-Type", "application/json").asJson();

            if (((JsonNode) request.getBody()).getObject().getBoolean("success")) {
                try {
                    Boolean isSuperVisor = Boolean.valueOf(((JsonNode) request.getBody()).getObject().getJSONObject("content").getBoolean("isSuperVisor"));
                    if (isSuperVisor.booleanValue()) {
                        String image_hex = ((JsonNode) request.getBody()).getObject().getJSONObject("content").getString("image_hex");
                        System.out.println("image hex from server " + image_hex);
                        fingerPrintValueByte = Configs.hexStringToByteArray(image_hex);
                        System.out.println("image byte after conversion server " + fingerPrintValueByte);

                        start();
                        fpLibrary.INSTANCE.GenFpChar();
                    } else {
                        Configs.notifyUser(this, "You donot have admin rights!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Configs.notifyUser(this, "You donot have admin rights!");
                }

            } else {
                Configs.notifyUser(this, "Supervisor doesnt exist on server!");
            }
        } catch (UnirestException ex) {
            Configs.notifyUser(this, "Check server connection!");
            timer.cancel();
            Logger.getLogger(RescanUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(IntroPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loginActionPerformed(ActionEvent evt) {
        goToLoginPage();
    }

    private void exitButtonActionPerformed(ActionEvent evt) {
        String password = JOptionPane.showInputDialog(this, "Enter Admin passcode", "Enter passcode");
        if (!"K!nG5B3tt".equals(password)) {
            Configs.notifyUser(this, "Enter valid passcode");
        } else {
            System.exit(0);
        }
    }

    public void start() {
        timer.schedule(new java.util.TimerTask() {

            public void run() {
                IntroPage.this.fpsmessage();
            }
        }, 0L, 100L);
    }

    private void fpsmessage() {
        int fpsmsg = fpLibrary.INSTANCE.GetWorkMsg();
        int retmsg = fpLibrary.INSTANCE.GetRetMsg();

        switch (fpsmsg) {
            case 1:
                fingerprint.setText("Please Open Device");
                break;
            case 3:
                fingerprint.setText("Please Lift Finger");
                break;
            case 2:
                fingerprint.setText("Enter Supervisor fingerprint");
                break;
            case 5:
                if (retmsg == 1) {
                    fpLibrary.INSTANCE.GetFpCharByGen(matbuf, matsize);

                    int ret = fpLibrary.INSTANCE.MatchTemplateOne(matbuf, fingerPrintValueByte, fingerPrintValueByteSize);
                    System.out.println("Match Val : " + String.valueOf(ret));

                    if (ret < 100) {
                        Configs.notifyUser(this, "Match not found!");
                    } else if ((ret <= 150) && (ret >= 100)) {
                        fpLibrary.INSTANCE.GenFpChar();
                    } else if (ret > 150) {
                        supervisorcomment.setEnabled(true);
                        submitComment.setEnabled(true);
                    }
                } else {
                    Configs.notifyUser(this, "Capture Fail");
                }
                break;
            case 7:
                configs.DrawImage(fingerprint, img);

                System.out.println("returned image for drawing " + String.valueOf(img));
                break;
            case 8:
                Configs.notifyUser(this, "Time Out");
        }
    }

    private void submitComment(String comment, String empCode, String supervisorCode) {
        try {
            HttpResponse<JsonNode> submitCom = com.mashape.unirest.http.Unirest.post(prefs.get("BASE_URL", "") + "/fingerprintCore/submitSupervisorComment").field("supervisorCode", supervisorCode).field("employeeCode", empCode).field("comment", comment).asJson();

            System.out.println("from comment server " + submitCom.getBody());
            if (((JsonNode) submitCom.getBody()).getObject().getBoolean("error")) {
                Configs.notifyUser(this, "Failed to submit comment!");
                submitComment.setEnabled(false);
                supervisorcomment.setEnabled(false);
                timer.cancel();
            } else {
                Configs.notifyUser(this, "Comment submitted!");
                submitComment.setEnabled(false);
                supervisorcomment.setEnabled(false);
                timer.cancel();
            }
        } catch (UnirestException ex ) {
            Logger.getLogger(IntroPage.class.getName()).log(Level.SEVERE, null, ex);
            Configs.notifyUser(this, "Please connect to internet");
        } catch (JSONException ex) {
            Logger.getLogger(IntroPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void goToLoginPage() {
        dispose();
        RescanUI login = new RescanUI();
        login.setVisible(true);
    }

    private JPanel jPanel1;

    private JPanel jPanel2;

    private javax.swing.JScrollPane jScrollPane1;
    private JButton login;
    private JButton submitComment;
    private JTextArea supervisorcomment;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IntroPage().setVisible(true);
            }
        });
    }
}
