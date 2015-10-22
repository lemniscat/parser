package parser;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 */
public class SqlInterface
{
    Boolean sqlIsConnected = false;

    public java.sql.Date ConvertJavaDateToSqlDate(Date date)
    {
        return new java.sql.Date(date.getTime());
    }

    public Boolean UpdateSqlBase(Connection con, ArrayList<WorkInfo> work)
    {
        if (sqlIsConnected)
        {
            for (WorkInfo temp : work)
                try
                {
                    String sql = "INSERT INTO vacancy (name,city,money,date,global,url) VALUES";
                    sql = sql + " ('" + temp.name + "','";
                    sql = sql + temp.city + "','";
                    sql = sql + temp.money + "','";
                    sql = sql + ConvertJavaDateToSqlDate(temp.date) + "','";
                    sql = sql + temp.global + "','";
                    sql = sql + temp.url + "');";

                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(sql);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }


        }
        try
        {
            con.close();
        }
        catch (Exception e)
        {
        }
        return true;
    }

    public Connection ConnectSQL()//метод подключение к базе данный
    {
        // обьявление обьектов, пример из документации MS
        Connection con = null;
        try
        {
            // Establish the connection.
            SQLServerDataSource ds = new SQLServerDataSource();
            ds.setServerName("SRV\\BASECOMP");
            ds.setPortNumber(47634);
            ds.setDatabaseName("pars");
            ds.setPassword("1");
            ds.setUser("user/parser");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = ds.getConnection();
            if (!con.isClosed())
                sqlIsConnected = true;
        }
        // Handle any errors that may have occurred.
        catch (Exception e)
        {
            e.printStackTrace();
            sqlIsConnected = false;
            return con = null;
        }
        return con;
    }
}
