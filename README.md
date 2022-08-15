# APRFramework

## Using DockerHub
All dependencies used by the APRFramework are neatly encapsulated within a Dockerfile, meaning the only requirement for running this experiment is Docker itself. DockerHub provides the simplest mechanism to run this project, with recommendation to try this method first. 

### Steps
1. Download Docker for your specific operating system, https://docs.docker.com/get-docker/
2. Follow the installation instructions for Docker if needed, and, check that Docker installed correctly
3. Create your own config.yml file, using https://github.com/fentonreid/APRFramework/blob/main/config.yml as reference
4. Open a terminal tab in the same directory as this configuration file
5. Run the command found at step 6 to start the APRFramework. Replacing \${pwd} with the absolute path to the directory you are in
6. ***docker run -v \${pwd}/config.yml:/APRFramework/config.yml -v \${pwd}/output/:/output/ fentonreid/aprframework:latest***
7. Once the APRFramework has finished, the desired output for each bug will be saved in the /output directory
8. Make sure to remove the locally downloaded container once you are finished, as this file is \ around 2.58GB in size
9. To do this, type '***docker images***' in your terminal and locate the 'IMAGE ID' of the 'fentonreid/aprframework' container
10. You can remove this image locally by typing: '***docker rmi -f \$IMAGE ID***', replacing \$IMAGE ID, with your specific value


## Building the container locally
An alternative method for creating the required Docker container is also possible, where you can build the container yourself with the given Dockerfile, the steps are outlined below. 

### Steps
1. Repeat the steps for the 'Using DockerHub' section up to and including step 2
2. Git clone my repository to a directory of your choosing, using the following command in a command line window, '***https://github.com/fentonreid/APRFramework***'
3. Edit the original config.yml file to the desired configuration
4. Open a terminal tab in the same directory as this GitHub repository
5. Run the Docker build command: '***docker build -t aprframework .***'
6. The container may take some time to create, please be patient
7. Once the container is created, run the command found at step 8 to start the APRFramework. Replacing \${pwd} with the absolute path to the directory you are in
8. ***docker run -v \${pwd}/output/:/output/ aprframework***
9. Once the APRFramework has finished outputting and the container is no longer required, please cleanup the container, following steps 8-10 of 'Using DockerHub'
10. Once the APRFramework has finished, the desired output for each bug will be saved in the /output directory
11. Make sure to remove the locally downloaded container once you are finished, as this file is \ around 2.58GB in size
12. To do this, type '***docker images***' in your terminal and locate the 'IMAGE ID' of the 'fentonreid/aprframework' container
13. You can remove this image locally by typing: '***docker rmi -f \$IMAGE ID***', replacing \$IMAGE ID, with your specific value
