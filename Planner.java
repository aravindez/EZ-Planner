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
    private int userid;
    private int calid;

    private JPanel descPane = new JPanel();

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "avihome";

    public Planner(int _userid, int _calid)
    { super("EZY-L Calendar"); userid=_userid; calid=_calid;}

    public JPanel makeHeaderButtons(Container x)
    {
        JPanel header = new JPanel();
       header.setLayout(new GridLayout(1,5));
        JButton month = new JButton("Month");
        JButton week = new JButton("Week");
        JButton day = new JButton("Day");
        JButton newTask = new JButton("New Task");
        JButton logout = new JButton("Logout");
        month.addActionListener(e -> { x.removeAll(); x.add(makeHeaderButtons(x),BorderLayout.NORTH); descPane.removeAll(); x.add(makeSideBar(),BorderLayout.WEST); x.add(makeMonth(today),BorderLayout.CENTER); setVisible(true); repaint(); });
        week.addActionListener(e -> { x.removeAll(); x.add(makeHeaderButtons(x),BorderLayout.NORTH); descPane.removeAll(); x.add(makeSideBar(),BorderLayout.WEST); x.add(makeWeek(today),BorderLayout.CENTER); setVisible(true); repaint(); });
        day.addActionListener(e -> { x.removeAll(); x.add(makeHeaderButtons(x),BorderLayout.NORTH); descPane.removeAll(); x.add(makeSideBar(),BorderLayout.WEST); x.add(makeDay(today),BorderLayout.CENTER); setVisible(true); repaint(); });
        logout.addActionListener(e -> { dispose(); login.main(new String[0]); });
        header.add(month);
        header.add(week);
        header.add(day);
        header.add(newTask);
        header.add(logout);

        return header;
    }

    public JPanel makeSideBar()
    {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(line);
        descPane.setLayout(new GridLayout(1,1));
        descPane.setBorder(line);

        ArrayList<Calendar> calList = new ArrayList<Calendar>();
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);

            stmt = conn.createStatement();
            String valid = String.format("SELECT id FROM calendar AS c INNER JOIN user_calendar AS uc ON uc.calendar_id=c.id WHERE uc.user_id=%s;", Integer.toString(userid));
            ResultSet rs = stmt.executeQuery(valid);

            while(rs.next())
            { calList.add(new Calendar(rs.getInt("id"))); }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(SQLException se) { se.printStackTrace(); }
        catch(Exception e) { e.printStackTrace(); }
        finally{
            try{ if(stmt!=null) { stmt.close(); } }
            catch(SQLException se2) {}
            try{ if(conn!=null) { conn.close(); } }
            catch(SQLException se) { se.printStackTrace(); } }

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(calList.size(),1));
        for(Calendar i : calList)
        {
            JButton temp = new JButton("  "+i.name+"  ");
            temp.addActionListener(e ->
                {
                    descPane.removeAll();
                    descPane.add(new JLabel(i.description));
                    setVisible(true);
                });
            buttons.add(temp);
        }
        sidebar.add(buttons, BorderLayout.NORTH);
        sidebar.add(descPane, BorderLayout.SOUTH);
        return sidebar;
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
        for(int i = 1; i < LocalDate.of(x.getYear(),x.getMonthValue(),1).getDayOfWeek().getValue(); i++)
        {
            JPanel temp = new JPanel();
            temp.setBorder(line);
            temp.setBackground(new Color(255,255,255));
            month.add(new JPanel());
        }
        ArrayList<calTile> tiles = new ArrayList();
        for(LocalDate i = LocalDate.of(x.getYear(), x.getMonth(), 1); i.compareTo(LocalDate.of(x.getYear(), x.getMonth(), x.getMonth().maxLength())) <= 0; i = i.plusDays(1))
        {
            calTile tempTile = new calTile(i, calid);
            if(i.compareTo(x) == 0)
            { tempTile.setSelected(true); }
            tiles.add(tempTile);
            month.add(tempTile);
        }
        for (int i = 0; i < tiles.size(); i++)
        { tiles.get(i).addMouseListener(new calColorChanger1(tiles)); }

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
        ArrayList<dayTile> tiles = new ArrayList();
        for(long i = x.getDayOfWeek().getValue(); i > 0; i--)
        {
            dayTile temp = new dayTile(x.minusDays(i), calid);
            tiles.add(temp);
            days.add(temp);
        }
        dayTile now = new dayTile(x, calid);
        now.setSelected(true);
        tiles.add(now);
        days.add(now);
        for(long i = x.getDayOfWeek().getValue(); i < 6; i++)
        {
            dayTile temp = new dayTile(x.plusDays(i), calid);
            tiles.add(temp);
            days.add(temp);
        }
        for(int i = 0; i < tiles.size(); i++)
        { tiles.get(i).addMouseListener(new calColorChanger2(tiles)); }

        week.add(makeHeader(x),BorderLayout.NORTH);
        week.add(days,BorderLayout.CENTER);
        return week;
    }

    public JPanel makeDay(LocalDate x)
    {
        JPanel day = new JPanel();
        day.setLayout(new BorderLayout());
        JPanel header = new JPanel();
        header.add(new JLabel(x.getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.ENGLISH)));
        day.add(header,BorderLayout.NORTH);
        day.add(new dayTile(x,calid));
        return day;
    }

    public void run()
    {
        setSize(950,700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        //SplashScreen splash = new SplashScreen(10000);
        //splash.showSplashAndExit();
        cp.add(makeHeaderButtons(cp), BorderLayout.NORTH);
        cp.add(makeSideBar(), BorderLayout.WEST);
        cp.add(makeMonth(today), BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args)
    { javax.swing.SwingUtilities.invokeLater(new Planner(1,1)); }

    class calColorChanger1 extends calTile implements MouseListener
    {
        private ArrayList<calTile> tiles;
    
        public calColorChanger1(ArrayList<calTile> _tiles)
        { tiles = _tiles; }
    
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    
        public void mouseClicked(MouseEvent e)
        {
            calTile tile = (calTile) e.getSource();
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
            calTile tile = (calTile) e.getSource();
            if (!tile.getSelect())
            { tile.setBackground(new Color(111,207,245)); }
        }
    
        public void mouseExited(MouseEvent e)
        {
            calTile tile = (calTile) e.getSource();
            if (!tile.getSelect())
            { tile.setBackground(new Color(255,255,255)); }
        }
    }

    class calColorChanger2 extends dayTile implements MouseListener
    {
        private ArrayList<dayTile> tiles;
    
        public calColorChanger2(ArrayList<dayTile> _tiles)
        { tiles = _tiles; }
    
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    
        public void mouseClicked(MouseEvent e)
        {
            dayTile tile = (dayTile) e.getSource();
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
    }
    
    class calPick implements MouseListener
    {
        private Color color;
        private String desc;
        private boolean selected;

        public calPick(Color _color, String _desc)
        { color = _color; desc = _desc; selected = false; }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
            
        public void mouseClicked(MouseEvent e)
        {
            if(selected)
            {
                setForeground(color);
                repaint();
                descPane.removeAll();
                descPane.add(new JLabel(desc));
                descPane.repaint();
            }
            else
            {
                setForeground(new Color(0,0,0));
                repaint();
                descPane.removeAll();
                descPane.repaint();
            }
        }
    
        public void mouseEntered(MouseEvent e)
        {
            setForeground(color);
        }

        public void mouseExited(MouseEvent e)
        {
            setForeground(new Color(0,0,0));
        }
    } 

}
