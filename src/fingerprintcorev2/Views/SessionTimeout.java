package fingerprintcorev2.Views;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Date;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SessionTimeout
        extends JFrame {

    private final Timer altTimer = new Timer();
    private final Timer loggedOutDurationTimer = new Timer();

    public SessionTimeout() {
        setUndecorated(true);
        setExtendedState(6);
        initComponents();
        setAlwaysOnTop(true);
        setDefaultCloseOperation(0);

        loggedOutDuration();
        altTimer.schedule(new TimerTask() {

            public void run() {
                SessionTimeout.this.tabLayout();
            }
        }, 5000L);
    }

    private JButton jButton1;

    /**
     * Times the duration time and sends an update to the server.
     */
    private void loggedOutDuration() {
        loggedOutDurationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Preferences prefs = Preferences.userRoot().node("FingerprintClass");
                Long currentTimeInMills = new Date().getTime();
                NetworkCalls.sendLogoutAlert(prefs, currentTimeInMills);
            }

        }, 9000L);
    }

    private void tabLayout() {
        try {
            Runtime.getRuntime().exec("D:\\alt.bat", null, null);
        } catch (IOException ex) {
            Logger.getLogger(IntroPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JLabel jLabel1;

    private JPanel jPanel1;

    private void initComponents() {
        jPanel1 = new JPanel();
        jButton1 = new JButton();
        jLabel1 = new JLabel();

        setDefaultCloseOperation(3);

        jPanel1.setBorder(BorderFactory.createEtchedBorder());

        jButton1.setText("Scan fingerprint");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SessionTimeout.this.jButton1ActionPerformed(evt);
            }

        });
        jLabel1.setFont(new Font("Tahoma", 0, 18));
        jLabel1.setForeground(new Color(255, 0, 0));
        jLabel1.setHorizontalAlignment(0);
        jLabel1.setText("Session timed out, Please rescan fingerprint");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(177, 177, 177).addComponent(jButton1, -2, 334, -2).addContainerGap(178, 32767)).addComponent(jLabel1, GroupLayout.Alignment.TRAILING, -1, -1, 32767));

        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(44, 44, 44).addComponent(jLabel1).addGap(39, 39, 39).addComponent(jButton1, -1, 39, 32767).addContainerGap()));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jPanel1, -1, -1, 32767).addContainerGap()));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jPanel1, -2, -1, -2).addContainerGap(-1, 32767)));

        pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        loggedOutDurationTimer.cancel();
        dispose();
        RescanUI login = new RescanUI();
        login.setVisible(true);
    }

    public synchronized void addWindowListener(WindowListener l) {
        super.addWindowListener(l);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

}
