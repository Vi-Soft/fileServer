cd /home/visrvadmin/vi-soft/fileServer
docker container stop file-server-app
docker rmi file-server
docker build -t file-server .
docker run  --rm -d -v /home/visrvadmin/vi-soft/fileServer:/tmp  --network host --name file-server-app file-server
