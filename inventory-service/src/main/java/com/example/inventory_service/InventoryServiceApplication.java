package com.example.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);


        // INVENTORY MİCROSERSERVİCE SORUMLULUKLARI :

        /*
            ENVANTER BİLGİLERİNİ YÖNETİR.
            ÜRÜNLERİN STOK DURUMUNU SAĞLAR.
            ÜRÜNLERİN STOKLARINI DOĞRULAR VE GÜNCELLER (PRODUCT MS VE ORDER MS İLE İLETİŞİM.)
         */

        /*
        API'ler:
        GET /inventory: Tüm stokları listeleme.
        GET /inventory/{productId}: Belirli bir ürünün stok durumunu getirme.
        POST /inventory: Yeni stok ekleme.
        PUT /inventory/{productId}: Stok güncelleme.
        DELETE /inventory/{productId}: Stok silme.
         */
    }

}
