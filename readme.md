# GemFire Function Deadlock Example

First don't use this it will cause a functions to lock up and will cause threads on the server to be used and potentially run the server out of resources.   

Why would this even be created?  Folks wanted to see the output of the gfsh command `show dead-locks --file=deadlocks.txt`


There are currently two examples of typical issues with integrating with GemFire.   

1. CacheWriter - while attempting to write to some backing store it gets overwhelmed by the high transactional load that GemFire applications can place on lesser databases.  
2. Function - The realm of what developer can achieve in a function is insurmountable.  That can be calling out another service, loading GB of data, just about any usecase.

How to run:
1. In the project directory type `./gradlew jar` this will create the code that needs to be deployed on the servers
2. Start up GemFire by using the script `start_gemfire.sh` in the `scripts` directory.
3. Then using an IDE run the Main.   It will start up two threads which will cause one thread to block.  The main will block until a key is pressed and will unlock and exit

Feel free to change what a `CacheWriter` example would look like.

This doesn't cause the show deadlock to report it found one.   This is because the show deadlock command is using the Java deadlock detection.   So one could easily alter the code to show a traditional java deadlock.

To clean up run the 'cleanup.sh' command in the scripts directory.