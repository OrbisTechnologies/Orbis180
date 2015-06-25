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


import java.util.ArrayList;
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
    
  public DatabaseDAO(){
      Properties config= new Properties();
      dbLocation = config.getProperty("com.orbis.orbis180.sesame.server");
  
        Connection conn = null;
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
            s = conn.createStatement();
            statements.add(s);

            // We create a table...
            s.execute("create table location(num int, addr varchar(40))");
            System.out.println("Created table location");
            s.close();
        }
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
  void init(){
      
  }  
  
}