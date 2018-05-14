How to package:
------------
In app's terminal, type in [activator], now you are in current app dir, and type in [dist] to package this Application

So, your package is ready in /home/brad/workspace/YarnMonitor/target/universal/yarnmonitor-1.0-SNAPSHOT.zip

sbt rpm:packageBin

How to run it:
------------
- 1. Upload your application's zip to server
- 2. Unzip your zip
- 3. cd /$APP_HOME/bin
- 4. Run it **in background** by command: nohup ./yarnmonitor -Dplay.crypto.secret=dmpyarnmonitor021 -Dhttp.port=9090 &
- 5. Dplay.crypto.secret must be equals application.conf parameter:application.secret
- 6. If you want to change port, update Dhttp.port