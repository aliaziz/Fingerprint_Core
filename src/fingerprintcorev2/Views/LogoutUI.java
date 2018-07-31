package fingerprintcorev2.Views;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LogoutUI
        extends JFrame {

    private static Timer timer = new Timer();

    public LogoutUI() {
        initComponents();
        timer.schedule(new TimerTask() {
            public void run() {
                dispose();
                System.exit(0);
            }
        }, 2500L);
    }

    private JLabel jLabel1;

    private void initComponents() {
        jLabel1 = new JLabel();

        setDefaultCloseOperation(3);

        jLabel1.setFont(new Font("Tahoma", 0, 18));
        jLabel1.setForeground(new Color(255, 0, 0));
        jLabel1.setHorizontalAlignment(0);
        jLabel1.setText("You are logged out!");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(jLabel1, -1, 423, 32767).addContainerGap()));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(51, 51, 51).addComponent(jLabel1).addContainerGap(46, 32767)));

        pack();
    }

    public static void main(String[] args) {
    }
}
