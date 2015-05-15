import java.util.Properties;

import java.sql.*;

public class Calendar
{
    public int id;
    public String name;
    public int red;
    public int green;
    public int blue;
    public String description;

    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static final String dburl = "jdbc:mysql://127.0.0.1/cal";
    static final String user = "root";
    static final String pass = "avihome";

    public Calendar(int _id)
    {
        Connection conn = null;
        Statement stmt = null;
        int id = 0;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);

            stmt = conn.createStatement();
            String valid = String.format("SELECT id, name, color, description FROM calendar WHERE id=%s;", Integer.toString(_id));
            ResultSet rs = stmt.executeQuery(valid);

            while(rs.next())
            {
                id = rs.getInt("id");
                name = rs.getString("name");
                red = Integer.valueOf(rs.getString("color").substring(0,3));
                green = Integer.valueOf(rs.getString("color").substring(3,6));
                blue = Integer.valueOf(rs.getString("color").substring(6,9));
                description = rs.getString("description");
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
