##### setup first #####
1. Server&Project must run on 32-bit JVM
 	- add System variables >>> JAVA_HOME_THE_GREAN_GANG to your java 32bit
2. to know wpcom.dll
	- copy wpcom.dll to  <jboss root>\bin 
		or, add >>>>>  -Djava.library.path="<wpcom.dll directory>" to JVM arguments(on server)
3. goto C:\Windows\System32\drivers\etc\hosts then add following host to avoid plugins request to yp.shoutcast.com
	127.0.0.1	yp.shoutcast.com	
	
	
##### log4j ##### 
1. to know Log4j Properties 
	- create "jboss-deployment-structure.xml" for using log4j own instance 
	- export into .jar then Add to Build Path to build path
		or, copy log4j.properties to WEB-INF/classes/<place-here>   

##### PushServlet #####
1. atmosphere-runtime-1.1.0.RC3.jar required!
2. don't forget to map web.xml for /primepush

##### primeface #####
1. use primefaces-4.0 for avoid bug of primeface-3.5 in case filterMatchMode="contains" NOT WORKING! 
 
ERROR!!
java.lang.UnsatisfiedLinkError: Native Library ..............\wpcom.dll already loaded in another classloader 
FIX by restart server


Develop on Eclipse Kepler, JBoss AS 7.1

