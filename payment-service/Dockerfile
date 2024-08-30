# 1. Kullanılacak temel imajı belirtir
FROM openjdk:17-jdk

# 2. Uygulama için bir çalışma dizini oluşturun
WORKDIR /app

# 3. Derleme zamanı değişken tanımlama
ARG JAR_FILE=target/*.jar

# 4. JAR dosyasını Docker imajına kopyalıyoruz ve isimlendiriyoruz
COPY ${JAR_FILE} paymentservice.jar

# 5. Uygulamanın çalıştırılması için gerekli komutu belirtir
ENTRYPOINT ["java", "-jar", "paymentservice.jar"]

# 6. Bağlantı noktasını açığa çıkarma (Eğer varsayılan olarak 8080'de çalışıyorsa)
EXPOSE 8085
