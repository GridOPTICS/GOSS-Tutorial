## GOSS-Tutorial Instructions

#### Presented at the _3rd Workshop on Next-Generation Analytics for the Future Power Grid_ July 16th 2014

For the following instructions $ROOT will be ~/git in linux, %USERPROFILE%\git on windows.  For the rest of the tutorial / will be used as the default path separator.  This tutorial assumes that $ROOT exists.  The [] brackets are used when a file extension is necessary for windows .bat files.

1. Open a Terminal (cmd) window and change the working directory to $ROOT
	* git clone https://github.com/GridOPTICS/GOSS-Tutorial.git
	* git clone https://github.com/GridOPTICS/GOSS.git

2. Modify the goss.properties file in $ROOT/GOSS the root of the GOSS repository to fit your connection criteria.
	* Currently this file has non-sensitive password and connection data.

3. Copy $ROOT/goss.properties file to ~/.goss/goss.properties
	* execute $ROOT/copy-props[.bat]

4. Build GOSS and GOSS-Tutorial via maven
	* cd GOSS
	* mvn clean install
	* cd ../GOSS-Tutorial
	* mvn clean install
	
5. Start Karaf (with GOSS core included)
	* cd ../GOSS/goss-karaf-assembly
	* mvn clean compile
	* ./start-karaf[.bat]
	
6. Register GOSS Tutorial
	* install -s wrap:mvn:net.sf.py4j/py4j/0.8.1
	* feature:repo-add mvn:pnnl.goss.tutorial/tutorial-pmu-features/0.0.2-SNAPSHOT/xml/feature
	* feature:install tutorial-pmu-feature 

7. Open browser to http://localhost:8181/pmu-tutorial/index.html
8. Click 'Start PMU Stream'
9. Click 'Start Aggregator'
 

