package com.example.product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);


        // PRODUCT MİCROSERVİCE SORUMLULUKLARI
		/*
		 ÜRÜN BİLGİLERİNİ YÖNETİR (ÜRÜN EKLEME , SİLME GÜNCELLEME)
		 ÜRÜN DETAYLARINI SAĞLAR (İSİM,AÇIKLAMA,FİYAT VB..)
		 ÜRÜNLERİN MEVCUT STOK BİLGİLERİNİ SAĞLAR (INVENTORY MS İLE İLETİŞİM)
		 */
    }

}
