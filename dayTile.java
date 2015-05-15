import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import java.time.LocalDate;
import java.time.Month;
import java.time.DayOfWeek;
import java.time.ZoneId;

import java.util.Properties;

import java.sql.*;

//there are 96 15-min intervals in a day

public class dayTile extends JPanel
{
    private LocalDate date;
    private int userid;
    private int calid;
    private ArrayList<JPanel> timeSlots;
    private boolean selected;
    private Border line = BorderFactory.createLineBorder(Color.black);

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "avihome";

    public dayTile(LocalDate _date, int _userid, int _calid)
    {
        this.date = _date;
        this.userid = _id;
        this.calid = _calid
        
        this.selected = false;
        for(int i = 0; i < 96; i++)
        { timeSlots.add(new JPanel()); }
        add(new JLabel(Integer.toString(daynum), SwingConstants.LEFT));
        setBackground(new Color(255,255,255));
        setBorder(line);
    }

    public static ArrayList<Task> getTaskList()
    {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);
            String[] cred = {u.getText(), p.getText()};

            stmt = conn.createStatement();
            String valid = String.format("SELECT id FROM task WHERE username='%s' AND password=MD5('%s');", u.getText(), p.getText());
            ResultSet rs = stmt.executeQuery(valid);

            while(rs.next())
            { id = rs.getInt("id"); }
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
