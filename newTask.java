/*
    This is the newTask module.
*/

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;

import java.util.Properties;

import java.sql.*;

public class newTask extends JFrame implements Runnable
{
    private int userid;
    private int calid;

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "avihome";

    public newTask(int _userid, int _calid)
    { super("New Task"); userid = _userid; calid = _calid; }

    public void run()
    {
        setSize(355,123);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();
        setLayout(new GridLayout(4,2));
        JLabel nm = new JLabel("Name: ");
        JLabel d = new JLabel("Due Date (YYYY-MM-DD): ");
        JLabel de = new JLabel("Description: "); 
        JTextField name = new JTextField();        
        JTextField due = new JTextField();        
        JTextField desc = new JTextField();        
        JButton add = new JButton("Submit");
        JButton exit = new JButton("Cancel");
        add.addActionListener(e -> { submit(name.getText(),due.getText(),desc.getText()); String[] temp = {Integer.toString(userid),Integer.toString(calid)}; Planner.main(temp); dispose(); });
        exit.addActionListener(e -> { String[] temp = {Integer.toString(userid),Integer.toString(calid)}; Planner.main(temp); dispose(); });
        cp.add(nm); cp.add(name);
        cp.add(d); cp.add(due);
        cp.add(de); cp.add(desc);
        cp.add(add); cp.add(exit);
        getRootPane().setDefaultButton(add);
        setVisible(true);
    }

    /*
    * @param    String name     name to be inserted in the task record
    * @param    String due      due date to be inserted in the task record
    * @param    String desc     description to be inserted in the task record
    */
    public void submit(String name, String due, String desc)
    {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);

            stmt = conn.createStatement();

            String insert = String.format("INSERT INTO task (name, created_on, due, content) VALUES ('%s', CURDATE(), '%s', '%s'); ", name, due, desc, calid);
            stmt.executeUpdate(insert);

            int taskid = 0;
            String getId = String.format("SELECT id FROM task WHERE name='%s' AND due='%s' AND content='%s';", name, due, desc);
            ResultSet rs = stmt.executeQuery(getId);
            while(rs.next())
            { taskid = rs.getInt("id"); }

            String connect = String.format("INSERT INTO calendar_task (calendar_id, task_id) VALUES (%d, %d);", calid, taskid);
            stmt.executeUpdate(connect);

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
    }

    public static void main(String[] args)
    { javax.swing.SwingUtilities.invokeLater(new newTask(Integer.parseInt(args[0]), Integer.parseInt(args[1]))); }
}
