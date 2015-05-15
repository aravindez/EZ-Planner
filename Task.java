















import java.util.Properties;

import java.sql.*;

public class Task
{
    private int id;
    private String name;
    private LocalDate created_on;
    private LocalDate due;
    private String content;
    private boolean complete;
    public Task(_id)
    {
        Connection conn = null;
        Statement stmt = null;
        int id = 0;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl,user,pass);

            stmt = conn.createStatement();
            String valid = String.format("SELECT id, name, color, description FROM calendar id=%s;", Integer.toString(_id));
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
