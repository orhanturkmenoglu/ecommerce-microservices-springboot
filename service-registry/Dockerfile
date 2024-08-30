#Kullanılacak temel imajı belirtir.
FROM openjdk:17-jdk

# Uygulama için bir çalışma dizini oluşturun
# sonraki tüm komutlar bu dizin içerisinde çalışacaktır
#düzen sağlar.
WORKDIR /app

#Derleme zamanı değişken tanımlama target altında .jar uzantılı dosyaları barındırır.
ARG JAR_FILE=target/*.jar

# JAR dosyasını Docker imajına kopyalıyoruz ve isimlendiriyoruz

COPY ${JAR_FILE} serviceregistry.jar

#Uygulamanın çalıştırılması için gerekli komutu belirtir.
ENTRYPOINT ["java","-jar","serviceregistry.jar"]

#BAĞLANTI NOKTASINI AÇIĞA ÇIKARMA
EXPOSE 8761

#docker build -t my-spring-boot-app .
#docker run -p 8761:8761 my-spring-boot-app