# 1. Kullanılacak temel imajı belirtir
FROM openjdk:17-jdk

# 2. Uygulama için bir çalışma dizini oluşturun
WORKDIR /app

VOLUME /temp

# 4. JAR dosyasını Docker imajına kopyalıyoruz ve isimlendiriyoruz


# 5. Uygulamanın çalıştırılması için gerekli komutu belirtir
ENTRYPOINT ["java", "-jar", "paymentservice.jar"]

# 6. Bağlantı noktasını açığa çıkarma (Eğer varsayılan olarak 8080'de çalışıyorsa)
EXPOSE 8085
