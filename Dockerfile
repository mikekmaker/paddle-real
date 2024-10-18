# Step 1: Use Eclipse Temurin as the base image (successor to AdoptOpenJDK)
FROM eclipse-temurin:17-jdk

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Install required dependencies, including curl, unzip, and zip
RUN apt-get update && apt-get install -y curl unzip zip && \
    curl -s https://get.sdkman.io | bash

# Step 4: Install Grails 6.2 using SDKMAN in a single shell session
RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install grails 6.2.0"

# Step 5: Copy the application source code into the container
COPY . /app

# Step 6: Build the Grails application using the Gradle wrapper
#RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && ./gradlew build -x test -x integrationTest"
RUN bash -c "./gradlew build -x test -x integrationTest"
# Step 7: Expose the port the application will run on (default Grails port is 8080)
EXPOSE 8080

# Step 8: Start the Grails application using the Gradle wrapper
#CMD bash -c "source /root/.sdkman/bin/sdkman-init.sh && grails run-app"
#CMD ["bash", "-c", "source /root/.sdkman/bin/sdkman-init.sh && grails run-app"]
#CMD ["bash", "-c", "source /root/.sdkman/bin/sdkman-init.sh && ./gradlew bootRun"]
CMD ["bash", "-c", "./gradlew bootRun"]