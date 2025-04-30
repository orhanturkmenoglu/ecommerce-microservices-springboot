

🚀 E-commerce-Springboot: Mikroservis Tabanlı E-Ticaret Uygulaması 🛒 

GitHub'daki en son projemi paylaşmaktan heyecan duyuyorum - e-commerce-springboot adlı e-ticaret uygulamamız, Spring Boot kullanarak mikroservis mimarisiyle oluşturulmuştur. Bu proje, modern mikroservis kavramlarının ve en iyi uygulamaların uygulanmasını göstermektedir.

🔧 **Kullanılan Teknolojiler ve Araçlar:**

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
* JUnit & Mockito: Unit test yazmanın temel araçları; doğru ve hızlı testler yazabilmek için kullanıyorum.
* FeignClient & MockBean: Mikroservisler arası iletişimi test etmek için son derece kullanışlı.
  
🔑 **Yeni Gelen Özellikler:**

✅ **Jwt Authentication :** API Gateway üzerinden kimlik doğrulama ve yetkilendirme yapılarak sistemlere güvenli erişim sağlanması.
💳 **Stripe API Entegrasyonu:** Ödeme işlemlerini yönetmek için kullanılan güçlü ve esnek bir ödeme işlemcisidir. Stripe API, kullanıcıların kredi kartı ödemelerini güvenli bir şekilde alabilmesi için entegre edilmiştir. Müşterilerin ödeme işlemleri sırasında güvenli ve sorunsuz bir deneyim sunmayı amaçlar.
🕛 **Zamanlanmış Görev:** Her gece 24:00'te tamamlanmamış sepetlerin otomatik temizlenmesi



📌 **Proje Özeti:** Bu proje, servis kaydı, API geçidi, hata toleransı, merkezi yapılandırma ve ödeme yönetimi gibi çeşitli mikroservis kavramlarını içermektedir. Uygulama, müşteri yönetimi, adres yönetimi, ürün yönetimi, envanter yönetimi, sipariş yönetimi ve ödeme yönetimi gibi temel e-ticaret işlevlerini kapsamaktadır. Müşterilerin sipariş verebilmesi için önce sisteme kayıt olmaları ve giriş sağlamaları gerekmektedir. Bu süreç, API Gateway üzerinden Jwt Authentication kullanılarak yapılan kimlik doğrulama ve yetkilendirme ile güvenli bir şekilde yönetilmektedir. Sipariş verildikten sonra ödeme yapılması gerekmekte ve ödeme işlemi için Stripe API kullanılarak ödeme güvenli bir şekilde alınmaktadır. Ödeme işlemi tamamlandıktan sonra stok miktarı RabbitMQ üzerinden güncellenmektedir. Sipariş güncelleme esnasında ödeme durumu iptal edilir ve sipariş güncellendiğinde yeniden ödeme yapılması gerekir.

✨ Ayrıca, her gece saat 24:00'te otomatik olarak sepet temizleme işlemi yapılmaktadır. Bu özellik, tamamlanmamış ve 24 saatten eski siparişleri sistemden siler, böylece veritabanı ve sistem verimliliği korunmuş olur.

📂 **Proje Yapısı:**

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

🛠️ **Nasıl Çalışır?**

Bu mikroservis tabanlı e-ticaret uygulaması, bir dizi bağımsız servisin birleşiminden oluşur. Her bir servis, kendi sorumluluğuna göre işlev görür ve diğer servislerle iletişim kurar. İşte uygulamanın genel işleyişi:

**Kullanıcı Kayıt ve Giriş İşlemi:** Kullanıcı, API Gateway üzerinden sisteme giriş yapar. Kimlik doğrulama, JWT Authentication kullanılarak yapılır.

**Ürün ve Sipariş Yönetimi:** Kullanıcı, Product-Service üzerinden ürünleri görüntüler ve Order-Service üzerinden sipariş oluşturur.

**Ödeme İşlemi:** Payment-Service devreye girer ve Stripe API ile ödeme alınır.

**Kargo Durumu:** Sipariş ödendikten sonra, Cargo-Service ile kargo durumu takip edilir.

**Veritabanı Temizliği:** Her gece saat 24:00'te tamamlanmamış siparişler sistemden temizlenir.

**Asenkron İletişim:** Kafka ile mikroservisler arası asenkron iletişim sağlanır.
  
📥 **Projeyi İndirme ve Çalıştırma**
1. GitHub Reposunu Klonlayın
Projeyi bilgisayarınıza klonlamak için:
* git clone https://github.com/orhanturkmenoglu/ecommerce-microservices-springboot.git
* cd ecommerce-microservices-springboot
  
2. Bağımlılıkları Yükleyin
Projeyi açtıktan sonra, gerekli bağımlılıkları yüklemek için Maven veya Gradle kullanabilirsiniz. Maven için şu komutu çalıştırın:
* mvn clean install


📈 **Gelecekteki Geliştirmeler:**
* Test Otomasyonu ve CI/CD Entegrasyonu: Sürekli entegrasyon ve dağıtım (CI/CD) süreçlerini otomatize etmek.
* API Rate Limiting ve Throttling: API'yi aşırı yükten korumak için rate limiting ve throttling mekanizmaları eklemek.
* Kubernetes ve Helm Kullanımı: Uygulamanın ölçeklenebilirliğini artırmak için Kubernetes üzerinde çalıştırılacak şekilde yapılandırmak ve Helm chart’ları ile dağıtım yapmak.
* WebSocket Desteği: Gerçek zamanlı bildirimler ve veri güncellemeleri için WebSocket desteği eklemek.

📝 **Katkıda Bulunma**
Eğer projeye katkı sağlamak isterseniz, PR gönderebilir veya issue oluşturabilirsiniz. Her türlü geri bildirim ve katkı büyük bir heyecanla karşılanacaktır!
