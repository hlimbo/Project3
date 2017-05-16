WAR file deployment
------------------------------------------------------------
Before beginning, rename your previous ROOT to something
else to keep your original data. Afterwards, take the war 
file and rename it from ccopelan_project2.war to ROOT.war 
and drop it into the webapps folder of your tomcat 
installation. When the tomcat server is running, it should
automatically create the ROOT folder after a little bit of 
time.

Compilation
------------------------------------------------------------
Not required, but if one wishes to compile the class
files of this application, all the java files are
within the WEB-INF/sources folder. The expected output
directory for the class files is the WEB-INF/classes
folder.

Commands:
    cd WEB-INF
    javac -d classes -cp lib/servlet-api.jar:lib/mysql-connector-java-5.1.41-bin.jar:lib/servlet-api.jar sources/*.java

Expected MySQL user and password
------------------------------------------------------------
The program expects a user called "user" but without the
quotation marks. This user's password is "password" but
without the quotation marks. This user should have 
SELECT,INSERT, UPDATE, and DROP privileges on the gamedb


Access local website
------------------------------------------------------------
Go to localhost:8080/ on the machine with the tomcat running
after the ROOT folder was created by tomcat.
"See WAR file deployment" for directions on installing the
WAR file.

Access aws instance
------------------------------------------------------------
At the time of this writing, the ip address and port of the
aws instance is https://34.201.42.202:8443/

Example customer and credit card for website demonstration
------------------------------------------------------------

For website login:

email:   einti@ntag.edu 
password: snZtoXfqs4Fpe0C9Op53

For confirmation of purchase:

First Name: Tina
Last Name: Watson
Credit Card Number: 0000574018613373  
Expiration Date: 2018-11-12

Special Characters in Search
------------------------------------------------------------
The search engine uses SQL LIKE in the backend. As such,
you able to use '%' like a wildcard to match any amount
of characters at where the '%' is. To match only one
character of any character, '_' can be used instead.
As example, both M_rio and M%io will match Mario.



Compiling XML Parsers
------------------------------------------------------------

Running XML Parsers
------------------------------------------------------------
