# Vi-Soft-NG
The next generation of Vi-Soft's quality control system.

### Prerequisites:
  1. jdk 1.8 or later
    http://www.oracle.com/technetwork/java/javase/downloads/index.html
  2. git 2.17.0 or later
    https://git-scm.com/downloads
  3. Apache Maven 3.5.2 or later
    https://maven.apache.org/download.cgi
  4. MongoDB 3.6 or later
    https://www.mongodb.com/download-center#atlas
  5. IPFS 0.4.15 or later
    https://ipfs.io/docs/install/
  6. Release 8080 port
  
### Getting started guide:
  1. Open terminal
  2. cd ~/MongoDB/Server/3.6/bin
  3. ./mongod
  4. Open another terminal
  5. cd ~/go-ipfs
  6. ./ipfs daemon
  7. Open another terminal
  8. cd ~/"your work directory name"
  9. git clone https://github.com/Vi-Soft/Vi-Soft-NG.git
  10. cd Vi-Soft-NG
  11. mvn clean package
  12. mvn dependency:sources
  13. mvn eclipse:clean eclipse:eclipse
  14. mvn clean package exec:exec
  15. Open web browser on URI: http://localhost:7080/api
  16. Upload file or multiple files
  17. Uploaded files there are in Vi-Soft-NG/docFileOut
  15. Open web browser on URI: http://localhost:7080/graphql
  19. Run query: 
  <pre>
      query allDocFiles {
        allDocFiles(filter:{projectId: "2", taskId: "1.1"}){
          id
          fileContentHash
          fileName
          projectId
          taskId
          createdAt
          businessType
          postedBy {
            name
            id
            lastname
            email
            passwd
          }
          fileLengthInBytes
        }
      }
  </pre>
  20. Get some "fileContentHash" from result of step 16.
  21. Open web browser on URI: http://localhost:5001/ipfs/QmQLXHs7K98JNQdWrBB2cQLJahPhmupbDjRuH1b9ibmwVa/#/objects
  22. Enter "fileContentHash" in hash field, press "GO"
  
  
