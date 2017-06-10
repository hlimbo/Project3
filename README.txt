WAR file deployment
------------------------------------------------------------
Before beginning, rename your previous ROOT to something
else to keep your original data. Afterwards, take the war 
file and rename it from gamesstation_webapp.war to ROOT.war 
and drop it into the webapps folder of your tomcat 
installation. When the tomcat server is running, it should
automatically create the ROOT folder after a little bit of 
time.

IP Addresses
------------------------------------------------
instance 1 (load balancer): 34.201.9.40
** Note: instance 1 both supports http and https protocol.

http protocol: http://34.201.9.40
https protocol: https://34.201.9.40:8443

instance 2 (master): 34.206.54.116
instance 3 (slave): 54.236.62.19

google instance: 35.197.13.226

Averaging Log File
------------------------------------------------
avgLog.py will return the average of all the numbers
in either the ts.txt or tj.txt file. It just has
to be given the file name as an argument. The data files
it is used on can be found within the gamesstation_webapp.war
under the results/ folder. Alternatively, the results/
folder has been included here so that the jmeter_report.html
file is able to find the images it needs to include.

    python avgLog.py path/to/ts.txt

JMeter test file
------------------------------------------------
The JMeter plans are within the war file. Additionally,
the file it pulls the get parameters from is the included
queryList.txt file.

Example customer and credit card for website demonstration
------------------------------------------------------------

For website login:

email:   einti@ntag.edu 
password: snZtoXfqs4Fpe0C9Op53

Alternatively, an easier dummy user can be used for login

email:   user@user.edu 
password: password

For confirmation of purchase:

First Name: Tina
Last Name: Watson
Credit Card Number: 0000574018613373  
Expiration Date: 2018-11-12

For Further Information
------------------------------------------------------------
There is a more detailed README.txt within the 
gamesstation_webapp.war file