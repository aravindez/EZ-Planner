import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.GridLayout;

import java.time.LocalDate;
import java.time.Month;
import java.time.DayOfWeek;
import java.time.ZoneId;

import java.util.Properties;
import java.util.ArrayList;

import java.sql.*;

public class calTile extends JPanel
{
    private LocalDate date;
    public Calendar cal;
    private ArrayList<Task> taskList;
    private boolean selected;
    private Border line = BorderFactory.createLineBorder(Color.black);

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "avihome";

    public calTile(LocalDate _date, Calendar _cal)
    {
        this.date = _date;
        this.cal = _cal;
        this.taskList = calTile.getTaskList(cal.id, date);
        this.selected = false;
        setLayout(new GridLayout(6,1));
        add(new JLabel(Integer.toString(date.getDayOfMonth()), SwingConstants.LEFT));
        for(int i = 0; i < 5; i++)
        {
            try
            { add(new JLabel(taskList.get(i).name)); }
            catch (IndexOutOfBoundsException e)
            { break; }
        }
        setBackground(new Color(255,255,255));
        setBorder(line);
    }

    public calTile()
    { this(LocalDate.now(), new Calendar(1)); }

    public boolean getSelect()
    { return selected; }

    public void setSelected(boolean flag)
    {
        this.selected = flag;
        if(flag)
        { setBackground(new Color(120,120,120)); }
        else
        { setBackground(new Color(255,255,255)); }
    }

    private static ArrayList<Task> getTaskList(int calid, LocalDate date)
    {
        ArrayList<Task> list = new ArrayList<Task>();
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);

            stmt = conn.createStatement();
            String valid = String.format("SELECT id FROM task INNER JOIN calendar_task AS ct ON ct.task_id=task.id WHERE ct.calendar_id=%d AND due='%d-%d-%d';", calid, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            ResultSet rs = stmt.executeQuery(valid);

            while(rs.next())
            { list.add(new Task(rs.getInt("id"))); }
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
        return list;
    }
}
