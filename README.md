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

6. Install Required Features
	* feature:repo-add activemq
 

