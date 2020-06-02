#!/bin/sh
set -x
set -e
set -v

#INIT PROJECTS RELATED INFO
#------------- UPDATE ALWAYS CORRESPONDINGLY!
# pom.xml Artifact id must be 'project'
#remote_address=54.229.84.224
remote_address=35.158.134.255
frontend_folder_name=codenames-frontend
frontend_source_location=./$frontend_folder_name/dist/*
backend_source_location=./target/project-0.0.1-SNAPSHOT.jar
frontend_remote_location=/home/ubuntu/frontend
backend_remote_location=/home/ubuntu
pem_file_full_path=my-key.pem
ssh_options='StrictHostKeyChecking=accept-new'

#BUILD PROJECT
cd $frontend_folder_name
npm install
npm run build
cd ..
mvn clean package -DskipTests=true
#chmod 400 $pem_file_full_path

echo "$SSH_KEY" > key.pem

#COPY LOCAL FILES TO SERVER
scp  -o $ssh_options -i key.pem -r $frontend_source_location ubuntu@$remote_address:$frontend_remote_location
scp  -o $ssh_options -i key.pem $backend_source_location ubuntu@$remote_address:$backend_remote_location/project-0.0.1-SNAPSHOT.jar.new

#UPDATE .JAR WITH NEW, AND RESTART
ssh -o $ssh_options -i key.pem ubuntu@$remote_address './shutdown.sh'
ssh -o $ssh_options -i key.pem ubuntu@$remote_address 'mv project-0.0.1-SNAPSHOT.jar.new project.jar'
ssh -o $ssh_options -i key.pem ubuntu@$remote_address './start.sh'
