import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.time.LocalDate;
import java.time.Month;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.TextStyle;

import java.util.Properties;
import java.util.Locale;
import java.util.ArrayList;

import java.sql.*;

public class Planner extends JFrame implements Runnable
{
    private LocalDate today = LocalDate.now(ZoneId.systemDefault());
    private Border raisedBevel = BorderFactory.createRaisedBevelBorder();
    private Border line = BorderFactory.createLineBorder(Color.black);
    private int id;

    public Planner(int _id)
    { super("EZY-L Calendar"); id=_id; }

    public JPanel makeHeaderButtons(Container x)
    {
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(1,5));
        JButton month = new JButton("Month");
        JButton week = new JButton("Week");
        JButton day = new JButton("Day");
        JButton newTask = new JButton("New Task");
        JButton logout = new JButton("Logout");
        month.addActionListener(e -> { repaint(); });
        week.addActionListener(e -> { x.removeAll(); x.add(makeHeaderButtons(x),BorderLayout.NORTH); x.add(makeWeek(today),BorderLayout.CENTER); repaint(); });
        day.addActionListener(e -> { repaint(); });
        logout.addActionListener(e -> { dispose(); login.main(new String[0]); });
        header.add(month);
        header.add(week);
        header.add(day);
        header.add(newTask);
        header.add(logout);

        return header;
    }

    public JPanel weekHeader()
    {
        JPanel weekHeader = new JPanel();
        weekHeader.setLayout(new GridLayout(1,7));
        JPanel sunday = new JPanel();
        sunday.setBorder(line);
        sunday.add(new JLabel("Sunday"));
        weekHeader.add(sunday);
        JPanel monday = new JPanel();
        monday.setBorder(line);
        monday.add(new JLabel("Monday"));
        weekHeader.add(monday);
        JPanel tuesday = new JPanel();
        tuesday.setBorder(line);
        tuesday.add(new JLabel("Tuesday"));
        weekHeader.add(tuesday);
        JPanel wednesday = new JPanel();
        wednesday.setBorder(line);
        wednesday.add(new JLabel("Wednesday"));
        weekHeader.add(wednesday);
        JPanel thursday = new JPanel();
        thursday.setBorder(line);
        thursday.add(new JLabel("Thursday"));
        weekHeader.add(thursday);
        JPanel friday = new JPanel();
        friday.setBorder(line);
        friday.add(new JLabel("Friday"));
        weekHeader.add(friday);
        JPanel saturday = new JPanel();
        saturday.setBorder(line);
        saturday.add(new JLabel("Saturday"));
        weekHeader.add(saturday);
        return weekHeader;
    }

    public JPanel makeHeader(LocalDate x)
    {
        JPanel monthHeader = new JPanel();
        monthHeader.setLayout(new GridLayout(2,1));
        JPanel monthName = new JPanel();
        monthName.add(new JLabel(today.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)),SwingConstants.CENTER);
        JPanel weekHeader = weekHeader();
        monthName.setBorder(line);
        weekHeader.setBorder(line);
        monthHeader.add(monthName);
        monthHeader.add(weekHeader);
        return monthHeader;
    }

    public JPanel makeMonth(LocalDate x)
    {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        
        JPanel month = new JPanel();
        month.setLayout(new GridLayout(5,7));
        for(int i = 1; i < tellDay.tellDay(1,x.getMonthValue(),x.getYear()); i++)
        {
            JPanel temp = new JPanel();
            temp.setBorder(line);
            temp.setBackground(new Color(255,255,255));
            month.add(new JPanel());
        }
        ArrayList<Tile> tiles = new ArrayList();
        for(LocalDate i = LocalDate.of(x.getYear(), x.getMonth(), 1); i.compareTo(LocalDate.of(x.getYear(), x.getMonth(), x.getMonth().maxLength())) <= 0; i = i.plusDays(1))
        {
            Tile tempTile = new Tile(i);
            if(i.compareTo(x) == 0)
            { tempTile.setSelected(true); }
            tiles.add(tempTile);
            month.add(tempTile);
        }
        for (int i = 0; i < tiles.size(); i++)
        { tiles.get(i).addMouseListener(new colorChanger(tiles)); }

        pane.add(makeHeader(x),BorderLayout.NORTH);
        pane.add(month,BorderLayout.CENTER);
        return pane;
    }

    public JPanel makeWeek(LocalDate x)
    {
        JPanel week = new JPanel();
        week.setLayout(new BorderLayout());

        JPanel days = new JPanel();
        days.setLayout(new GridLayout(1,7));
        for(long i = x.getDayOfWeek().getValue(); i > 0; i--)
        { days.add(new Tile(x.minusDays(i))); }
        days.add(new Tile(x));
        for(long i = x.getDayOfWeek().getValue(); i < 6; i++)
        { days.add(new Tile(x.plusDays(i))); }

        week.add(makeHeader(x),BorderLayout.NORTH);
        week.add(days,BorderLayout.CENTER);
        return week;
    }

    public void run()
    {
        setSize(950,750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(makeHeaderButtons(cp), BorderLayout.NORTH);
        cp.add(makeMonth(today), BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args)
    { javax.swing.SwingUtilities.invokeLater(new Planner(1)); }

    class colorChanger extends Tile implements MouseListener
    {
        private ArrayList<Tile> tiles;

        public colorChanger(ArrayList _tiles)
        { tiles = _tiles; }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}

        public void mouseClicked(MouseEvent e)
        {
            Tile tile = (Tile) e.getSource();
            if (tile.getSelect())
            {
                tile.setBackground(new Color(255,255,255));
                tile.setSelected(false);
            }
            else
            {
                tile.setSelected(true);
                tile.setBackground(new Color(64,188,237));
                for(int i = 0; i < tiles.size(); i++)
                {
                    if(!tile.equals(tiles.get(i)))
                    { tiles.get(i).setSelected(false); }
                }
            }
        }
    
        public void mouseEntered(MouseEvent e)
        {
            Tile tile = (Tile) e.getSource();
            if (!tile.getSelect())
            { tile.setBackground(new Color(111,207,245)); }
        }

        public void mouseExited(MouseEvent e)
        {
            Tile tile = (Tile) e.getSource();
            if (!tile.getSelect())
            { tile.setBackground(new Color(255,255,255)); }
        }
    }
}
