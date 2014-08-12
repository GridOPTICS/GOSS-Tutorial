## GOSS-Tutorial Instructions

#### Presented at the _3rd Workshop on Next-Generation Analytics for the Future Power Grid_ July 16th 2014

1. Clone the GOSS and GOSS-Tutorial repositories
	* git clone https://github.com/GridOPTICS/GOSS-Tutorial.git
	* git clone https://github.com/GridOPTICS/GOSS.git
2. Create configuration file
 	* create a file call goss.properties under a .goss folder in your home directory.
 	* TODO UPDATE THE .properties
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

3. Build GOSS and GOSS-Tutorial via maven
	* cd GOSS
	* mvn install
	* cd ../GOSS-Tutorial
	* mvn install
	
4. Start Karaf (with GOSS core included)
	* cd ../GOSS/goss-karaf-assembly
	* mvn clean compile
	**TODO MAYBE COPY SOME CONFIG FILES AROUND?  like the logging one
	* start-karaf.bat
	
5. Register GOSS Tutorial
	* install -s wrap:mvn:net.sf.py4j/py4j/0.8.1
	* feature:repo-add mvn:pnnl.goss.tutorial/tutorial-pmu-features/0.0.1-SNAPSHOT/xml/feature
	* feature:install tutorial-pmu-feature 

6. Open browser to http://localhost:8181/pmu-tutorial/index.html
7. Click 'Start PMU Stream'
8. Click 'Start Aggregator'
 

