RESUBMISSION
—————————————
Name: Kavya Gautam
SCU ID: W1166971
DB_name: db11g
DB_username: kgautam
db password: archishma

Files Included in HW3.zip:
-------------------------------------------------------------------------------------------------
createdb.sql  - creates 3 tables, 2 indexes and  1 metadata table
dropdb.sql  - drops all the tables, indexes and deletes metadata
populate.java  - reads from files on cmd prompt and inserts data into the tables in db
readme.txt
HW3.java
spatialData.java  (HW3.java uses methods and static variables from this)
——————————————————————————————————————————————————————————————————————————————————————————

compile and run paths:
-------------------------------------------------------------------------------------------------

1) The sampleUI.java - does not run in terminal as numerous binding variables do not stand alone out of netbeans
   
The sampleSCU folder as a whole if opened as an existing project in netbeans 8.0 runs successssssfully
  -connects to database and retrieves information required to populate GUI

The GUI has the following required features:
Whole area in combination with checkboxes draws all necessary objects on image
Range area clicked  - lets user draw a polygon and SDO_Filter returns the buildings and draws 


the sampleSCU.jar is located in sampleSCU - dist folder and can be launched with jar launcher.


2) populate.java
   -------------
compile: javac -classpath .:ojdbc7.jar:sdoapi.jar populate.java

run:     java -classpath .:ojdbc7.jar:sdoapi.jar populate building.xy hydrant.xy firebuilding.txt






