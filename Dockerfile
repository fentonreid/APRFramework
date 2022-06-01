FROM ubuntu:20.04

#############################################################################
# Requirements
#############################################################################

RUN \
  apt-get update -y && \
  apt-get install software-properties-common -y && \
  apt-get update -y && \
  apt-get install -y openjdk-8-jdk \
                git \
                build-essential \
				subversion \
				perl \
				curl \
				unzip \
				cpanminus \
				make \
                && \
  rm -rf /var/lib/apt/lists/*

# Java version
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

#############################################################################
# Setup APRFramework
#############################################################################

# ----------- Step 1. Copy APRFramework from host directory into /APRFramework container directory --------------
WORKDIR /APRFramework
COPY . .

# ----------- Step 2. Add the SnakeYAML version 1.30 as an external jar file --------------
RUN curl https://repo1.maven.org/maven2/org/yaml/snakeyaml/1.30/snakeyaml-1.30.jar -o /APRFramework/snakeyaml-1.30.jar

#############################################################################
# Setup Defects4J
#############################################################################

# Timezone
ENV TZ=America/Los_Angeles
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# ----------- Step 1. Clone defects4j from Github --------------
WORKDIR /APRFramework
RUN git clone https://github.com/rjust/defects4j.git defects4j

# ----------- Step 2. Initialize Defects4J ---------------------
WORKDIR /APRFramework/defects4j
RUN cpanm --installdeps .
RUN ./init.sh

# ----------- Step 3. Add Defects4J's executables to PATH ------
ENV PATH="/APRFramework/defects4j/framework/bin:${PATH}"  

#############################################################################
# Setup Main.java add external jar + CMD
#############################################################################

# ----------- Step 1. Setup javac to compile current directory and add external snakeyaml jar --------------
WORKDIR /APRFramework/src
#RUN javac -cp ".:../snakeyaml-1.30.jar" APRFramework.java

# ----------- Step 2. Set Docker CMD where the Main.java class is run --------------
#CMD ["java", "-cp", ".:../snakeyaml-1.30.jar", "APRFramework"]
#--------------