rm -rf singlesingle/
rm -f singlesingle.txt
#jmeter -n -t SingleThreadSingleInstance.jmx -l singlesingle.txt -e -o singlesingle/
jmeter -n -t SingleThreadSingleInstance.jmx -e -o singlesingle/
rm -rf tensingle/
rm -f tensingle.txt
jmeter -n -t TenThreadSingleInstance.jmx -e -o tensingle/
jmeter -n -t HttpsTenThreadSingleInstance.jmx -l httpstensingle.txt
jmeter -n -t SingleThreadCluster.jmx -l singlecluster.txt
jmeter -n -t TenThreadCluster.jmx -l tencluster.txt
