import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Container;
import java.awt.GridLayout;

public class loading extends JFrame implements Runnable
{
    public loading() { super("Please Wait"); }

    public void run()
    {
        setSize(250,75);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();
        JLabel loading = new JLabel("                      loading...");
        cp.add(loading);
        setVisible(true);
    }

    public static void main(String[] args)
    { javax.swing.SwingUtilities.invokeLater(new loading()); }
}
