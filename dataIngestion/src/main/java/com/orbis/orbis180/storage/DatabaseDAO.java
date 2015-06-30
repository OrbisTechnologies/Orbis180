/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;

import com.orbis.orbis180.dataStructures.WileyQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
//import java.util.Collections.ArrayList;

/**
 *
 * @author cblount
 */
public class DatabaseDAO {
    
  private Connection connect = null;
  private Statement statement = null;
  private ResultSet resultSet = null;

  public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
  
    /* the default framework is embedded */
    private String framework = "embedded";
    private String protocol = "jdbc:derby:";
    private String dbLocation ;
    private Connection conn = null;
  public DatabaseDAO(){
      Properties config= new Properties();
      dbLocation = config.getProperty("com.orbis.orbis180.sesame.server");
  
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        PreparedStatement psInsert;
        PreparedStatement psUpdate;
        Statement s;
        ResultSet rs = null;
        try{
            String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
            conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");


            System.out.println("Connected to and created database " + dbName);

            // We want to control transactions manually. Autocommit is on by
            // default in JDBC.
            conn.setAutoCommit(true);

            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
        }
        
        //Standard SQL error handling
        catch (SQLException sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // ResultSet
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }
        }
  }
  /**
   * Clears the connection to the internal storage
   */
  public void uninit(){
            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
  }
  
    /**
     * Clears information from the Internal storage
     */
  public void clear(){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        Statement drops;
        ResultSet rs = null;
        try{
     
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            if(conn==null){
            String dbName = "derbyDB"; // the name of the database
            
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
            conn = DriverManager.getConnection(protocol + dbName + dbLocation + ";create=true");
             System.out.println("Connected to database " + dbName);

            // We want to control transactions manually. Autocommit is on by
            // default in JDBC.
            conn.setAutoCommit(true);
            }
            
            
            DatabaseMetaData dbmd = conn.getMetaData();
            s = conn.createStatement();
            statements.add(s);
            drops =  conn.createStatement();
            statements.add(drops);
            rs = dbmd.getTables(null, "APP", "queries", null);
            //if the table doesn't exist
            
            if(!rs.next())
            {// We (re)create a table...
                drops.execute("drop table queries");
              s.execute("create table queries(keyword varchar(100), location varchar(100), responseTime int, queryDate date)");
            }
            System.out.println("Dropped and Created table queries");
         rs.close();
            s.close();
            
        }
        catch (Exception sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // ResultSet
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

        } 
  }  
  
  /**
   * 
   * This Creates the query Table if it doesn't exist
   * @param createDB 
   */
  public void init(Boolean createDB){
     
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        Statement drops;
        ResultSet rs = null;
        try{
     
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            if(conn==null){
            String dbName = "derbyDB"; // the name of the database
            
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
            conn = DriverManager.getConnection(protocol + dbName + dbLocation + ";create=true");
             System.out.println("Connected to and created database " + dbName);

            // We want to control transactions manually. Autocommit is on by
            // default in JDBC.
            conn.setAutoCommit(true);
            }
            
            
            DatabaseMetaData dbmd = conn.getMetaData();
            if(createDB){
            s = conn.createStatement();
            statements.add(s);
            drops =  conn.createStatement();
            statements.add(drops);
            rs = dbmd.getTables(null, "APP", "queries", null);
            //if the table doesn't exist
            
            if(!rs.next())
            {// We (re)create a table...
               // drops.execute("drop table queries");
              s.execute("create table queries(keyword varchar(100), location varchar(100), responseTime int, queryDate date)");
            }
            System.out.println("Created table queries");
         rs.close();
            s.close();
            }
        }
        catch (Exception sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // ResultSet
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

        } 
  }  
  
  /***
   * Adds a record of a Query that has gone to the server
   * @param location
   * @param keyword
   * @param responseTime 
   */
  public void addQuery( String location, String keyword, Integer responseTime){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        try{
            
            if(conn==null){
                String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
                
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
                conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");
            }
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            DatabaseMetaData dbmd = conn.getMetaData();
            //if the table doesn't exist
            
            java.util.Date date = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date( date.getTime() );
            //protection from sql injection
            PreparedStatement insertQuery = conn.prepareStatement("insert into queries (keyword, location, responseTime, queryDate) values ( ? , ? , ? , ? )");
            statements.add(insertQuery);
            
            insertQuery.setString(1, keyword);
            insertQuery.setString(2, location);
            insertQuery.setInt(3, responseTime);
            insertQuery.setDate(4, sqlDate);
            
            insertQuery.executeUpdate();
            insertQuery.close();
        }
        catch (Exception sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } 
    }
  
 
public Integer getQueryCount(){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs = null;
        int returnVal=0;
        try{
            
            if(conn==null){
                String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
                conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");
            }
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            s = conn.createStatement();
            statements.add(s);
            DatabaseMetaData dbmd = conn.getMetaData();
            //if the table doesn't exist
            rs = s.executeQuery("select count ( responseTime ) as total from queries");
            if (rs.next()) {
                returnVal = rs.getInt("total");
            }
            s.close();
            rs.close();
        }
        
        catch (SQLException sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } 
        return returnVal;
    }
     

