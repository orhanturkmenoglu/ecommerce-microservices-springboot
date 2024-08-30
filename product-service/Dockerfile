#Kullanılacak temel imajı belirtir.
FROM openjdk:17-jdk

# Uygulama için bir çalışma dizini oluşturun
# sonraki tüm komutlar bu dizin içerisinde çalışacaktır
#düzen sağlar.
WORKDIR /app

# 3. Derleme zamanı değişken tanımlama
ARG JAR_FILE=target/*.jar

# 4. JAR dosyasını Docker imajına kopyalıyoruz ve isimlendiriyoruz
COPY ${JAR_FILE} productservice.jar

# 5. Uygulamanın çalıştırılması için gerekli komutu belirtir
ENTRYPOINT ["java", "-jar", "productservice.jar"]


# 6. Bağlantı noktasını açığa çıkarma (Eğer varsayılan olarak 8080'de çalışıyorsa)
EXPOSE 8080