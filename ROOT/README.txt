WAR file deployment
------------------------------------------------------------
Before beginning, rename your previous ROOT to something
else to keep your original data. Afterwards, take the war 
file and rename it from gamesstation_webapp.war to ROOT.war 
and drop it into the webapps folder of your tomcat 
installation. When the tomcat server is running, it should
automatically create the ROOT folder after a little bit of 
time.

add_game Procedure
------------------------------------------------------------
The add_game procedure can be added to your sql database
by sourcing the add_game.sql file within the data folder.

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

Compilation for XML Parsers
------------------------------------------------------------
    using the bash run script to run java class files:
	run xml.CompaniesParser [absolute file name]/companies.xml
	e.g.
	run xml.CompaniesParser /c/Users/Harvey/Desktop/UCIHWK/Spring2017/CS122B/project1/apache-tomcat-8.5.13-windows-x64/apache-tomcat-8.5.13/webapps/XML_Parsing/newGames/companies.xml
	
	run xml.GamesParser [absolute file name]/newGames.xml
	e.g.
	run xml.GamesParser /c/Users/Harvey/Desktop/UCIHWK/Spring2017/CS122B/project1/apache-tomcat-8.5.13-windows-x64/apache-tomcat-8.5.13/webapps/XML_Parsing/newGames/newGames.xml

	run xml.PublishersParser [absolute file name]/pubs.xml
	e.g.
	run xml.PublishersParser /c/Users/Harvey/Desktop/UCIHWK/Spring2017/CS122B/project1/apache-tomcat-8.5.13-windows-x64/apache-tomcat-8.5.13/webapps/XML_Parsing/newGames/pubs.xml

Expected MySQL user and password
------------------------------------------------------------
The program expects a user called "user" but without the
quotation marks. This user's password is "password" but
without the quotation marks. This user should have 
SELECT,INSERT, UPDATE, and DROP on the gamedb. In addition
to those priviledges, the user is expected to have the
permission to create and call procedures in order to use
the dashboard.


Access local website
------------------------------------------------------------
Go to https://localhost:8443/ on the machine with the tomcat running
after the ROOT folder was created by tomcat.
"See WAR file deployment" for directions on installing the
WAR file.

Access aws instance
------------------------------------------------------------
At the time of this writing, the ip address and port of the
aws instance is https://34.200.221.197:8443/

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

Special Characters in Search
------------------------------------------------------------
The search engine uses SQL LIKE in the backend. As such,
you able to use '%' like a wildcard to match any amount
of characters at where the '%' is. To match only one
character of any character, '_' can be used instead.
As example, both M_rio and M%io will match Mario.

XML Optimization Report
------------------------------------------------------------
It can be found at the relative url /XML_tuning.txt
For example, if running on the localhost, it would be at
    localhost:8443/XML_tuning.txt

XML Files
------------------------------------------------------------
The XML files can be found in the newGames.zip file within
the data folder

SIMILIARTO UDF
------------------------------------------------------------
Install similiarto.so from the data/ folder into the
mysql plugin folder. Typically, this would be at
/usr/lib/mysql/plugin/libsimiliarto.so
Afterwards, run the similiarto.sql file to load the
SIMILIARTO function into mysql.

SIMILIARTO SEARCH
--------------------------------------------------------------
Does not apply to autocomplete searches. Similiarto query only executes when hitting the search button.
Searches are Case sensitive e.g. Dark Souls is not the same as dark souls
String distance option specifies how many characters a query needs to be off by to show a suggested search result.
e.g. Dbrk Souls should return Dark Souls in the search results page with a string distance of 1.

Android
------------------------------------------------------------
SQL Query to insert dummy user
USE gamedb;
INSERT INTO customers (cc_id,first_name,last_name,address,email,password) VALUES("0000574018613373","tester","mcteston","tester streets", "testee@email.com", "email");

Login for android app
+-------+------------------+------------+-----------+----------------+------------------+----------+
| id    | cc_id            | first_name | last_name | address        | email            | password |
+-------+------------------+------------+-----------+----------------+------------------+----------+
| 16293 | 0000574018613373 | tester     | mcteston  | tester streets | testee@email.com | email    |
+-------+------------------+------------+-----------+----------------+------------------+----------+

Passwords do not get saved when hitting the back button on the login page for security reasons

Averaging Log File
------------------------------------------------
avgLog.py will return the average of all the numbers
in either the ts.txt or tj.txt file. It just has
to be given the file name as an argument

    python avgLog.py path/to/ts.txt
