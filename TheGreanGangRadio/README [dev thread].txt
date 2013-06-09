1. Server&Project must run on 32-bit JVM
2. add >>>>>  -Djava.library.path="<wpcom.dll directory>"   to JVM arguments(on server)



ERROR!!
java.lang.UnsatisfiedLinkError: Native Library ..............\wpcom.dll already loaded in another classloader 
FIX by restart server