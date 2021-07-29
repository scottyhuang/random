## random
Implement a clone of $ cat /dev/random

Uses JVM heap memory usage and System clock as entropy sources to generate seed for Java SecureRandom's cryptographically strong RNG.
 
Backup OpenSSL JNI implementation uses native chipset hardware random number generator if available.   

Human generated and other entropy sources were considered but not used.

### Background
- [Intel® Digital Random Number Generator (DRNG) Software Implementation Guide](https://software.intel.com/content/www/us/en/develop/articles/intel-digital-random-number-generator-drng-software-implementation-guide.html)  
- [Apache Commons Crypto](https://commons.apache.org/proper/commons-crypto/userguide.html)  

### Build Instructions
1. get the source: 
    git clone git@github.com:https://github.com/scottyhuang/random.git
2. from the random/ directory, perform maven build: mvn clean install

### Usage
* $ mvn exec:java -Dexec.mainClass="com.scotth.random.DevRandom"
  - generate file ./devrandom containing a stream of random bytes, use ctrl-c to terminate

* $ mvn exec:java -Dexec.mainClass="com.scotth.random.DevRandom" -Dexec.args="-f rand-test -s 128"
  - generate file with name specified by the -f command line option and with number of bytes speficed by the -s command line option
