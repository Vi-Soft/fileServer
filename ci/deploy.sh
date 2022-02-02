cd ~/vi-soft/fileServer
docker container stop file-server
docker rmi file-server
docker build -t file-server .
docker run  --rm -d --network host --name file-server-app file-server
