#!/bin/bash

# Update the system
yum update -y

# Install Docker
yum install -y docker

# Start Docker
systemctl start docker

# Enable Docker
systemctl enable docker

# Run the Docker container
docker run -d -p 80:80 -e MAIL_USERNAME=username -e MAIL_PASSWORD=password -v mail-log:/app/log --name mail-app sarathvedha/spring-boot-mail-app:latest