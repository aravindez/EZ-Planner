/*
    This is the newUser module.
*/

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.Color;

import java.util.Properties;

import java.sql.*;

public class newUser extends JFrame implements Runnable
{
    private Container cp = getContentPane();
    private JTextField fname = new JTextField();
    private JTextField lname = new JTextField();
    private JTextField uname = new JTextField();
    private JTextField pword = new JPasswordField();
    private JTextField cpword = new JPasswordField();
    private JTextField email = new JTextField();
    private JLabel error = new JLabel();

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "";

    public newUser()
    { super("New User"); }

    public void run()
    {
        setSize(355,175);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cp.setLayout(new BorderLayout());
        JPanel canvas = new JPanel();
        canvas.setLayout(new GridLayout(7,2));
        JLabel fnm = new JLabel("First Name: ");
        JLabel lnm = new JLabel("Last Name: ");
        JLabel un = new JLabel("Username: ");
        JLabel pw = new JLabel("Password: ");
        JLabel cpw = new JLabel("Confirm Password: ");
        JLabel em = new JLabel("Email: ");
                
        JButton add = new JButton("Submit");
        JButton exit = new JButton("Cancel");
        add.addActionListener(e -> { submit(uname.getText(),pword.getText(),cpword.getText(),email.getText(),fname.getText(),lname.getText()); dispose(); });
        exit.addActionListener(e -> { dispose(); });

        canvas.add(fnm); canvas.add(fname);
        canvas.add(lnm); canvas.add(lname);
        canvas.add(un); canvas.add(uname);
        canvas.add(pw);  canvas.add(pword);
        canvas.add(cpw); canvas.add(cpword);
        canvas.add(em);  canvas.add(email);
        canvas.add(add); canvas.add(exit);
        cp.add(canvas, BorderLayout.CENTER);
        error.setForeground(Color.RED);
        cp.add(error, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(add);
        setVisible(true);
    }

    public void submit(String uname, String pword, String cpword, String email, String fname, String lname)
    {
        if (uname.equals("") || pword.equals("") || cpword.equals("") || email.equals("") || fname.equals("") || lname.equals(""))
        {
            error.setText("field left empty");
            repaint();
            setVisible(true);
            return;
        }
        if (!pword.equals(cpword))
        {
            error.setText("passwords don't match");
            setVisible(true);
            return;
        }

        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);

            stmt = conn.createStatement();

            int uid=0;
            int cid=0;
            ResultSet rs;

            String insertUser = String.format("INSERT INTO user (username, password, email, first_name, last_name) VALUES ('%s', MD5('%s'), '%s', '%s', '%s');", uname, pword, email, fname, lname);
            stmt.executeUpdate(insertUser);

            String getUser = String.format("SELECT id FROM user WHERE username='%s' AND password=MD5('%s') AND email='%s' AND first_name='%s' AND last_name='%s';", uname, pword, email, fname, lname);
            rs = stmt.executeQuery(getUser);
            while(rs.next())
            { uid = rs.getInt("id"); }

            String insertCal = String.format("INSERT INTO calendar (name, color, description) VALUES ('%s', '24002602', \"%s %s\'s default calendar\");", uname, fname, lname);
            stmt.executeUpdate(insertCal);

            String getCal = String.format("SELECT id FROM calendar WHERE name='%s' AND description=\"%s %s\'s default calendar\";", uname, fname, lname);
            rs = stmt.executeQuery(getCal);
            while(rs.next())
            { cid = rs.getInt("id"); }

            String insertUserCal = String.format("INSERT INTO user_calendar (user_id, calendar_id, owner, viewCal, editCal) VALUES (%d, %d, 1, 1, 1);", uid, cid);
            stmt.executeUpdate(insertUserCal);

            rs.close();
            stmt.close();
            conn.close();
        }
        catch(SQLIntegrityConstraintViolationException workpls)
        {
            error.setText("username already taken");
            repaint();
            setVisible(true);
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
    { javax.swing.SwingUtilities.invokeLater(new newUser()); }
}
