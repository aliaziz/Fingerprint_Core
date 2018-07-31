package fingerprintcorev2.Views;

import java.awt.Robot;
import javax.swing.JFrame;









public class AltTabStopper
  implements Runnable
{
  private boolean working = true;
  private JFrame frame;
  
  public AltTabStopper(JFrame frame)
  {
    this.frame = frame;
  }
  
  public void stop()
  {
    working = false;
  }
  
  public static AltTabStopper create(JFrame frame)
  {
    AltTabStopper stopper = new AltTabStopper(frame);
    new Thread(stopper, "Alt-Tab Stopper").start();
    return stopper;
  }
  
  public void run()
  {
    try
    {
      Robot robot = new Robot();
      while (working)
      {
        robot.keyRelease(18);
        robot.keyRelease(9);
        frame.requestFocus();
      }
    } catch (Exception e) {
      e.printStackTrace();System.exit(-1);
    }
  }
}
