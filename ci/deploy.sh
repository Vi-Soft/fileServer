cd /home/visrvadmin/vi-soft/fileServer
#docker container stop file-server-app
#docker rmi file-server
#docker build -t file-server .
#docker run  --rm -d -v /home/visrvadmin/vi-soft/fileServer:/tmp  --network host --name file-server-app file-server

PID=`ps -eaf | grep file-server | grep -v grep | awk '{print $2}'`
if [[ "" !=  "$PID" ]]; then
  echo "killing $PID"
  kill -9 $PID
fi
cp jar-dir/*.jar jar-dir/file-server.jar
java -Dcheck-db-services.run-all=false -DuseDbFile=false -Dmail-environment.prod-mode=true -jar jar-dir/file-server.jar
