## GOSS-Tutorial Instructions

#### Presented at the _3rd Workshop on Next-Generation Analytics for the Future Power Grid_ July 16th 2014

1. Download and extract [Karaf 3.0.1](http://karaf.apache.org/index/community/download.html#Karaf3.0.1)  We will call the extraction directory KARAF_HOME for this tutorial.
2. Clone the GOSS and GOSS-Tutorial repositories
	* git clone https://github.com/GridOPTICS/GOSS-Tutorial.git
	* git clone https://github.com/GridOPTICS/GOSS.git
3. Create configuration file
 	* create a file call goss.properties under a .goss folder in your home directory.
 	* Add the following content to the goss.properties file:  
    goss.system.username=goss  
    goss.system.password=goss  
    goss.use.auth=true  
    accesscontrol.db.url=jdbc:mysql://localhost:3306/gridopticsdb  
	accesscontrol.db.user=root  
	accesscontrol.db.pw=CHANGEME  
	ldap.host=localhost  
	ldap.port=10389  
	ldap.connection.user=uid=admin,ou=system  
	ldap.connection.pw=CHANGEME  
	ldap.goss.base=ou=goss,ou=system  
	goss.broker.config=pnnl-goss-activemq-broker.xml  
	goss.broker.confignosec=pnnl-goss-activemq-broker-nosecurity.xml  
	goss.broker.name=GOSS-Activemq-Broker  
	goss.broker.data=data/goss-broker

4. Build GOSS and GOSS-Tutorial via maven
	* cd GOSS
	* mvn install
	* cd ../GOSS-Tutorial
	* mvn install
	
5. Start Karaf   
 	* cd KARAF_HOME
 	* on Linux
 	* on Linux bin/karaf
 	* on Windows bin/karaf.bat

6. Register and Install Required Features/Bundles
	* feature:repo-add activemq 5.9.0
	* feature:repo-add cxf 2.7.10
	* feature:repo-add http://repo1.maven.org/maven2/org/apache/felix/org.apache.felix.ipojo.features/1.12.0/org.apache.felix.ipojo.features-1.12.0.xml
	* feature:install ipojo-all activemq cxf
	* install -s wrap:mvn:net.sf.py4j/py4j/0.8.1

7. Register GOSS
	* feature:repo-add mvn:pnnl.goss/goss-core-feature/0.1.3-SNAPSHOT/xml/features
	* feature:repo-add mvn:pnnl.goss.tutorial/tutorial-pmu-features/0.0.1-SNAPSHOT/xml/feature
	* feature:install goss-core-feature tutorial-pmu-feature 

8. Open browser to http://localhost:8181/pmu-tutorial/index.html
9. Click 'Start PMU Stream'
10. Click 'Start Aggregator'
 

