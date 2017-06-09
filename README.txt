IP Addresses
------------------------------------------------
instance 1 (load balancer): 34.201.9.40
instance 2 (master): 34.206.54.116
instance 3 (slave): 54.236.62.19

google instance: 35.197.13.226

Averaging Log File
------------------------------------------------
avgLog.py will return the average of all the numbers
in either the ts.txt or tj.txt file. It just has
to be given the file name as an argument. The data files
it is used on can be found within the gamesstation_webapp.war
under the results/ folder.

    python avgLog.py path/to/ts.txt

JMeter test file
------------------------------------------------
The JMeter plans are within the war file. Additionally,
the file it pulls the get parameters from is the included
queryList.txt file.