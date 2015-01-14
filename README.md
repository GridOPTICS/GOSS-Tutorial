## GOSS-Tutorial Instructions

Updated 1/14/2015

For the following instructions $ROOT will be ~/git in linux, %USERPROFILE%\git on windows.  For the rest of the tutorial / will be used as the default path separator.  This tutorial assumes that $ROOT exists.  The [] brackets are used when a file extension is necessary for windows .bat files.

Requirement:
* Git (http://git-scm.com/download)
* Maven 3.0.5 (http://maven.apache.org/docs/3.0.5/release-notes.html)
* JDK 1.7 (http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)

Steps:

1. Open a Terminal (cmd) window and change the working directory to $ROOT
	* git clone https://github.com/GridOPTICS/GOSS-Tutorial.git

2. Build GOSS-Tutorial
	* cd ../GOSS-Tutorial
	* gradlew install
	* mvn clean install
	
3. Start Karaf
	* cd ../GOSS-Tutorial
	* ./start-karaf[.bat]
	
4. Register GOSS Tutorial in Karaf
	* feature:install goss-tutorial-feature 

5. Open browser to http://localhost:8181/tutorial/index.html

6. Click 'Start PMU Stream'

7. Click 'Start Aggregator'
 

