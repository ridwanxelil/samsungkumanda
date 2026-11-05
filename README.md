# Samsung Kumanda (samsungkumanda) — v1.2.1

IR vericili Android telefonlar için Samsung TV kızılötesi kumandası.

## Derleme
1. Android Studio'da **Open** ile `samsungkumanda` klasörünü açın.
2. **Build > Build APK(s)** veya telefonu bağlayıp **Run**.

## Özellikler
- Gerçek kumanda görünümü: güç/HDMI/kaynak, kısayollar, rakamlar, SES/KANAL rokerleri,
  daire yön tuşu + OK, geri/ana sayfa/menü, çıkış/araçlar/uyku/boyut, renkli ve medya tuşları
- Ses, kanal ve yön tuşlarında basılı tutunca tekrar
- **Klavye ile yazma:** alta dokununca telefon klavyesi açılır; harfler TV'ye rakam tuşlarıyla
  (çoklu dokunuş) gönderilir. ↵ Enter gönderir. Silme TV'ye gönderilmez.

## Yapı
- `ir/SamsungProtocol.kt`, `ir/IrTransmitter.kt` — IR gönderimi
- `RemoteKey.kt` — tuş ve komut kodları
- `KeyStyle.kt` — renkler/şekiller · `DpadView.kt` — yön halkası
- `RemoteUi.kt` — arayüz · `TextEntry.kt` — klavye → rakam tuşları
- `RepeatTouchListener.kt` — basılı tutma

## Notlar
- Bazı ek tuş kodları (HDMI, Kanal Listesi, Favoriler, TTX, Uyku, Boyut) yaygın Samsung kod
  listelerinden alınmıştır; modele göre çalışmayabilir.
- Çoklu dokunuş yazma, TV'nin ekran klavyesi rakam tuşlarını kabul ettiğinde çalışır.
  Türkçe harfler (ç, ş, ğ, ı, ö, ü) ASCII karşılıklarıyla gönderilir.

## Çevrimiçi APK üretme (GitHub Actions)
1. GitHub'da yeni bir depo açın ve bu klasörün **içindekileri** (`.github`, `app`, `gradle`, `build.gradle.kts` …) depo köküne yükleyin.
2. **Actions** sekmesinde "APK derle" iş akışı kendiliğinden çalışır (veya **Run workflow** deyin).
3. Bitince çalıştırmanın altındaki **Artifacts** bölümünden `samsungkumanda-debug-apk` dosyasını indirin, içinden APK çıkar.
4. APK'yı telefona atıp kurun (bilinmeyen kaynaklardan kuruluma izin verin).
