1. Server&Project must run on 32-bit JVM
2. add >>>>>  -Djava.library.path="<wpcom.dll directory>"   to JVM arguments(on server)
3. to know Log4j Properties 
	- create "jboss-deployment-structure.xml" for using log4j own instance 
	- export into .jar then Add to Build Path to build path
		or, copy log4j.properties to WEB-INF/classes/<place-here>   


ERROR!!
java.lang.UnsatisfiedLinkError: Native Library ..............\wpcom.dll already loaded in another classloader 
FIX by restart server