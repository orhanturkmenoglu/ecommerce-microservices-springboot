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
* Zipkin: Dağıtık izleme ve performans izleme için.
* Redis: Hızlı veri erişimi ve önbellekleme için.
* Swagger UI: API belgeleri için kullanıcı dostu arayüz.
* Docker ve Docker Compose: Sistem bileşenlerinin sorunsuz çalışmasını sağlamak için kapsayıcı yönetimi ve konfigürasyonu.
  
🔑 Yeni Gelen Özellikler:

Keycloak: API Gateway üzerinden kimlik doğrulama ve yetkilendirme yapılarak sistemlere güvenli erişim sağlanması.
Stripe API: Ödeme işlemlerini yönetmek için kullanılan güçlü ve esnek bir ödeme işlemcisidir. Stripe API, kullanıcıların kredi kartı ödemelerini güvenli bir şekilde alabilmesi için entegre edilmiştir. Müşterilerin ödeme işlemleri sırasında güvenli ve sorunsuz bir deneyim sunmayı amaçlar.

📌 Proje Özeti: Bu proje, servis kaydı, API geçidi, hata toleransı, merkezi yapılandırma ve ödeme yönetimi gibi çeşitli mikroservis kavramlarını içermektedir. Uygulama, müşteri yönetimi, adres yönetimi, ürün yönetimi, envanter yönetimi, sipariş yönetimi ve ödeme yönetimi gibi temel e-ticaret işlevlerini kapsamaktadır. Müşterilerin sipariş verebilmesi için önce sisteme kayıt olmaları ve kayıt esnasında adres bilgilerini sağlamaları gerekmektedir. Bu süreç, API Gateway üzerinden Keycloak kullanılarak yapılan kimlik doğrulama ve yetkilendirme ile güvenli bir şekilde yönetilmektedir. Sipariş verildikten sonra ödeme yapılması gerekmekte ve ödeme işlemi için Stripe API kullanılarak ödeme güvenli bir şekilde alınmaktadır. Ödeme işlemi tamamlandıktan sonra stok miktarı RabbitMQ üzerinden güncellenmektedir. Sipariş güncelleme esnasında ödeme durumu iptal edilir ve sipariş güncellendiğinde yeniden ödeme yapılması gerekir.

✨ Ayrıca, her gece saat 24:00'te otomatik olarak sepet temizleme işlemi yapılmaktadır. Bu özellik, tamamlanmamış ve 24 saatten eski siparişleri sistemden siler, böylece veritabanı ve sistem verimliliği korunmuş olur.

📂 Proje Yapısı:

* config-server: Merkezi yapılandırma sunucusu.
* discovery-server: Eureka kullanarak servis kaydı.
* api-gateway: Spring Cloud Gateway kullanarak API geçidi.
* customer-service: Müşteri bilgilerini yönetir ve müşterilerin sisteme kayıt olmasını sağlar.
* address-service: Müşteri adreslerini yönetir. (Adres bilgisi müşteri kaydı sırasında oluşturulur, daha sonrasında güncellenebilir.)
* product-service: Ürün bilgilerini yönetir.
* inventory-service: Envanter ve stok seviyelerini yönetir.
* order-service: Müşteri siparişlerini yönetir.
* payment-service: Sipariş ödemelerini yönetir. (Sipariş verildikten sonra ödeme yapılması gerekir).
* cargo-service: Kargo durumlarını yönetir; sipariş verildikten sonra kargo durumu hazırlanır ve ödeme tamamlandıktan sonra kargo siparişi tamamlanır.
  
🌐 Endpointler:

Customer Service:

* POST /api/v1/customers: Yeni müşteri kaydı oluştur.
* GET /api/v1/customers/all: Tüm müşterileri listele.
* GET /api/v1/customers/{customerId}: Belirli bir müşteri bilgilerini ID ile getir.
* GET /api/v1/customers/customerByFirstName: Belirli bir isimle müşterileri listele.
* GET /api/v1/customers/track-cargo/{trackingNumber} :Müşterinin belirli bir kargonun ayrıntılarını izleme numarasına göre almanıza olanak tanır.
* PUT /api/v1/customers/{customerId}: Müşteri bilgilerini güncelle. (Bu işlem sırasında müşteri adresi güncellenmez. Adres güncellemesi için ayrı bir endpoint kullanılmalıdır.)
* DELETE /api/v1/customers/{customerId}: Müşteri kaydını sil.

