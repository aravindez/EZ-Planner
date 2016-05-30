/*
    This is a Task object.
    It retrieves and holds all the relevant information about a task, given an id:
        name
        created date
        due date
        content
        whether it is complete
*/

import java.time.LocalDate;

import java.util.Properties;

import java.sql.*;

public class Task
{
    public int id;
    public String name;
    public LocalDate created_on;
    public LocalDate due;
    public String content;
    public boolean complete;

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "";

    public Task(int _id)
    {
        Connection conn = null;
        Statement stmt = null;
        int id = 0;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);

            stmt = conn.createStatement();
            String valid = String.format("SELECT id, name, created_on, due, content, complete FROM task WHERE id=%s;", Integer.toString(_id));
            ResultSet rs = stmt.executeQuery(valid);

            String temp;
            while(rs.next())
            {
                id = rs.getInt("id");
                name = rs.getString("name");
                temp = rs.getString("created_on");
                created_on = LocalDate.of(Integer.valueOf(temp.substring(0,4)),Integer.valueOf(temp.substring(5,7)),Integer.valueOf(temp.substring(8,10)));
                temp = rs.getString("due");
                due = LocalDate.of(Integer.valueOf(temp.substring(0,4)),Integer.valueOf(temp.substring(5,7)),Integer.valueOf(temp.substring(8,10)));
                content = rs.getString("content");
                complete = (rs.getInt("complete") == 1) ? true : false;
            }
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
}
