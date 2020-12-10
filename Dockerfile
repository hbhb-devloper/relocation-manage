FROM registry-vpc.cn-hangzhou.aliyuncs.com/hbhb/8-jdk-alpine as builder
WORKDIR application
ADD ./target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM registry-vpc.cn-hangzhou.aliyuncs.com/hbhb/8-jdk-alpine
# 解决alpine镜像中easyexcel导出poi时缺少特定字体的问题
#RUN apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT exec java $JAVA_OPTS $JAVA_DEBUG_OPTS org.springframework.boot.loader.JarLauncher
EXPOSE 8888