/*
    This is the login module.
*/

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.Container;

import java.util.Properties;

import java.sql.*;

public class login extends JFrame implements Runnable
{
    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "";
    static final String pass = "";

    public login()
    { super("EZ Planner"); }

    /*
    * @param    JTextField u    the username field
    * @param    JTextField p    the password field
    * @return   String[]        a string array with the user and default calendar id's if the username and password are valid
                                if not, a string array with user and default calendar id's of zero
    */
    public String[] validate(JTextField u, JTextField p)
    {
        Connection conn = null;
        Statement stmt = null;
        int userid = 0;
        int calid = 0;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);
            String[] cred = {u.getText(), p.getText()};
            
            stmt = conn.createStatement();
            String valid = String.format("SELECT id FROM user WHERE username='%s' AND password=MD5('%s');", u.getText(), p.getText());
            ResultSet rs = stmt.executeQuery(valid);

            while(rs.next())
            { userid = rs.getInt("id"); }
            rs.close();

            String cal = String.format("SELECT calendar_id FROM user_calendar WHERE user_id=%s;", userid);
            ResultSet rsCal = stmt.executeQuery(cal);

            while(rsCal.next())
            { calid = rsCal.getInt("calendar_id"); break; }

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
        String[] ret = {Integer.toString(userid), Integer.toString(calid)};
        return ret;
    }

    public void run()
    {
        setSize(250,100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();
        cp.setLayout(new GridLayout(3,2));
        JLabel un = new JLabel("username: ");
        JLabel pw = new JLabel("password: ");
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        JButton signin = new JButton("Sign In");
        JButton noo = new JButton("New User");
        signin.addActionListener(e -> {
            String[] check = validate(username, password);
            if (Integer.parseInt(check[0]) == 0)
            {
                username.setText("wrong username or password");
                password.setText("");
            }
            else
            {
                dispose();
                Planner.main(check);
            }
        });
        noo.addActionListener(e -> { newUser.main(new String[0]); });
        cp.add(un);
        cp.add(username);
        cp.add(pw);
        cp.add(password);
        cp.add(signin);
        cp.add(noo);
        getRootPane().setDefaultButton(signin);
        setVisible(true);
    }

    public static void main(String [] args)
    { javax.swing.SwingUtilities.invokeLater(new login()); }
}
