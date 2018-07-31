package fingerprintcorev2.Views;

import java.awt.EventQueue;
import javax.swing.GroupLayout;

public class SplashScreenLayout extends javax.swing.JFrame {

    public SplashScreenLayout() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(3);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 617, 32767));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 461, 32767));

        pack();
    }

    private static void sleepThread() {
        try {
            Thread.sleep(5000L);

        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        }
    }

    public static void main(String[] args) {
        sleepThread();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SplashScreenLayout().setVisible(true);
            }
        });
    }
}
