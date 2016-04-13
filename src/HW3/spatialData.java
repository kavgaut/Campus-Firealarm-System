/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.*;

import oracle.spatial.geometry.JGeometry;

import java.util.ArrayList;
import java.util.Arrays;

import oracle.jdbc.OracleResultSet;

/**
 *
 * @author kavyagautam
 */
public class spatialData {
    
    public static Double[] fh_x, fh_y;
    public static int[] buildings_l;
    public static Double[] buildings_x, buildings_y;
    public static int[] bof_l;
    public static Double[] bof_x, bof_y; 
    public static Double[] FHRB_x, FHRB_y;
    public static int FHRB_l;
    public static Double[] closestfh_x;
    public static Double[] closestfh_y;
    public static String query;
    
    public static ArrayList<String> QueryDisplayArray;

    ResultSet HydrantsResultSet;
    ResultSet BuildingsResultSet;
    ResultSet FireBuildsResultSet;

    public spatialData() {
        this.QueryDisplayArray = new ArrayList<>();
        this.FireBuildsResultSet = null;
        this.HydrantsResultSet = null;
        this.BuildingsResultSet = null;
        this.fh_x = null;
        this.fh_y= null;
        this.buildings_l = new int[36];
        this.buildings_x = null;
        this.buildings_y = null;
        this.FHRB_l = 1;
        this.FHRB_x = null;
        this.FHRB_y = null;
        this.closestfh_x = null;
        this.closestfh_y = null;
        this.bof_l = new int[3];
        this.bof_x = null;
        this.bof_y = null; 
        this.query="";
    }
   
    public void processQueries(Boolean cb_buildings, Boolean cb_fh, Boolean cb_bof, 
                               Boolean rb_wholearea, Boolean rb_rangearea, Boolean rb_NN, Boolean rb_fh, 
                               ArrayList<Integer> rp_x, ArrayList<Integer> rp_y, int rb_fh_click_x, int rb_fh_click_y) throws SQLException, Exception
    
