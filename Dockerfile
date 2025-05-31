FROM openjdk:21-ea-9
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bank_transaction_system.jar

ENV LOG_DIR=/data/logs
ENV LOG_FILE=${LOG_DIR}/bank_transaction_system.log

RUN mkdir -p ${LOG_DIR}
ENTRYPOINT ["java","-jar","/bank_transaction_system.jar","--logging.file.name=${LOG_FILE}"]