import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
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
    public LocalDate currDate = today;
    private Border raisedBevel = BorderFactory.createRaisedBevelBorder();
    private Border line = BorderFactory.createLineBorder(Color.black);
    public int userid;
    public Calendar cal;
    private Container cp = getContentPane();
    public String state = "month";

    public JPanel descPane = new JPanel();
    private JPanel mainPane = new JPanel();

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "";

    public Planner(int _userid, Calendar _cal)
    { super("EZ Planner"); userid=_userid; cal=_cal;}

    // refreshes the month view
    public void monthRefresh(LocalDate x)
    {
        cp.removeAll();
        cp.add(makeHeaderButtons(),BorderLayout.NORTH);
        cp.add(makeSideBar(),BorderLayout.WEST);
        cp.add(makeMonth(x,cal),BorderLayout.CENTER);
        setVisible(true); 
    }

    // refreshes the week view
    public void weekRefresh(LocalDate x)
    {
        cp.removeAll(); 
        cp.add(makeHeaderButtons(),BorderLayout.NORTH); 
        cp.add(makeSideBar(),BorderLayout.WEST);
        cp.add(makeWeek(x),BorderLayout.CENTER);
        setVisible(true);
    }

    // refreshes the day view
    public void dayRefresh(LocalDate x)
    {
        cp.removeAll();
        cp.add(makeHeaderButtons(),BorderLayout.NORTH);
        cp.add(makeSideBar(),BorderLayout.WEST);
        cp.add(makeDay(x),BorderLayout.CENTER);
        setVisible(true); 
    }

    // makes the navigational buttons at the top of the view
    public JPanel makeHeaderButtons()
    {
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(1,5));
        JButton month = new JButton("Month");
        JButton week = new JButton("Week");
        JButton day = new JButton("Day");
        JButton nTask = new JButton("New Task");
        JButton logout = new JButton("Logout");
        month.addActionListener(e -> { state = "month"; descPane.removeAll(); monthRefresh(currDate); });
        week.addActionListener(e -> { state = "week"; descPane.removeAll(); weekRefresh(currDate); });
        day.addActionListener(e -> { state = "day"; descPane.removeAll(); dayRefresh(currDate); });
        nTask.addActionListener(e -> { dispose(); String[] temp = { Integer.toString(userid),Integer.toString(cal.id)}; newTask.main(temp); });
        logout.addActionListener(e -> { dispose(); login.main(new String[1]);});
        header.add(month);
        header.add(week);
        header.add(day);
        header.add(nTask);
        header.add(logout);

        return header;
    }

    // constructs the sidebar that helps you navigate through the different calendars you own
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
        buttons.setLayout(new GridLayout(calList.size()+1,1));
        JButton addCal = new JButton("Add Calendar");
        addCal.addActionListener(e ->
            {
                dispose();
                String[] temp = {Integer.toString(userid)};
                //newCal.main(temp);
            });
        buttons.add(addCal);
        for(Calendar i : calList)
        {
            JLabel temp = new JLabel("  "+i.name+"  ");
            temp.addMouseListener(new options(this, i));
            buttons.add(temp);
        }
        sidebar.add(buttons, BorderLayout.NORTH);
        sidebar.add(descPane, BorderLayout.SOUTH);
        return sidebar;
    }

    // creates the weekday labels
    public JPanel weekHeader()
    {
        JPanel weekHeader = new JPanel();
        weekHeader.setBorder(line);
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

    // makes the month name and weekday labels
    public JPanel makeHeader()
    {
        JPanel monthHeader = new JPanel();
        monthHeader.setLayout(new GridLayout(2,1));

        JPanel monthWButtons = new JPanel();
        monthWButtons.setLayout(new BorderLayout());

        JPanel monthName = new JPanel();
        monthName.add(new JLabel(currDate.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" - "+currDate.getYear()),SwingConstants.CENTER);
        monthName.setBorder(line);

        if (state == "month")
        {
            JButton back, next;
            if (currDate.getMonth().getValue()==1)
            {
                back = new JButton(Month.of(12).getDisplayName(TextStyle.FULL,Locale.ENGLISH));
            }
            else
            {
                back = new JButton(Month.of(currDate.getMonth().getValue()-1).getDisplayName(TextStyle.FULL,Locale.ENGLISH));
            }
            back.setBorder(line);
            back.addActionListener(e -> {
                if (currDate.getMonth().getValue() == 1)
                {
                    currDate = LocalDate.of(currDate.getYear()-1,12,1);
                }
                else
                {
                    currDate = LocalDate.of(currDate.getYear(),currDate.getMonth().getValue()-1,1);
                }
                if (state == "month") { monthRefresh(currDate); }
                else if (state == "week") { weekRefresh(currDate); }
                else if (state == "day") { dayRefresh(currDate); }
            });
            if (currDate.getMonth().getValue()==12)
            {
                next = new JButton(Month.of(1).getDisplayName(TextStyle.FULL,Locale.ENGLISH));
            }
            else
            {
                next = new JButton(Month.of(currDate.getMonth().getValue()+1).getDisplayName(TextStyle.FULL,Locale.ENGLISH));
            }
            next.setBorder(line);
            next.addActionListener(e -> {
                if (currDate.getMonth().getValue() == 12)
                {
                    currDate = LocalDate.of(currDate.getYear()+1,1,1);
                }
                else
                {
                    currDate = LocalDate.of(currDate.getYear(),currDate.getMonth().getValue()+1,1);
                }
                if (state == "month") { monthRefresh(currDate); }
                else if (state == "week") { weekRefresh(currDate); }
                else if (state == "day") { dayRefresh(currDate); }
            });

            monthWButtons.add(back,BorderLayout.WEST);
            monthWButtons.add(next,BorderLayout.EAST);
        }

        monthWButtons.add(monthName,BorderLayout.CENTER);

        monthHeader.add(monthWButtons);
        monthHeader.add(weekHeader());
        return monthHeader;
    }

    // constructs the month view for a given date and calendar
    public JPanel makeMonth(LocalDate x, Calendar _cal)
    {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        
        JPanel month = new JPanel();
        month.setLayout(new GridLayout(6,7));

        // creates variables for the first of the month and the end of the month
        long firstDay = LocalDate.of(x.getYear(),x.getMonth(),1).getDayOfWeek().getValue(); 
        if(firstDay == 7) {firstDay = 0;}

        long lastDay;
        LocalDate endDay;
        if (x.getMonth().getValue() == 2 && !x.isLeapYear())
        {
            lastDay = LocalDate.of(x.getYear(),x.getMonth(),28).getDayOfWeek().getValue();
            endDay = LocalDate.of(x.getYear(),x.getMonth(),28);
        }
        else
        {
            lastDay = LocalDate.of(x.getYear(),x.getMonth(),x.getMonth().maxLength()).getDayOfWeek().getValue();
            endDay = LocalDate.of(x.getYear(), x.getMonth(), x.getMonth().maxLength());
        }
        if(lastDay == 7) {lastDay = 0;}

        // creates the blank jpanels before the first of the month
        int counter = 0;
        for(long i = 0; i < firstDay; i++)
        { month.add(new JPanel()); counter++; }

        // creates the actual month
        ArrayList<Tile> tiles = new ArrayList();
        for(LocalDate i = LocalDate.of(x.getYear(), x.getMonth(), 1); i.compareTo(endDay) <= 0; i = i.plusDays(1))
        {
            Tile tempTile = new Tile(this, i, cal, true);
            if(i.compareTo(x) == 0)
            { tempTile.setSelected(true); }
            tiles.add(tempTile);
            month.add(tempTile);
            counter++;
        }

        // creates the blank days after then end of the month
        for(long i = 0; i < 6 - lastDay; i++)
        { month.add(new JPanel()); counter++; }
        for(int i = 0; i < 42-counter; i++)
        { month.add(new JPanel()); }

        for (int i = 0; i < tiles.size(); i++)
        { tiles.get(i).addMouseListener(new calColorChanger(tiles)); }

        pane.add(makeHeader(),BorderLayout.NORTH);
        pane.add(month,BorderLayout.CENTER);
        return pane;
    }

    public JPanel makeWeek(LocalDate x)
    {
        JPanel week = new JPanel();
        week.setLayout(new BorderLayout());

        JPanel days = new JPanel();
        days.setLayout(new GridLayout(1,7));

        // constructs tiles before current date
        ArrayList<Tile> tiles = new ArrayList();
        for(long i = x.getDayOfWeek().getValue(); i > 0; i--)
        {
            Tile temp = new Tile(this, x.minusDays(i), cal);
            tiles.add(temp);
            days.add(temp);
        }

        // constructs current date tile
        Tile now = new Tile(this, x, cal);
        now.setSelected(true);
        tiles.add(now);
        days.add(now);

        // constructs tiles after current date
        for(long i = x.getDayOfWeek().getValue()-2; i < 4; i++)
        {
            Tile temp = new Tile(this, x.plusDays(i), cal);
            tiles.add(temp);
            days.add(temp);
        }

        for(int i = 0; i < tiles.size(); i++)
        { tiles.get(i).addMouseListener(new calColorChanger(tiles)); }

        week.add(makeHeader(),BorderLayout.NORTH);
        week.add(days,BorderLayout.CENTER);
        return week;
    }

    // constructs day view
    public JPanel makeDay(LocalDate x)
    {
        JPanel day = new JPanel();
        day.setLayout(new BorderLayout());
        JPanel header = new JPanel();
        header.add(new JLabel(x.getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.ENGLISH)));
        day.add(header,BorderLayout.NORTH);
        day.add(new Tile(this,x,cal));
        return day;
    }

    public void run()
    {
        setSize(1050,725);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cp.setLayout(new BorderLayout());
        cp.add(makeHeaderButtons(), BorderLayout.NORTH);
        cp.add(makeSideBar(), BorderLayout.WEST);
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
                tile.setPlannerDate();
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

    class options implements MouseListener
    {
        private Planner pl;
        private Calendar cal;

        public options(Planner _pl, Calendar _cal)
        { pl = _pl; cal = _cal; }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseClicked(MouseEvent e)
        {
            JLabel label = (JLabel) e.getSource();

            if (e.getClickCount() == 2)
            {
                dispose();
                String[] temp = {Integer.toString(pl.userid),Integer.toString(cal.id)};
                //calendarOptions.main(temp);
            }
            else
            {
                pl.cal = new Calendar(cal.id);
                if (pl.state == "month") { monthRefresh(currDate); }
                else if (pl.state == "week") { weekRefresh(currDate); }
                else if (pl.state == "day") { dayRefresh(currDate); }
                pl.descPane.removeAll();
                pl.descPane.add(new JLabel(cal.description));
                pl.descPane.setVisible(true);
                pl.setVisible(true);
            }
        }
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }
}