    {
         
        //Beofre staring with Business Logic, we need to connect to database
        //ESTABLISH CONNECTION WITH DATABASE
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
        String uname = "kgautam";      
        String pwd = "archishma";
        
       
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
  
        //NOW COMES THE BUSINESS LOGIC
            
         for (int i = 0; i<36;i++)
         {buildings_l[i] = 0;}
         for (int i = 0; i<3;i++)
         {bof_l[i] = 0;}
        
        String RangeQuery = "";
        String NeighborQuery = "";
        String ClosestHyd = "";
        
        Statement WR = connection.createStatement();
        Statement RQ = connection.createStatement();
        Statement NN = connection.createStatement();
        Statement CFH = connection.createStatement();
        
        
        //FOR THE WHOLE AREA RADIO BUTTON
        if (rb_wholearea){
            QueryDisplayArray.clear();
            //System.out.println("Entered the RB WHOLEAREA");
            
        //WHOLE REGION RADIO BUTTON + BUILDINGS CHECKBOX COMBINATION 
        //==> RETRIEVE WHOLE AREA RESULTSET FOR BUILDINGS, SEPERATE Xs AND YS 
        //==>THEN UPDATE THE buildings_x, buildings_y and buildings_l lists
            
            if (cb_buildings){
                //System.out.println("Entered the CB BUILDINGS");
                BuildingsResultSet = WR.executeQuery("SELECT BLD_GEOM FROM BUILDING_INFO");
                Double x = null;
                Double y = null;
                ArrayList<Double> A1 = new ArrayList<>();
                ArrayList<Double> A2 = new ArrayList<>();
                try 
                {
                    int l =0;
                    while( BuildingsResultSet.next() )
                    {
                         byte[] image = ((OracleResultSet)BuildingsResultSet).getBytes(1);
                        //convert image into a JGeometry object using the SDO pickler
                        JGeometry geom = JGeometry.load(image);
                        buildings_l[l] = (geom.getOrdinatesArray().length)/2;
                        l++;
                
                        for (int i=0; i<(geom.getOrdinatesArray().length);i=i+2)
                        {
                            x = geom.getOrdinatesArray()[i];
                            y= geom.getOrdinatesArray()[i+1];
                            A1.add(x);
                            A2.add(y);
                        }
                    }
                    
                    buildings_x = A1.toArray(new Double[A1.size()]);
                    buildings_y = A2.toArray(new Double[A2.size()]);
    
                    QueryDisplayArray.add("SELECT BLD_GEOM FROM BUILDING_INFO");
                    A1.clear();
                    A2.clear();
            
                }
                catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                } 
                
            }
        
        //WHOLE REGION RADIO BUTTON + HYDRANTS CHECKBOX COMBINATION 
        //==> RETRIEVE WHOLE AREA RESULTSET FOR FIREHYDRANTS, SEPERATE Xs AND YS
        //==> THEN UPDATE ONLY THE 'fh_x' AND 'fh_y' LISTS
            
            if (cb_fh){
                //System.out.println("Entered the CB FIRE HYDRANTS");
                HydrantsResultSet = WR.executeQuery("SELECT FH_GEOM FROM FIREHYDRANTS_INFO");
                Double x = null;
                Double y = null;
                ArrayList<Double> A1 = new ArrayList<>();
                ArrayList<Double> A2 = new ArrayList<>();
                try
                {
                    while( HydrantsResultSet.next() )
                    {
                
                        byte[] image = ((OracleResultSet)HydrantsResultSet).getBytes(1);
                        JGeometry geom = JGeometry.load(image);
                        x = geom.getPoint()[0];
                        y = geom.getPoint()[1];
                        A1.add(x);
                        A2.add(y);
                    }   
                    fh_x = A1.toArray(new Double[A1.size()]);
                    fh_y = A2.toArray(new Double[A2.size()]);
                    
                    A1.clear();
                    A2.clear();
                    QueryDisplayArray.add("SELECT FH_GEOM FROM FIREHYDRANTS_INFO");
                }
                catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                }
                
            }
        //WHOLE REGION RADIO BUTTON + FIREBULDINGS CHECKBOX COMBINATION
        //==> RETRIEVE WHOLE AREA RESULTSET FOR BUILDINGS ON FIRE, SEPERATE Xs AND YS 
        //==>THEN UPDATE THE 'bof_x','bof_y' AND 'bof_l' LISTS
         
            if (cb_bof)
          
