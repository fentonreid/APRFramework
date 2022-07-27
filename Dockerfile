FROM ubuntu:20.04 

# Install dependencies
RUN apt-get update -y && \
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
            wget && \
    rm -rf /var/lib/apt/lists/*

#############################################################################
# Setup Defects4J
#############################################################################

# Setup Defects4j environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 \
    TZ=America/Los_Angeles \
    PATH="defects4j/framework/bin:${PATH}" 

# Setup Defects4J dependencies
WORKDIR /
RUN git clone https://github.com/rjust/defects4j.git defects4j

WORKDIR /defects4j
RUN cpanm --installdeps . && \
    ./init.sh && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
    
#############################################################################
# Maven setup
#############################################################################

# Setup Maven environment variables
ENV MAVEN_VERSION=3.8.6 \
    MAVEN_HOME=/opt/maven \
    M2_HOME=/opt/maven \
    PATH=${M2_HOME}/bin:${PATH}

WORKDIR /
RUN wget https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -P /tmp/mvn && \
    wget https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz.sha512 -P /tmp/mvn/ && \
    echo "$(cat /tmp/mvn/apache-maven-${MAVEN_VERSION}-bin.tar.gz.sha512) /tmp/mvn/apache-maven-${MAVEN_VERSION}-bin.tar.gz" | sha512sum -c && \
    tar xzf /tmp/mvn/apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt/ && \
    ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/local/bin && \
    rm /tmp/mvn/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    rm /tmp/mvn/apache-maven-${MAVEN_VERSION}-bin.tar.gz.sha512


#############################################################################
# APRFramework runner
#############################################################################

WORKDIR /APRFramework
COPY . .

# mvn compile exec:java -Dexec.mainClass="APRFramework"
# mvn -Dtest=TestSPM#SPM1 test

#RUN mvn clean compile assembly:single
#CMD java -jar target/APRFramework-jar-with-dependencies.jar