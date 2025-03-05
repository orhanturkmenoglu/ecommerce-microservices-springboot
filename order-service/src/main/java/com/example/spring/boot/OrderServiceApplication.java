package com.example.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
@EnableScheduling
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);


        /*
            Sipariş Listeleme: Tüm siparişleri getirir.
            Sipariş Getirme: Belirli bir siparişi ID'ye göre getirir.
            Sipariş Oluşturma: Yeni bir sipariş oluşturur.
            Sipariş Güncelleme: Mevcut siparişi günceller.
            Sipariş Silme: Belirli bir siparişi siler.
         */
    }

}