            {
                //System.out.println("Entered the CB BUILDINGS ON FIRE");
                FireBuildsResultSet = WR.executeQuery("SELECT BLD_GEOM FROM BUILDING_INFO B, BLD_ON_FIRE F WHERE B.BLD_NAME = F.BLD_NAME");
                Double x = null;
                Double y = null;
                ArrayList<Double> A1 = new ArrayList<>();
                ArrayList<Double> A2 = new ArrayList<>();
                try 
                {
                    int l =0;
                    while( FireBuildsResultSet.next() )
                    {
                         byte[] image = ((OracleResultSet)FireBuildsResultSet).getBytes(1);
                        //convert image into a JGeometry object using the SDO pickler
                        JGeometry geom = JGeometry.load(image);
                        bof_l[l] = (geom.getOrdinatesArray().length)/2;
                        l++;
                        for (int i=0; i<(geom.getOrdinatesArray().length);i=i+2)
                        {
                            x = geom.getOrdinatesArray()[i];
                            y= geom.getOrdinatesArray()[i+1];
                            A1.add(x);
                            A2.add(y);
                        }
                    }
                    
                    bof_x = A1.toArray(new Double[A1.size()]);
                    bof_y = A2.toArray(new Double[A2.size()]);
            
                    A1.clear();
                    A2.clear();
                    QueryDisplayArray.add("SELECT BLD_GEOM FROM BUILDING_INFO B, BLD_ON_FIRE F WHERE B.BLD_NAME = F.BLD_NAME");
  
                }
                catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                } 
            
            
            }

        }
        
        //FOR THE RANGE QUERY RADIO BUTTON
        if (rb_rangearea){
            System.out.println("Entered the RB RANGEAREA");
            QueryDisplayArray.clear();
            
            //The complete list of coords as a string (polygon)
            int l = rp_x.size();    
            String coords = ""; 
            for (int i = 0; i < l; i++){
                coords = coords + String.valueOf(rp_x.get(i)) + ", " + String.valueOf(rp_y.get(i)) + ", " ;
                //System.out.println(coords);  
            }  
            coords = coords + "" + rp_x.get(0) + ", " + rp_y.get(0);
            //System.out.println("The coordinates string:" + coords);
            
            if (cb_fh){
                System.out.println("Entered the CB FIRE HYDRANTS");
                
                HydrantsResultSet = RQ.executeQuery("SELECT F.FH_GEOM FROM FIREHYDRANTS_INFO F where SDO_ANYINTERACT(F.FH_GEOM, SDO_GEOMETRY(" + 2003 + ",NULL,NULL,SDO_ELEM_INFO_ARRAY(" + 1 + "," + 1003 + "," + 1 + "),SDO_ORDINATE_ARRAY(" + coords + ")))" +  "= 'TRUE'");
                Double x = null;
                Double y = null;
                ArrayList<Double> A1 = new ArrayList<>();
                ArrayList<Double> A2 = new ArrayList<>();
                try
                {
                    while( HydrantsResultSet.next() )
                    {
                
                        byte[] image = ((OracleResultSet)HydrantsResultSet).getBytes(1);
                        JGeometry geom = JGeometry.load(image);
                        x = geom.getPoint()[0];
                        y = geom.getPoint()[1];
                        A1.add(x);
                        A2.add(y);
                    }   
                    fh_x = A1.toArray(new Double[A1.size()]);
                    fh_y = A2.toArray(new Double[A2.size()]);
                    
                    A1.clear();
                    A2.clear();
                    QueryDisplayArray.add("SELECT F.FH_GEOM FROM FIREHYDRANTS_INFO F where SDO_INSIDE(F.FH_GEOM, SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + coords + ")))= 'TRUE'");
                }
                catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                }
                
            }
                
            //RANGE AREA POLYGON RADIO BUTTON + BUILDINGS CHECKBOX COMBINATION 
            //==> RETRIEVE RANGE POLYGON AREA RESULTSET FOR BUILDINGS, SEPERATE Xs AND YS 
            //==>THEN UPDATE THE 'buildings_x','buildings_y' AND 'buildings_l' LISTS
            if (cb_buildings){
                System.out.println("Entered the CB BUILDINGS");
                BuildingsResultSet = RQ.executeQuery("SELECT B.BLD_GEOM FROM BUILDING_INFO B WHERE SDO_ANYINTERACT(B.BLD_GEOM,SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 1),SDO_ORDINATE_ARRAY(" + coords +"))) = 'TRUE'");
                Double x = null;
                Double y = null;
                ArrayList<Double> A1 = new ArrayList<>();
                ArrayList<Double> A2 = new ArrayList<>();
                
                try 
                {
                    while( BuildingsResultSet.next() )
                    {
                         byte[] image = ((OracleResultSet)BuildingsResultSet).getBytes(1);
                        //convert image into a JGeometry object using the SDO pickler
                        JGeometry geom = JGeometry.load(image);
                        buildings_l[l] = (geom.getOrdinatesArray().length)/2;
                        l++;
                
                        for (int i=0; i<(geom.getOrdinatesArray().length);i=i+2)
                        {
                            x = geom.getOrdinatesArray()[i];
                            y = geom.getOrdinatesArray()[i+1];
                            A1.add(x);
                            A2.add(y);
                        }
                    }
                    
          
                    buildings_x = A1.toArray(new Double[A1.size()]);
                    buildings_y = A2.toArray(new Double[A2.size()]);

           
                    System.out.print(Arrays.toString(buildings_y));
                    System.out.println();
                    A1.clear();
                    A2.clear();
                    QueryDisplayArray.add("SELECT B.BLD_GEOM FROM BUILDING_INFO B where SDO_ANYINTERACT(B.BLD_GEOM, SDO_GEOMETRY(" + 2003 + ",NULL,NULL,SDO_ELEM_INFO_ARRAY(" + 1 + "," + 1003 + "," + 1 + "),SDO_ORDINATE_ARRAY(" + coords + ")))" +  "= 'TRUE'");
            
                }
                
            catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                } 
                
            }
            
            if(cb_bof){
                FireBuildsResultSet = RQ.executeQuery("SELECT B.BLD_GEOM, B.BLD_NAME FROM BUILDING_INFO B, BLD_ON_FIRE F where B.BLD_NAME = F.BLD_NAME AND SDO_ANYINTERACT(B.BLD_GEOM, SDO_GEOMETRY(2003 ,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + coords + ")))='TRUE'");
                Double x = null;
                Double y = null;
                ArrayList<Double> A1 = new ArrayList<>();
                ArrayList<Double> A2 = new ArrayList<>();
                
                try 
                {
                    l =0;
                    while( FireBuildsResultSet.next() )
                    {
                         byte[] image = ((OracleResultSet)FireBuildsResultSet).getBytes(1);
                        //convert image into a JGeometry object using the SDO pickler
                        JGeometry geom = JGeometry.load(image);
                        bof_l[l] = (geom.getOrdinatesArray().length)/2;
                        l++;
                
                        for (int i=0; i<(geom.getOrdinatesArray().length);i=i+2)
                        {
                            x = geom.getOrdinatesArray()[i];
                            y = geom.getOrdinatesArray()[i+1];
                            A1.add(x);
                            A2.add(y);
                        }
                    }
                    
                    bof_x = A1.toArray(new Double[A1.size()]);
                    bof_y = A2.toArray(new Double[A2.size()]);
            
                    System.out.print(Arrays.toString(bof_y));
                    System.out.println();
                    A1.clear();
                    A2.clear();
                    QueryDisplayArray.add("SELECT B.BLD_GEOM FROM BUILDING_INFO B, BLD_ON_FIRE F where B.BLD_NAME = F.BLD_NAME AND SDO_ANYINTERACT(B.BLD_GEOM, SDO_GEOMETRY(2003 ,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + coords + ")))= 'TRUE'");
            
                }
                
            catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                } 
            }

 
        }
        
        if (rb_NN){
                QueryDisplayArray.clear();
                
                BuildingsResultSet = NN.executeQuery("(SELECT B.BLD_GEOM FROM BUILDING_INFO B WHERE (SDO_WITHIN_DISTANCE(B.BLD_GEOM,SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1, 1003, 1),SDO_ORDINATE_ARRAY(226, 150, 254, 164, 240, 191, 212, 176, 226, 150)),'DISTANCE = 100') = 'TRUE')) UNION ALL (SELECT B.BLD_GEOM FROM BUILDING_INFO B WHERE (SDO_WITHIN_DISTANCE(B.BLD_GEOM,SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1, 1003, 1),SDO_ORDINATE_ARRAY(564, 425, 585, 436, 573, 458, 552, 447, 564, 425)),'DISTANCE = 100') = 'TRUE')) UNION ALL (SELECT B.BLD_GEOM FROM BUILDING_INFO B WHERE (SDO_WITHIN_DISTANCE(B.BLD_GEOM,SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1, 1003, 1),SDO_ORDINATE_ARRAY(677, 320, 708, 337, 698, 368, 661, 351, 677, 320)),'DISTANCE = 100') = 'TRUE'))");
                
                Double x = null;
                Double y = null;
                ArrayList<Double> A1 = new ArrayList<>();
                ArrayList<Double> A2 = new ArrayList<>();
                
                try 
                {
                    int l =0;
                    
                    while( BuildingsResultSet.next() )
                    {
                         byte[] image = ((OracleResultSet)BuildingsResultSet).getBytes(1);
                        //convert image into a JGeometry object using the SDO pickler
                        JGeometry geom = JGeometry.load(image);
                        buildings_l[l] = (geom.getOrdinatesArray().length)/2;
                        l++;
                
                        for (int i=0; i<(geom.getOrdinatesArray().length);i=i+2)
                        {
                            x = geom.getOrdinatesArray()[i];
                            y = geom.getOrdinatesArray()[i+1];
                            A1.add(x);
                            A2.add(y);
                        }
                    }
                   
          
                    buildings_x = A1.toArray(new Double[A1.size()]);
                    buildings_y = A2.toArray(new Double[A2.size()]);
            
                    A1.clear();
                    A2.clear();
                    
                    QueryDisplayArray.add("(SELECT B.BLD_GEOM FROM BUILDING_INFO B WHERE (SDO_WITHIN_DISTANCE(B.BLD_GEOM,SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1, 1003, 1),SDO_ORDINATE_ARRAY(226, 150, 254, 164, 240, 191, 212, 176, 226, 150)),'DISTANCE = 100') = 'TRUE')) UNION ALL (SELECT B.BLD_GEOM FROM BUILDING_INFO B WHERE (SDO_WITHIN_DISTANCE(B.BLD_GEOM,SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1, 1003, 1),SDO_ORDINATE_ARRAY(564, 425, 585, 436, 573, 458, 552, 447, 564, 425)),'DISTANCE = 100') = 'TRUE')) UNION ALL (SELECT B.BLD_GEOM FROM BUILDING_INFO B WHERE (SDO_WITHIN_DISTANCE(B.BLD_GEOM,SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1, 1003, 1),SDO_ORDINATE_ARRAY(677, 320, 708, 337, 698, 368, 661, 351, 677, 320)),'DISTANCE = 100') = 'TRUE'))");
            }
                catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                }
                FireBuildsResultSet = WR.executeQuery("SELECT BLD_GEOM FROM BUILDING_INFO B, BLD_ON_FIRE F WHERE B.BLD_NAME = F.BLD_NAME");
                x = null;
                y = null;
                
                try 
                {
                    int l =0;
                    while( FireBuildsResultSet.next() )
                    {
                         byte[] image = ((OracleResultSet)FireBuildsResultSet).getBytes(1);
                        //convert image into a JGeometry object using the SDO pickler
                        JGeometry geom = JGeometry.load(image);
                        bof_l[l] = (geom.getOrdinatesArray().length)/2;
                        l++;
                        for (int i=0; i<(geom.getOrdinatesArray().length);i=i+2)
                        {
                            x = geom.getOrdinatesArray()[i];
                            y= geom.getOrdinatesArray()[i+1];
                            A1.add(x);
                            A2.add(y);
                        }
                    }
                 
                    bof_x = A1.toArray(new Double[A1.size()]);
                    bof_y = A2.toArray(new Double[A2.size()]);

                    A1.clear();
                    A2.clear();
                    //QueryDisplayArray.add("SELECT BLD_GEOM FROM BUILDING_INFO B, BLD_ON_FIRE F WHERE B.BLD_NAME = F.BLD_NAME");
                    
                    
                }
                catch( Exception e )
                { 
                    System.out.println(" Error : " + e.toString() ); 
                }

        }
        
        if (rb_fh) {
            //First need to update Building_x and Building_y lists from whole area query
            //then using the new building list from GUI class passed here to check closest Hydrants from a query
            QueryDisplayArray.clear();
            rb_fh_query_process (rb_fh_click_x, rb_fh_click_y, connection);
        }
        
      /**
       * Close the database connection
       */
        try 
            { 
               connection.close(); 
            } 
            catch (SQLException e) { 
               System.err.println("Cannot close connection: " + e.getMessage()); 
            }
            System.out.println();
            System.out.println("!!!Database connection closed succesfully!!!");
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }   
    }
    
    //This method takes the clicked point dynamically from image click event and queries the database for the  building that contains the
    //point and also the 2 closest fire hydrants of the guilding and updates 5 parameters accordingly FHRB_l, x, y and closestfh_x and y
    private void rb_fh_query_process (int rb_fh_click_x, int rb_fh_click_y, Connection con) throws SQLException {
        FHRB_x = null;
        FHRB_y = null;
        FHRB_l = 0;
        closestfh_x = null;
        closestfh_y = null;
        Statement st1 = con.createStatement();
        Statement st2 = con.createStatement();
        ResultSet brs = st1.executeQuery("select b.bld_geom from building_info b where sdo_contains(b.bld_geom,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + rb_fh_click_x + "," + rb_fh_click_y + ",NULL),NULL,NULL))='TRUE'");
        ResultSet fhrs = st2.executeQuery("select f.FH_GEOM from firehydrants_info f where SDO_NN(f.fh_geom, (select b.bld_geom from building_info b where sdo_contains(b.bld_geom,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + rb_fh_click_x + "," + rb_fh_click_y + ",NULL),NULL,NULL))='TRUE'), 'sdo_num_res=2') = 'TRUE'");
        Double a = null;
        Double b = null;
        ArrayList<Double> a1 = new ArrayList<>();
        ArrayList<Double> a2 = new ArrayList<>();
        try 
        {
            
            while( brs.next() )
            {
            byte[] image = ((OracleResultSet)brs).getBytes(1);
            //convert image into a JGeometry object using the SDO pickler
            JGeometry geom = JGeometry.load(image);
            FHRB_l = (geom.getOrdinatesArray().length)/2;
            
            for (int i=0; i<(geom.getOrdinatesArray().length);i=i+2)
                {
                    a = geom.getOrdinatesArray()[i];
                    b= geom.getOrdinatesArray()[i+1];
                    a1.add(a);
                    a2.add(b);
                }
            }
           
            FHRB_x = a1.toArray(new Double[a1.size()]);
            FHRB_y = a2.toArray(new Double[a2.size()]);
    
            a1.clear();
            a2.clear();
            
            }
            catch( Exception e )
            { 
                System.out.println(" Error : " + e.toString() ); 
            } 
        try 
        {
            
            while( fhrs.next() )
            {
            byte[] image = ((OracleResultSet)fhrs).getBytes(1);
            //convert image into a JGeometry object using the SDO pickler
            JGeometry geom = JGeometry.load(image);
         
                a = geom.getPoint()[0];
                b = geom.getPoint()[1];
                a1.add(a);
                a2.add(b);   
            }
           
            closestfh_x = a1.toArray(new Double[a1.size()]);
            closestfh_y = a2.toArray(new Double[a2.size()]);
           
            a1.clear();
            a2.clear();
            QueryDisplayArray.add("select f.FH_GEOM from firehydrants_info f where SDO_NN(f.fh_geom, (select b.bld_geom from building_info b where sdo_contains(b.bld_geom,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + rb_fh_click_x + "," + rb_fh_click_y + ",NULL),NULL,NULL))='TRUE'), 'sdo_num_res=2') = 'TRUE'");
            
            }
            catch( Exception e )
            { 
                System.out.println(" Error : " + e.toString() ); 
            }


        }       
    
   /**
    * Meta data result set
    */
    private void showMetaDataOfResultSet(ResultSet result) throws SQLException {
        ResultSetMetaData meta = result.getMetaData();
           for (int col = 1; col <= meta.getColumnCount(); col++) {
               System.out.println("Column: " + meta.getColumnName(col) +
                                  ",\t Type: " + meta.getColumnTypeName(col));
           }
        System.out.println();
    }
    
    public static void main (String[] args) throws Exception{
        spatialData sd = new spatialData ();

    }
    
}