Address Service:

* GET /api/v1/addresses/all: Tüm adresleri listele.
* GET /api/v1/addresses/{addressId}: Belirli bir adresi ID ile getir.
* PUT /api/v1/addresses/{addressId}: Adres bilgilerini güncelle. (Adres güncellemesi müşteri kaydından ayrı olarak yapılır.)
* DELETE /api/v1/addresses/{addressId}: Belirli bir adres kaydını sil.

Product Service:

* POST /api/v1/products: Yeni ürün oluştur.
* GET /api/v1/products/all: Tüm ürünleri listele.
* GET /api/v1/products/{id}: Belirli bir ürünü ID ile getir.
* GET /api/v1/products/inventoryById/{inventoryId}: Belirli bir envanter ID'si ile ürünü getir.
* GET /api/v1/products/productByPriceRange?minPrice={minPrice}&maxPrice={maxPrice}: Belirtilen fiyat aralığındaki ürünleri listele.
* GET /api/v1/products/productByQuantity?quantity={quantity}: Belirli miktarda olan ürünleri listele.
* GET /api/v1/products/productByPriceGreaterThanEqual?price={price}: Belirli bir fiyatın üzerindeki ürünleri listele.
* GET /api/v1/products/productByPriceLessThanEqual?price={price}: Belirli bir fiyatın altındaki ürünleri listele.
* GET /api/v1/products/productByCategory?category={category}: Belirli bir kategoriye ait ürünleri listele.
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
* GET /api/v1/orders/orderDateBetween?startDateTime={startDateTime}&endDateTime={endDateTime}: Belirtilen tarih aralığındaki siparişleri listele.
* POST /api/v1/orders: Yeni sipariş oluştur.
* PUT /api/v1/orders/{id}: Var olan bir siparişi güncelle.
* DELETE /api/v1/orders/{id}: Belirli bir siparişi sil.

Payment Service:

* POST /api/v1/payments: Yeni ödeme oluştur (Sipariş verildikten sonra ödeme yapılması gerekir. RABBIT MQ üzerinden ödeme alındıktan sonra stok güncellemesi yapılır).
* GET /api/v1/payments/{orderId}: Belirli bir sipariş ID'si ile ödeme durumunu getir.
* GET /api/v1/payments/paymentById/{paymentId}: Belirli bir ödeme ID'si ile ödeme bilgilerini getir.
* GET /api/v1/payments/paymentByType: Belirli bir ödeme türü ile ödeme bilgilerini getir.
* GET /api/v1/payments/paymentDateBetween: Belirtilen tarihler arasında ödeme bilgilerini getir.
* GET /api/v1/payments/paymentCustomerById/{customerId}: Belirli bir müşteri ID'si ile ödeme bilgilerini getir.
* PUT /api/v1/payments: Ödemeyi güncelle (Ödeme yapıldıktan sonra RabbitMQ üzerinden stok miktarı güncellenir).
* DELETE /api/v1/payments/{paymentId}: Ödemeyi sil.
* POST /api/v1/payments/cancelPayment/{paymentId}: Belirli bir ödeme ID'si ile ödemeyi iptal et.

Cargo Service:
* POST /api/v1/cargos: Yeni bir kargo oluşturur.
* GET /api/v1/cargos/all: Tüm kargoların listesini getirir.
* GET /api/v1/cargos/{cargoId}: Belirli bir ID'ye sahip kargoyu getirir.
* GET /api/v1/cargos/order/{orderId}: Belirli bir sipariş ID'sine bağlı kargoyu getirir.
* GET /api/v1/cargos/trackingNumber/{trackingNumber}: Belirli bir takip numarasına sahip kargoyu getirir.
* PUT /api/v1/cargos: Mevcut bir kargoyu günceller.
* DELETE /api/v1/cargos/{cargoId}: Belirli bir ID'ye sahip kargoyu siler.

📈 Gelecekteki Geliştirmeler:

* GraphQL Desteği: API'yi daha esnek hale getirmek için GraphQL entegrasyonu.
* Test Otomasyonu ve CI/CD Entegrasyonu: Sürekli entegrasyon ve dağıtım (CI/CD) süreçlerini otomatize etmek.
* Bildirim servisi eklenmesi.

