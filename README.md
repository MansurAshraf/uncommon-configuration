# Introduction
uncommon-configuration provides a simple and type safe API to read configuration data from various configuration sources such as properties files , YAML, JSON etc. It comes equipped with out of the box
support for most boiler plate tasks that are typically performed on a configuration file.

###Features include

+ Type safe conversion to most common data types.

+ Automatic polling and reloading of configuration file.

+ Encryption and decryption using both symmetric and asymmetric keys.

+ Support for nested configuration.

###Examples

           #sample properties file
           passwordExpiration=02/23/2012
           development.accounts=/home/bob,/home/andy,/home/jeff
           password=TjmYtjoeHUh0ryd9UOdltv #encrypted password

#####Reading from a properties file

           final Date passwordExpiration = configuration.get(Date.class, "passwordExpiration");

#####Reading a List from a properties file

           final List<String> result = configuration.getList(String.class, "development.accounts");

#####Reading an encrypted password

           final SymmetricKeyWrapper decryptedPassword = configuration.get(SymmetricKeyWrapper.class, "password")

#####Configuration reload

            configuration = new PropertyConfiguration(5, TimeUnit.SECONDS); //poll configuration every 5 seconds for changes



###Documentation
+  [tutorial](https://github.com/MuhammadAshraf/uncommon-configuration/wiki)

+  [javadocs](http://muhammadashraf.github.com/uncommon-configuration/uncommon-configuration-0.1-javadoc/)

###Download

+  binary: [uncommon-configuration-0.1.jar](https://oss.sonatype.org/service/local/repositories/releases/content/com/github/uncommon-configuration/uncommon-configuration/0.1/uncommon-configuration-0.1.jar)

+  source: [uncommon-configuration-0.1-sources.jar](https://oss.sonatype.org/service/local/repositories/releases/content/com/github/uncommon-configuration/uncommon-configuration/0.1//uncommon-configuration-0.1-sources.jar)

+  javadocs: [uncommon-configuration-0.1-javadoc.jar](https://oss.sonatype.org/service/local/repositories/releases/content/com/github/uncommon-configuration/uncommon-configuration/0.1//uncommon-configuration-0.1-javadoc.jar)


###Maven
           <dependency>
            <groupId>com.github.uncommon-configuration</groupId>
            <artifactId>uncommon-configuration</artifactId>
            <version>0.1</version>
           </dependency>

###Dependencies
           slf4j-1.6.4 [required]
           snakeyaml-1.10  [only if you are using YAML configuration]
           json-simple-1.1 [only if you are using JSON configuration]
           bouncycastle-140 [nly if you are using encryption]
           (JCE) Unlimited Strength Jurisdiction Policy Files  [only if you are using encryption]

###License
ASL 2 -  [http://www.apache.org/licenses/LICENSE-2.0]( http://www.apache.org/licenses/LICENSE-2.0)
