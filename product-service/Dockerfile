#Kullanılacak temel imajı belirtir.
FROM openjdk:17-jdk

# Uygulama için bir çalışma dizini oluşturun
# sonraki tüm komutlar bu dizin içerisinde çalışacaktır
#düzen sağlar.
WORKDIR /app

VOLUME /temp


# 4. JAR dosyasını Docker imajına kopyalıyoruz ve isimlendiriyoruz
COPY ./target/product-service-0.0.1-SNAPSHOT.jar   ./product-service-0.0.1-SNAPSHOT.jar

# 5. Uygulamanın çalıştırılması için gerekli komutu belirtir
ENTRYPOINT ["java", "-jar", "product-service-0.0.1-SNAPSHOT.jar"]


# 6. Bağlantı noktasını açığa çıkarma (Eğer varsayılan olarak 8080'de çalışıyorsa)
EXPOSE 8080