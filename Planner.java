import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.*;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.util.Properties;

import java.sql.*;

public class Planner extends JFrame implements Runnable
{
    private String state = "month";

    public Planner()
    { super("EZY-L Calendar"); }

    public JPanel makeHeader()
    {
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(1,5));
        JButton month = new JButton("Month");
        JButton week = new JButton("Week");
        JButton day = new JButton("Day");
        JButton newTask = new JButton("New Task");
        JButton logout = new JButton("Logout");
        month.addActionListener(e -> { state = "month"; System.out.println(state); repaint(); });
        week.addActionListener(e -> { state = "week"; System.out.println(state); repaint(); });
        day.addActionListener(e -> { state = "day"; System.out.println(state); repaint(); });
        logout.addActionListener(e -> { dispose(); login.main(new String[0]); });
        header.add(month);
        header.add(week);
        header.add(day);
        header.add(newTask);
        header.add(logout);
        return header;
    }

    public JPanel makeCal()
    {
        JPanel Cal = new JPanel();
        Cal.setLayout(new GridLayout(5,7));
        for(int i = 1; i < tellDay.tellDay(1,5,2015); i++)
        {
            JPanel temp = new JPanel();
            temp.setBorder(new BevelBorder(RAISED));
            Cal.add(new JPanel());
        }
        for(int i = 1; i <= 31; i++)
        {
            JPanel temp = new JPanel();
            temp.add(new JLabel(Integer.toString(i)));
            temp.setBorder(new BevelBorder(RAISED));
            Cal.add(temp);
        }
        for(int i = 1; i < tellDay.tellDay(31,5,2015); i++)
        {
            JPanel temp = new JPanel();
            temp.setBorder(new BevelBorder(RAISED));
            Cal.add(new JPanel());
        }
        return Cal;
    }

    public void run()
    {
        setSize(950,750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(makeHeader(), BorderLayout.NORTH);
        cp.add(makeCal(), BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args)
    { javax.swing.SwingUtilities.invokeLater(new Planner()); }
}
