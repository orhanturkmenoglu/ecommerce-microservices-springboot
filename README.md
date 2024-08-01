🚀 E-commerce-Springboot ile Mikroservis Mimarisine Sahip E-ticaret Projemizi Tanıtıyorum! 🛒

GitHub'daki en son projemi paylaşmaktan heyecan duyuyorum - e-commerce-springboot adlı e-ticaret uygulamamız, Spring Boot kullanarak mikroservis mimarisiyle oluşturulmuştur. Bu proje, modern mikroservis kavramlarının ve en iyi uygulamaların uygulanmasını göstermektedir.

🔧 Kullanılan Teknolojiler ve Araçlar:

* Spring Boot: Mikroservisler için temel çerçeve.
* Spring Cloud Config: Merkezi yapılandırma yönetimi.
* Spring Cloud Netflix Eureka: Servis kaydı ve keşfi.
* Spring Cloud Gateway: İstekleri yönlendiren API geçidi.
* Feign Client: Servisler arası iletişim için bildirimsel REST istemcisi.
* Resilience4J: Devre kesici, yeniden deneme ve hız sınırlayıcı mekanizmalar.
* Validation: Girdi doğrulaması için Hibernate Validator.
* Health Checks: Her bir mikroservisin sağlık durumunu izleme.
* RabbitMQ: Asenkron iletişim ve stok güncelleme mekanizması.
* Okta (Yakında): Kimlik doğrulama ve yetkilendirme için güvenli ve kullanıcı dostu bir çözüm.


📌 Proje Özeti:
Bu proje, servis kaydı, API geçidi, hata toleransı ve merkezi yapılandırma gibi çeşitli mikroservis kavramlarını içermektedir. Uygulama, ürün yönetimi, envanter yönetimi ve sipariş yönetimi gibi temel e-ticaret işlevlerini kapsamaktadır.

📂 Proje Yapısı:

* config-server: Merkezi yapılandırma sunucusu.
* discovery-server: Eureka kullanarak servis kaydı.
* api-gateway: Spring Cloud Gateway kullanarak API geçidi.
* product-service: Ürün bilgilerini yönetir.
* inventory-service: Envanter ve stok seviyelerini yönetir.
* order-service: Müşteri siparişlerini yönetir.

🌐 Endpointler:

Product Service:

* POST /api/v1/products: Yeni ürün oluştur.
* GET /api/v1/products/all: Tüm ürünleri listele.
* GET /api/v1/products/{id}: Belirli bir ürünü ID ile getir.
* GET /api/v1/products/inventoryById/{inventoryId}: Belirli bir envanter ID'si ile ürünü getir.
* GET /api/v1/products/productByPriceRange: Fiyat aralığına göre ürünleri getir.
* GET /api/v1/products/productByQuantity: Belirli miktardaki ürünleri getir.
* GET /api/v1/products/productByPriceGreaterThanEqual: Belirli fiyatın üzerindeki ürünleri getir.
* GET /api/v1/products/productByPriceLessThanEqual: Belirli fiyatın altındaki ürünleri getir.
* GET /api/v1/products/productByCategory: Kategoriye göre ürünleri getir.
* PUT /api/v1/products: Ürünü güncelle (Ürün güncellendiğinde envanter servisi de güncellenir).
* DELETE /api/v1/products/{id}: Ürünü sil (Ürün silindiğinde envanter servisi de güncellenir).

  
Inventory Service:

* POST /api/v1/inventories/create: Yeni envanter oluştur.
* GET /api/v1/inventories/all: Tüm envanterleri listele.
* GET /api/v1/inventories/{productId}: Belirli bir ürün ID'si ile envanteri getir.
* GET /api/v1/inventories/getInventoryId/{id}: Belirli bir envanter ID'si ile envanteri getir.
* PUT /api/v1/inventories/{inventoryId}: Envanteri güncelle.
* DELETE /api/v1/inventories/{productId}: Envanteri sil.

Order Service:

* GET /api/v1/orders: Tüm siparişleri listele.
* GET /api/v1/orders/{id}: Belirli bir siparişi ID ile getir.
* POST /api/v1/orders: Yeni sipariş oluştur (Sipariş verildiğinde stok miktarı RabbitMQ üzerinden envanter servisine iletilir ve güncellenir).
* PUT /api/v1/orders/{id}: Siparişi güncelle.
* DELETE /api/v1/orders/{id}: Siparişi sil.


📈 Gelecekteki Geliştirmeler:

* Okta entegrasyonu ile kimlik doğrulama ve yetkilendirme.
* Sleuth ve Zipkin kullanarak dağıtılmış izleme.
* Daha detaylı loglama ve izleme eklemeleri.

