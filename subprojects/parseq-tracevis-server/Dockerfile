FROM ubuntu

MAINTAINER Jaroslaw Odzga "jodzga@linkedin.com"

# Update aptitude with new repo
# Install other software
RUN apt-get -y update && apt-get install -y \
	graphviz \
	default-jdk \
	wget

RUN mkdir /opt/parseq-tracevis-server
RUN wget -O /opt/parseq-tracevis-server/parseq-tracevis-server-2.6.21-jar-with-dependencies.jar 'https://search.maven.org/remotecontent?filepath=com/linkedin/parseq/parseq-tracevis-server/2.6.21/parseq-tracevis-server-2.6.21-jar-with-dependencies.jar'

# Expose port 8080 to the host
EXPOSE 8080

# Set the current work directory
WORKDIR /opt/parseq-tracevis-server

ENTRYPOINT ["java", "-Xmx2g", "-Xms2g", "-jar", "/opt/parseq-tracevis-server/parseq-tracevis-server-2.6.21-jar-with-dependencies.jar", "/usr/bin/dot", "8080"]