public Integer getQueryCountSince( java.util.Date date){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs = null;
        java.sql.Date sqlDate = new java.sql.Date( date.getTime() );
        int returnVal=0;
        try{
            if(conn==null){
                String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
                conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");
            }
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            s = conn.createStatement();
            statements.add(s);
            DatabaseMetaData dbmd = conn.getMetaData();
            //if the table doesn't exist
            PreparedStatement countquery = conn.prepareStatement("select count ( * ) as total from queries where queryDate > ? ");
            
            countquery.setDate(1, sqlDate);
            rs = countquery.executeQuery();
            if (rs.next()) {
                returnVal = rs.getInt("total");
            }
            s.close();
            rs.close();
        }
        
        catch (SQLException sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } 
        return returnVal;
    }
    public Integer getAvgQueryTime(){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs = null;
        int returnVal=0;
        try{
            
            if(conn==null){
                String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
                conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");
            }
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            s = conn.createStatement();
            statements.add(s);
            DatabaseMetaData dbmd = conn.getMetaData();
            //if the table doesn't exist
            rs = s.executeQuery("select avg ( responseTime ) as total from queries");
            if (rs.next()) {
                returnVal = rs.getInt("total");
            }
            s.close();
            rs.close();
        }
        
        catch (SQLException sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } 
        return returnVal;
    }
    
    
    public Integer getQueriesPerDay(){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs = null;
        int returnVal=0;
        try{
            String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
            conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");

            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            s = conn.createStatement();
            statements.add(s);
            DatabaseMetaData dbmd = conn.getMetaData();
            
            rs = s.executeQuery("select count( * ) as averagepermonth from queries group by queryDate");
            returnVal=0;
            int dayCount=0;
            while (rs.next()) {
                returnVal += rs.getInt("averagepermonth");
                dayCount++;
            }
            returnVal= returnVal/ dayCount;
            s.close();
            rs.close();
        }
        
        catch (SQLException sqle)
        {
            System.out.println(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } 
        return returnVal;
    }
    public List<WileyQuery> getTopTen(){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs = null;
        int returnVal=0;
        List<WileyQuery> retval= new ArrayList<WileyQuery>();
        
        try{
            if(conn==null){
                String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
              
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
                conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");
                
            }
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            
            s = conn.createStatement();
            statements.add(s);
            DatabaseMetaData dbmd = conn.getMetaData();
            //if the table doesn't exist
            //rs = s.executeQuery("SELECT keyword, count(*)FROM queries " + "GROUP BY keyword ORDER BY Count(*) DESC");
            rs = s.executeQuery("SELECT keyword, count(keyword) FROM queries " + "GROUP BY keyword ORDER BY Count(keyword) DESC");
            for(int i=1; i<11; i++){
                if (rs.next()) {
                    WileyQuery  outval = new WileyQuery();
                    outval.count = rs.getInt("2");
                    outval.searchField = rs.getString("keyword");
                    retval.add(outval);
                }
            }
            s.close();
            rs.close();
        }
        
        catch (Exception ex)
        {
            System.out.println(ex);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } 
        return retval;
    }
    

            
    public List<WileyQuery> getQueryFreq(){
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs = null;
        int returnVal=0;
        List<WileyQuery> retval= new ArrayList<WileyQuery>();
        
        try{
            if(conn==null){
                String dbName = "derbyDB"; // the name of the database

            /*
             * This connection specifies create=true in the connection URL to
             * cause the database to be created when connecting for the first
             * time. To remove the database, remove the directory derbyDB (the
             * same as the database name) and its contents.
             *
             * The directory derbyDB will be created under the directory that
             * the system property derby.system.home points to, or the current
             * directory (user.dir) if derby.system.home is not set.
             */
              
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
                conn = DriverManager.getConnection(protocol + dbName + dbLocation
                    + ";create=true");
                
            }
            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            
            s = conn.createStatement();
            statements.add(s);
            DatabaseMetaData dbmd = conn.getMetaData();
            //if the table doesn't exist
            //rs = s.executeQuery("SELECT keyword, count(*)FROM queries " + "GROUP BY keyword ORDER BY Count(*) DESC");
            rs = s.executeQuery("SELECT queryDate as date, count(keyword) as freq FROM queries " + "GROUP BY queryDate ORDER BY queryDate");
            
                while (rs.next()) {
                    WileyQuery  outval = new WileyQuery();
                    outval.count = rs.getInt("freq");
                    outval.date = rs.getDate("date").toString();
                    retval.add(outval);
                }
            
            s.close();
            rs.close();
        }
        
        catch (Exception ex)
        {
            System.out.println(ex);
        } finally {
            // release all open resources to avoid unnecessary memory usage

            // Statements and PreparedStatements
            int i = 0;
            while (!statements.isEmpty()) {
                // PreparedStatement extend Statement
                Statement st = (Statement)statements.remove(i);
                try {
                    if (st != null) {
                        st.close();
                        st = null;
                    }
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }

            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } 
        return retval;
    }
    
}