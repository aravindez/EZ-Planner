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
import java.awt.Dimension;

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
    private Calendar cal;
    private Container cp = getContentPane();
    private ArrayList<Task> taskList = new ArrayList<Task>();
    private String state = "month";

    private JPanel descPane = new JPanel();
    private JPanel mainPane = new JPanel();

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "avihome";

    public Planner(int _userid, Calendar _cal)
    { super("EZY-LAL Calendar"); userid=_userid; cal=_cal;}

    public void monthRefresh()
    {
        cp.removeAll();
        cp.add(makeHeaderButtons(cp),BorderLayout.NORTH);
        cp.add(makeSideBar(),BorderLayout.WEST);
        cp.add(makeMonth(today,cal),BorderLayout.CENTER);
        setVisible(true); 
    }

    public void weekRefresh()
    {
        cp.removeAll(); 
        cp.add(makeHeaderButtons(cp),BorderLayout.NORTH); 
        cp.add(makeSideBar(),BorderLayout.WEST);
        cp.add(makeWeek(today),BorderLayout.CENTER);
        setVisible(true);
    }

    public void dayRefresh()
    {
        cp.removeAll();
        cp.add(makeHeaderButtons(cp),BorderLayout.NORTH);
        cp.add(makeSideBar(),BorderLayout.WEST);
        cp.add(makeDay(today),BorderLayout.CENTER);
        setVisible(true); 
    }

    public JPanel makeHeaderButtons(Container x)
    {
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(1,5));
        JButton month = new JButton("Month");
        JButton week = new JButton("Week");
        JButton day = new JButton("Day");
        JButton nTask = new JButton("New Task");
        JButton logout = new JButton("Logout");
        month.addActionListener(e -> { state = "month"; descPane.removeAll(); monthRefresh(); });
        week.addActionListener(e -> { state = "week"; descPane.removeAll(); weekRefresh(); });
        day.addActionListener(e -> { state = "day"; descPane.removeAll(); dayRefresh(); });
        nTask.addActionListener(e -> { dispose(); String[] temp = {Integer.toString(userid),Integer.toString(cal.id)}; newTask.main(temp); });
        logout.addActionListener(e -> { dispose(); login.main(new String[0]);});
        header.add(month);
        header.add(week);
        header.add(day);
        header.add(nTask);
        header.add(logout);

        return header;
    }

    public void makeTaskList()
    {
        Connection conn1 = null;
        Statement stmt1 = null;
        try{
        Class.forName("com.mysql.jdbc.Driver");
        conn1 = DriverManager.getConnection(dburl,user,pass);
        
        stmt1 = conn1.createStatement();
        String tasks = String.format("SELECT id FROM task AS t INNER JOIN calendar_task AS ct ON ct.task_id=t.id WHERE ct.calendar_id=%d;", cal.id);
        ResultSet rs = stmt1.executeQuery(tasks);
        
        while(rs.next())
        { taskList.clear(); taskList.add(new Task(rs.getInt("id"))); }
        rs.close();
        stmt1.close();
        conn1.close();
        }
        catch(SQLException se) { se.printStackTrace(); }
        catch(Exception e1) { e1.printStackTrace(); }
        finally{
            try{ if(stmt1!=null) { stmt1.close(); } }
            catch(SQLException se2) {}
            try{ if(conn1!=null) { conn1.close(); } }
            catch(SQLException se) { se.printStackTrace(); } }
    }

    public JPanel makeSideBar()
    {
        JPanel sidebar = new JPanel();
        //sidebar.setPreferredSize(new Dimension(175,725));
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
                    cal = new Calendar(i.id);
                    if (state == "month") { monthRefresh(); }
                    else if (state == "week") { weekRefresh(); }
                    else if (state == "day") { dayRefresh(); }
                    descPane.removeAll();
                    descPane.add(new JLabel(i.description));
                    descPane.setVisible(true);
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

    public JPanel makeMonth(LocalDate x, Calendar _cal)
    {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        
        JPanel month = new JPanel();
        month.setLayout(new GridLayout(6,7));

        long firstDay = LocalDate.of(x.getYear(),x.getMonth(),1).getDayOfWeek().getValue(); 
        if(firstDay == 7) {firstDay = 0;}
        long lastDay = LocalDate.of(x.getYear(),x.getMonth(),x.getMonth().maxLength()).getDayOfWeek().getValue();
        if(lastDay == 7) {lastDay = 0;}

        for(long i = 0; i < firstDay; i++)
        { month.add(new JPanel()); }
        ArrayList<Tile> tiles = new ArrayList();
        for(LocalDate i = LocalDate.of(x.getYear(), x.getMonth(), 1); i.compareTo(LocalDate.of(x.getYear(), x.getMonth(), x.getMonth().maxLength())) <= 0; i = i.plusDays(1))
        {
            Tile tempTile = new Tile(i, cal, true);
            if(i.compareTo(x) == 0)
            { tempTile.setSelected(true); }
            tiles.add(tempTile);
            month.add(tempTile);
        }
        for(long i = 0; i < 6 - lastDay; i++)
        { month.add(new JPanel()); }
        for (int i = 0; i < tiles.size(); i++)
        { tiles.get(i).addMouseListener(new calColorChanger(tiles)); }

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
        ArrayList<Tile> tiles = new ArrayList();
        for(long i = x.getDayOfWeek().getValue(); i > 0; i--)
        {
            Tile temp = new Tile(x.minusDays(i), cal);
            tiles.add(temp);
            days.add(temp);
        }
        Tile now = new Tile(x, cal);
        now.setSelected(true);
        tiles.add(now);
        days.add(now);
        for(long i = x.getDayOfWeek().getValue()-1; i < 5; i++)
        {
            Tile temp = new Tile(x.plusDays(i), cal);
            tiles.add(temp);
            days.add(temp);
        }
        for(int i = 0; i < tiles.size(); i++)
        { tiles.get(i).addMouseListener(new calColorChanger(tiles)); }

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
        day.add(new Tile(x,cal));
        return day;
    }

    public void run()
    {
        setSize(1050,725);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cp.setLayout(new BorderLayout());
        cp.add(makeHeaderButtons(cp), BorderLayout.NORTH);
        cp.add(makeSideBar(), BorderLayout.WEST);
        makeTaskList();
        mainPane.setLayout(new BorderLayout());
        mainPane.add(makeMonth(today, cal),BorderLayout.CENTER);
        cp.add(mainPane, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args)
    { javax.swing.SwingUtilities.invokeLater(new Planner(Integer.parseInt(args[0]), new Calendar(Integer.parseInt(args[1])))); }

    class calColorChanger extends Tile implements MouseListener
    {
        private ArrayList<Tile> tiles;
    
        public calColorChanger(ArrayList<Tile> _tiles)
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
            { tile.setBackground(new Color(165,165,165)); }
            //{ tile.setBackground(new Color(111,207,245)); }
        }
    
        public void mouseExited(MouseEvent e)
        {
            Tile tile = (Tile) e.getSource();
            if (!tile.getSelect())
            { tile.setBackground(new Color(255,255,255)); }
        }
    }
}
