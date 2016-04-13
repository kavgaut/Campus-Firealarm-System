

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author kavyagautam
 */
public class populate {
    
    public static String[] bld_id = null;
    public static String[] bld_name = null;
    public static String[] vertices = null;
    public static String[] bld_ordinates = null;
    public static String[] hyd_id = null;
    public static String[] hyd_ordinates = null;
    public static String[] buidings_on_fire = null;
    
    
    public static void main(String[] args) {
        populate ins = new populate();
        System.out.println("Reading files from inside the populate.java file for now"); 
        System.out.println();
        ins.read(args[0], args[1], args[2]);
        System.out.println("Reading files COMPLETE. Connecting to the Database ......");
        System.out.println();
        System.out.println();
        System.out.println("*******************************************************************************************************************");
        ins.run();     
    }  
    //A helper function to complete the polygon of each building by copying the first vertices set in the end
    public static int nthOccurrence(String str, char c, int n) {
    int pos = str.indexOf(c, 0);
    while (n-- > 0 && pos != -1)
        pos = str.indexOf(c, pos+1);
    return pos;
    }  
        
    private void run() { 
        //load driver
        try{
            Class.forName("oracle.jdbc.OracleDriver").newInstance();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        //define connection URL
        String host = "dagobah.engr.scu.edu";
        String dbname = "db11g";
        int port = 1521;
        String oracleURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbname;
        
       
        //establish connection
        String uname = "**";      
        String pwd = "**";
        try {
            Connection connection = DriverManager.getConnection(oracleURL, uname, pwd);
            //DB metadata info
             DatabaseMetaData dbmetadata = connection.getMetaData();
             String prodName = dbmetadata.getDatabaseProductName();
             String prodVer = dbmetadata.getDatabaseProductVersion();
             
             System.out.println("prodName is " + prodName);
             System.out.println("prodVersion is " + prodVer);
             System.out.println();
             System.out.println();
             System.out.println("Connection succeded");
             System.out.println("*******************************************************************************************************************");
             System.out.println();
             publishData(connection);
             try { 
               connection.close(); 
             } catch (SQLException e) { 
               System.err.println("Cannot close connection: " + e.getMessage()); 
           }
             System.out.println();
             System.out.println("!!!Database connection closed succesfully!!!");
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    } 
    private void publishData(Connection con) throws SQLException {
        
        //to clean up the old data in the table before inserting new data to avoid duplicates
        Statement stmt = con.createStatement(); 
        
        System.out.println("Deleting previous tuples to avoid duplictaion of records..."); 
        stmt.executeUpdate("delete BLD_ON_FIRE");
        stmt.executeUpdate("delete BUILDING_INFO"); 
        stmt.executeUpdate("delete FIREHYDRANTS_INFO"); 
        System.out.println();
      
        //1
        System.out.println("Inserting Data into BUILDING_INFO table");
        for (int b=1; b<37; b++)
                {
                    String buildinginfo = "INSERT INTO BUILDING_INFO VALUES ('" + bld_id[b] + "','" + bld_name[b] + "'," + vertices[b] + ",\n" + "MDSYS.SDO_GEOMETRY(" + 2003 + ", null, null, MDSYS.SDO_ELEM_INFO_ARRAY(" + 1 + "," + 1003 + "," + 1 + "),MDSYS.SDO_ORDINATE_ARRAY (" + bld_ordinates[b] + ")))";
                    //System.out.println(buildinginfo);
                    stmt.executeUpdate(buildinginfo);
                }
        System.out.println("inserted records into BUILDING_INFO table succesfully");
        System.out.println();
        //2
        System.out.println("Inserting Data into FIREHYDRANTS_INFO table");
        for (int h=1; h<81; h++)
                {
                    String fhinfo = "INSERT INTO FIREHYDRANTS_INFO VALUES ('" + hyd_id[h] + "',\n" + "MDSYS.SDO_GEOMETRY(" + 2001 + ", null, MDSYS.SDO_POINT_TYPE(" + hyd_ordinates[h] + ",NULL),NULL,NULL))";
                    //System.out.println(fhinfo);
                    stmt.executeUpdate(fhinfo);
                }
        System.out.println("inserted records into FIREHYDRANTS_INFO table succesfully");
        System.out.println();
        
        //3
        System.out.println("Inserting Data into BLD_ON_FIRE table");
        for (int f=1; f<4; f++)
                {
                    String firebuildings = "insert into BLD_ON_FIRE VALUES('" + buidings_on_fire[f] + "')";
                    //System.out.println(firebuildings);
                    stmt.executeUpdate(firebuildings);
                }
        System.out.println("inserted records into BLD_ON_FIRE table succesfully");
        System.out.println();
        
    }
    
    public void read(String filename1, String filename2, String filename3){
      
      BufferedReader br1 = null;
      BufferedReader br2 = null;
      BufferedReader br3 = null;
    
      try {
          String temp1 = "";
          String temp2 = "";
          String temp3 = "";
          String temp4 = "";
          String temp5 = "";
          String temp6 = "";
          String temp7 = "";
         
          ArrayList<String> A1 = new ArrayList<>(Arrays.asList(temp1));
          ArrayList<String> A2 = new ArrayList<>(Arrays.asList(temp2));
          ArrayList<String> A3 = new ArrayList<>(Arrays.asList(temp3));
          ArrayList<String> A4 = new ArrayList<>(Arrays.asList(temp4));
          ArrayList<String> A5 = new ArrayList<>(Arrays.asList(temp5));
          ArrayList<String> A6 = new ArrayList<>(Arrays.asList(temp6));
          ArrayList<String> A7 = new ArrayList<>(Arrays.asList(temp7));
          
          String line = null;
          
      //processeing First file Building.xy
        System.out.println("Parsing the First file - Building.xy");
        System.out.println();
      
        br1 = new BufferedReader(new FileReader(filename1));
     
        while ((line = br1.readLine()) != null) {
          
          String[] values = line.split(",",4);
          temp1 = values[0];
          temp2 = values[1];
          temp3 = values[2];
          temp4 = values[3];
          A1.add(temp1);
          A2.add(temp2);
          A3.add(temp3);
          A4.add(temp4);
          
      }
        
        bld_id = A1.toArray(new String[A1.size()]);
        bld_name = A2.toArray(new String[A2.size()]);
        vertices = A3.toArray(new String[A3.size()]);
        bld_ordinates = A4.toArray(new String[A4.size()]);
        
        //Logic for Adding the first coordinates set to the end to finish a simple polygon
        for (int i=1; i < bld_ordinates.length; i++){
            String str = bld_ordinates[i];
            int index = nthOccurrence(str, ',', 1);
            bld_ordinates[i] = bld_ordinates[i] + ", " + str.substring(1,index);  
            //System.out.println(bld_ordinates[i]);
        }
    
  
        //System.out.print(Arrays.toString(bld_id));
        //System.out.println();
        //System.out.println(bld_id.length);
        //System.out.print(Arrays.toString(bld_name));
        //System.out.println();
        //System.out.print(bld_name[0]);
        //System.out.println();
        //System.out.print(bld_name[1]);
        //System.out.println();
        //System.out.print(bld_name[2]);
        //System.out.println();
        //System.out.println();
        //System.out.println(bld_name.length);
        //System.out.print(Arrays.toString(vertices));
        //System.out.println();
        //System.out.println(vertices.length);
        //System.out.print(Arrays.toString(bld_ordinates));
        //System.out.println();
        //System.out.println(bld_ordinates.length);
        
        //System.out.println(bld_ordinates[0]);
        //System.out.println(bld_ordinates[1]);
        //System.out.println(bld_ordinates[2]);
        //System.out.println(bld_ordinates[35]);
        //System.out.println(bld_ordinates[36]);
        
        
        //Processing second file Hydrants.xy
        System.out.println("Parsing the second file Hydrants.xy");
        System.out.println();
        br2 = new BufferedReader(new FileReader(filename2));
 
        while ((line = br2.readLine()) != null) {
          
          String[] values = line.split(",",2);
          temp5 = values[0];
          temp6 = values[1];
          A5.add(temp5);
          A6.add(temp6);
        }
        
        hyd_id = A5.toArray(new String[A5.size()]);
        hyd_ordinates = A6.toArray(new String[A6.size()]);
       
        //System.out.print(Arrays.toString(hyd_id));
        //System.out.println();
        //System.out.println(hyd_id.length);
        //System.out.print(Arrays.toString(hyd_ordinates));
        //System.out.println();
        //System.out.println(hyd_ordinates.length);
     
        //Processing third file - firebuildings.txt
        System.out.println("Parsing the third file - firebuilding.txt");
        System.out.println();
        
        br3 = new BufferedReader(new FileReader(filename3));
        int i=0;

        while ((line = br3.readLine()) != null) {
          if (br3.readLine() != " ") {
          String[] values = line.split(" ");
          temp7 = values[0];
          A7.add(" " + temp7);
          }
        }
        buidings_on_fire = A7.toArray(new String[A7.size()]);
        //System.out.print(Arrays.toString(buidings_on_fire));
        //System.out.println();
        //System.out.println(buidings_on_fire.length);
        //System.out.println(buidings_on_fire[1]);
        //System.out.println(buidings_on_fire[2]);
        //System.out.println(buidings_on_fire[3]);
        
       }

      
     
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    finally {
      try {
        if (br1 != null)
          br1.close();
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    
  }
}
