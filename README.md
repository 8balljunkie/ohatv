# ohatv (Beta)
A lightweight Webapplication with responsive design to index Tv Shows and downloading.
This application has the same functions as Sickrage but with better Torrent support and no NZB support.
This is not to be used for findig legacy shows, but you could still use the rename function.

Screenshots: http://imgur.com/gallery/eafeI/new

## Disclaimer
The application is in Beta, and I will not be responsible for any actions regarding the usage.

## Features
- Finding show info and meta on thetvdb
- Select episodes wanted for download.
- Download using Transmission or Blackhole method.
- Automatically rename and move files to the desired folder using the standard by Kodi: Showname(folder) -> Season x(folder) -> episode(file).
- Manually rename and process files.
- Notifications on the dashboard.

##Upcomming Features
- Add support for scene tags
- File directory list viewer in the settings.
- Support for non conventional naming like 410 instead of S04E10.
- Support for combination episodes.
- Renaming files set by the user.
- Adding support for other bitTorrent clients.
- Support for Movies


##Installation Windows
Make sure Java is installed, check by command line with "java -version" you will need 1.7.0 or higher!

1. Download and extract the zip file from the Beta folder.
2. Open run.bat with notepad and change the "--port 9090" if you want a different port.
3. Start with run.bat
4. Jetty runner needs a while before it will show in the browser.
5. go to http://localhost:port
6. Set the settings first. You will have to type the directories, make sure checked permissions.

##Installation Mac/Linux
Make sure Java is installed, Terminal command to check is "java -version" you will need 1.7.0 or higher!
I have tested this with OracleJDK and not OpenJDK but feel free to test.

1. Download and extract the zip file from the Beta folder.
2. Open terminal and navigate to download location.
3. run the app with the following command (notice you can change the --port if you want a different one): "java -jar jetty-runner-9.2.10.v20150310.jar --port 9090 --log log --out out ohatv.war"
4. Jetty runner needs a while before it will show in the browser.
5. go to http://localhost:port
6. Set the settings first. You will have to type the directories, make sure checked permissions.
