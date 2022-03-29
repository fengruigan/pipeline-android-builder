FROM androidsdk/android-30:latest

RUN apt-get update -y && \
    apt-get install git && \
    mkdir -p /tmp/gradle-user-home/caches

COPY aws-lambda-rie /usr/local/bin/aws-lambda-rie

ENV AWS_ACCESS_KEY=""
ENV AWS_SECRET_KEY=""
ENV GRADLE_USER_HOME=/tmp/gradle-user-home
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV LAMBDA_TASK_ROOT=/var/task
ENV LAMBDA_RUNTIME_DIR=/var/runtime
ENV PATH=/var/lang/bin:/usr/local/bin:/usr/bin/:/bin:/opt/bin:/opt/android-sdk-linux/build-tools/30.0.2
ENV TZ=:/etc/localtime
ENV LANG=en_US.UTF-8
ENV LD_LIBRARY_PATH=/var/lang/lib:/lib64:/usr/lib64:/var/runtime:/var/runtime/lib:/var/task:/var/task/lib:/opt/lib
WORKDIR ${LAMBDA_TASK_ROOT}

COPY build/classes/java/main ${LAMBDA_TASK_ROOT}/
COPY build/dependencies/* ${LAMBDA_TASK_ROOT}/lib/

#ENTRYPOINT [ "/usr/bin/java", "-cp", "./lib/*", "com.amazonaws.services.lambda.runtime.api.client.AWSLambda" ]
ENTRYPOINT ["/usr/local/bin/aws-lambda-rie", "/usr/bin/java", "-cp", "./lib/*", "com.amazonaws.services.lambda.runtime.api.client.AWSLambda"]
CMD ["com.fengruigan.androidbuilder.AndroidBuilder::handleRequest"]