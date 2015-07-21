/*
 * Connection to SQLite
 * @author glenn
 */
package be.ohatv.sqlite;

import java.sql.*;

public class conManager {
    
    //private static final String dburl = "jdbc:sqlite:C:\\Users\\glenn.MCSC\\Documents\\NetBeansProjects\\ohatv\\sqlite\\ohatv.sqlite";
    //private static final String dburl = "jdbc:sqlite:/home/glenn/Documents/Netbeans/ohatv/sqlite/ohatv.sqlite";
    private static final String dburl = "jdbc:sqlite:ohatv.sqlite";
    private static final String webappversion = "0.0.0.1";
    private static final String sqliteversion = "0.0.0.1";
    private static boolean Databaseset = false;
        
    public static void init(){
        if(Databaseset == false){
            if(doTestCreate() == false){
                createTables();
                Databaseset = true;
            } else {
                if(doTestVersion()){
                    Databaseset = true;
                }else{
                    //need update
                }
            }
        }
    }
    
    public static boolean doTestCreate(){
        boolean result = false;
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(dburl);
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='SETTINGS'");
                        while ( rs.next() ) {
                            result = true;
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    public static boolean doTestVersion(){
        boolean result = false;
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(dburl);
            if(connection != null){
                try{
                    connection.setAutoCommit(false);
                    Statement stmt = connection.createStatement();
                    try{
                        ResultSet rs = stmt.executeQuery( "SELECT KEY, VALUE FROM SETTINGS;");
                        while ( rs.next() ) {
                            //System.out.println(rs.getString(1));
                            //System.out.println(rs.getString(2));
                            result = true;
                        }
                        rs.close();
                    }catch (Exception ex) {

                    }
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                connection.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    private static void createTables() {
        try{
            StringBuilder sbsql = new StringBuilder();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(dburl);
            sbsql.append("CREATE TABLE TVSHOWS (")
                 .append("ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
                 .append("SHOWNAME TEXT, ")
                 .append("JSONSHORT TEXT,")
                 .append("JSONFULL TEXT")
                 .append("); ");

            sbsql.append("CREATE TABLE PROVIDERFEEDS (")
                 .append("ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
                 .append("SHOWID INTEGER NOT NULL, ")
                 .append("SHOWRSS TEXT,")
                 .append("SHOWRSSDATE TEXT,")
                 .append("EZTV TEXT,")
                 .append("EZTVDATE TEXT")
                 .append("); ");
            
            sbsql.append("CREATE TABLE SETTINGS (")
                 .append("ID INTEGER PRIMARY KEY AUTOINCREMENT,")
                 .append("KEY TEXT NOT NULL,")
                 .append("VALUE TEXT NOT NULL")
                 .append("); ");

            sbsql.append("CREATE TABLE REQUESTDOWNLOAD (")
                 .append("ID INTEGER PRIMARY KEY AUTOINCREMENT,")
                 .append("REQUEST TEXT NOT NULL,")
                 .append("JSON TEXT,")
                 .append("FILESEARCH TEXT,")
                 .append("FILENAME TEXT,")
                 .append("STATUS INT") //-1 do nothing, 0 to find, 1 found magnet and send to btclient, 2 downloaded
                 .append("); ");

            sbsql.append("CREATE TABLE LOG (")
                 .append("ID INTEGER PRIMARY KEY AUTOINCREMENT,")
                 .append("STATUS INT,") //1 nothing, 2 ok, 3 error, 4 magnet, 5 download, 6 moved
                 .append("MESSAGE TEXT, ")
                 .append("DATE TEXT ")
                 .append(")");

            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        
        try{
            StringBuilder sbsql = new StringBuilder();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection(dburl);
            sbsql.append("INSERT INTO SETTINGS (KEY,VALUE) VALUES (")
                 .append("'appversion'").append(", '").append(webappversion).append("'")
                 .append(");");

            sbsql.append("INSERT INTO SETTINGS (KEY,VALUE) VALUES (")
                 .append("'sqliteversion'").append(", '").append(sqliteversion).append("'")
                 .append(");");
            
            sbsql.append("INSERT INTO SETTINGS (KEY,VALUE) VALUES (")
                 .append("'btclient'").append(", ''")
                 .append(");");
            
            sbsql.append("INSERT INTO SETTINGS (KEY,VALUE) VALUES (")
                 .append("'bturl'").append(", ''")
                 .append(");");
            
            sbsql.append("INSERT INTO SETTINGS (KEY,VALUE) VALUES (")
                 .append("'btpath'").append(", ''")
                 .append(");");
            
            sbsql.append("INSERT INTO SETTINGS (KEY,VALUE) VALUES (")
                 .append("'MoveTo'").append(", ''")
                 .append(");");
            
            sbsql.append("INSERT INTO SETTINGS (KEY,VALUE) VALUES (")
                 .append("'btignorewords'").append(", 'esp'")
                 .append(");");
            


            Statement stmt = c.createStatement();
            stmt.executeUpdate(sbsql.toString());
            stmt.close();
            c.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static Boolean isDatabaseSet(){
        if(Databaseset == false){
            init();
        }
        return Databaseset;
    }
    
    public static String getdburl(){
        return dburl;
    }
}
