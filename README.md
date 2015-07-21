# ohatv (Beta)
A Webapplication to find and index Tv Shows.

# disclaimer
The application is in Beta, and I will not be responsible for any actions regarding the usage.

#Installation Windows
Make sure Java is installed, check by command line with "java -version" you will need 1.7.0 or higher!

1. Download and extract the zip file from the Beta folder.
2. Open run.bat with notepad and change the "--port 9090" if you want a different port.
3. Start with run.bat
4. Jetty runner needs a while before it will show in the browser.
5. go to http://localhost:port

#Installation Mac/Linux
Make sure Java is installed, Terminal command to check is "java -version" you will need 1.7.0 or higher!
I have tested this with OracleJDK and not OpenJDK but feel free to test.

1. Download and extract the zip file from the Beta folder.
2. Open terminal and navigate to download location.
3. run the app with the following command (notice you can change the --port if you want a different one): "java -jar jetty-runner-9.2.10.v20150310.jar --port 9090 --log log --out out ohatv.war"
4. Jetty runner needs a while before it will show in the browser.
5. go to http://localhost:port
