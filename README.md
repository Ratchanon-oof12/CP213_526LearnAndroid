# FinancialAssistant (Android)

แอป Android สำหรับช่วยบันทึกรายรับรายจ่าย วิเคราะห์พฤติกรรมการเงิน และแนะนำแนวทางด้วย AI  
พัฒนาด้วย Kotlin + Jetpack Compose + Room

---

## 1) ภาพรวมโปรเจกต์

โปรเจกต์นี้เป็นแอปผู้ช่วยการเงินส่วนบุคคล โดยมีฟีเจอร์หลักดังนี้:

- บันทึกรายการรายรับ/รายจ่ายอย่างรวดเร็ว (Quick Add)
- ดูประวัติธุรกรรม พร้อมค้นหา/แก้ไข/ลบ (History)
- ดูกราฟวิเคราะห์รายเดือน/รายวัน/หมวดหมู่ (Analytics)
- จัดการหมวดหมู่รายจ่ายเองได้ (Categories)
- ตั้งเป้าหมายการเงินและอัปเดตความคืบหน้า (Goals)
- รับคำแนะนำจาก AI (Welcome/Analytics/Goals)
- ตั้งชื่อผู้ใช้, ชื่อผู้ช่วย, และไอคอนผู้ช่วยได้จากหน้า Settings

---

## 2) เทคโนโลยีที่ใช้

- **ภาษา**: Kotlin
- **UI**: Jetpack Compose + Material3
- **ฐานข้อมูลในเครื่อง**: Room (SQLite)
- **State**: ViewModel + StateFlow
- **AI**: Google AI Studio (Gemini API / Gemma model ผ่าน direct API หรือ proxy)
- **Build**: Gradle Kotlin DSL (`build.gradle.kts`)

---

## 3) โครงสร้างโปรเจกต์ (สำคัญ)

```text
CP213_526LearnAndroid/
├─ Project/
│  ├─ app/
│  │  ├─ src/main/
│  │  │  ├─ java/com/example/financialassistant/
│  │  │  │  ├─ WelcomeActivity.kt
│  │  │  │  ├─ QuickAddActivity.kt
│  │  │  │  ├─ HistoryActivity.kt
│  │  │  │  ├─ AnalyticsActivity.kt
│  │  │  │  ├─ CategoryActivity.kt
│  │  │  │  ├─ GoalActivity.kt
│  │  │  │  ├─ SettingsActivity.kt
│  │  │  │  ├─ FinancialViewModel.kt
│  │  │  │  ├─ SharedPrefsUtils.kt
│  │  │  │  ├─ ai/
│  │  │  │  │  ├─ AiClient.kt
│  │  │  │  │  ├─ AiInsightsCard.kt
│  │  │  │  │  ├─ AiModels.kt
│  │  │  │  │  └─ AiSnapshotBuilder.kt
│  │  │  │  └─ data/
│  │  │  │     ├─ AppDatabase.kt
│  │  │  │     ├─ FinancialRepository.kt
│  │  │  │     ├─ Transaction.kt / TransactionDao.kt
│  │  │  │     ├─ Category.kt / CategoryDao.kt
│  │  │  │     └─ SeedData.kt
│  │  │  ├─ res/...
│  │  │  └─ AndroidManifest.xml
│  │  ├─ build.gradle.kts
│  │  └─ proguard-rules.pro
│  ├─ build.gradle.kts
│  ├─ local.properties   # ไฟล์ local-only (ห้าม push key)
│  └─ gradle/...
└─ README.md
```

---

## 4) การไหลของข้อมูลโดยย่อ

1. UI (Activity/Compose) เรียก `FinancialViewModel`  
2. ViewModel ดึง/อัปเดตข้อมูลผ่าน `FinancialRepository`  
3. Repository ทำงานกับ Room DAO (`TransactionDao`, `CategoryDao`)  
4. หน้า AI จะสร้าง snapshot จากข้อมูลสรุป แล้วส่งให้ `AiClient`  
5. `AiClient` เรียก Gemini API และแปลงผลเป็น `title/summary/suggestions`

---

## 5) วิธีติดตั้งและรันบนคอมเครื่องใหม่

## 5.1 สิ่งที่ต้องติดตั้ง

- Android Studio (เวอร์ชันใหม่)
- Android SDK ที่ตรงกับโปรเจกต์
- JDK ที่ Android Studio แนะนำ
- Git

## 5.2 Clone โปรเจกต์

```bash
git clone https://github.com/Ratchanon-oof12/CP213_526LearnAndroid.git
cd CP213_526LearnAndroid
```

## 5.3 เปิดโปรเจกต์ใน Android Studio

- เปิดโฟลเดอร์ `Project/`
- รอ Gradle Sync ให้เสร็จ

## 5.4 ตั้งค่าไฟล์ `local.properties` (สำคัญมาก)

ไฟล์นี้เป็น local-only และ **ไม่ควร push ขึ้น Git**

ตัวอย่าง:

```properties
sdk.dir=C\:\\Users\\<YOUR_USER>\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=YOUR_NEW_RESTRICTED_KEY
GEMINI_MODEL=gemma-4-26b-a4b-it
```

> แนะนำให้ใช้ API key ใหม่ที่ทำการจำกัดสิทธิ์ (restrict) แล้วเสมอ

## 5.5 รันแอป

- เลือก emulator หรือเสียบมือถือจริง
- กด Run (`Shift + F10`)

---

## 6) การตั้งค่า AI ให้ปลอดภัย

ในโปรเจกต์นี้รองรับการเรียก AI แบบตรงจากแอป (direct API)  
ดังนั้นควรทำดังนี้:

- สร้าง API key ใหม่
- จำกัดสิทธิ์ key ให้เหลือเฉพาะ API ที่จำเป็น
- จำกัดแอปปลายทาง (package name + SHA-1)
- อย่าใส่ key ลงไฟล์ที่ถูก commit

> ถ้าจะปล่อยแอปสาธารณะ แนะนำย้ายการเรียก AI ไปผ่าน backend/proxy

---

## 7) ฟีเจอร์รีเซ็ตข้อมูล (Welcome Footer)

ปัจจุบันมี 3 ปุ่ม:

1. **Delete All Info + Seed Data**  
   ลบข้อมูลทั้งหมด แล้วสร้างหมวดหมู่เริ่มต้นกลับมา

2. **Delete All Info (No Seed)**  
   ลบข้อมูลทั้งหมดและไม่เติมข้อมูลเริ่มต้น

3. **Delete Username + Financial Name**  
   ลบเฉพาะชื่อผู้ใช้และชื่อผู้ช่วย

---

## 8) ปัญหาที่พบบ่อย

- **AI Timeout / ช้า**  
  ตรวจเน็ต, quota, หรือ model ที่เลือกใน `GEMINI_MODEL`

- **Rate limit (429)**  
  รอสักพักแล้วลองใหม่ หรือใช้ model ที่เบากว่า/เร็วกว่า

- **ชื่อผู้ใช้แสดงแปลกตอนเข้าแอปครั้งแรก**  
  แก้แล้วในเวอร์ชันปัจจุบัน (animation reset ตามชื่อใหม่)

---

## 9) ข้อเสนอแนะการพัฒนาต่อ

- เพิ่มหน้าจัดการโปรไฟล์เต็มรูปแบบ
- เพิ่ม Export/Import ข้อมูล
- เพิ่มระบบ budget alert รายเดือน
- เพิ่มโหมด offline AI fallback / cached insights

---

## 10) License

ใช้เพื่อการเรียนและพัฒนาภายในโปรเจกต์นี้เป็นหลัก  
หากจะนำไปใช้งานจริงเชิงพาณิชย์ ควรตรวจสอบเงื่อนไขโมเดล/API เพิ่มเติม

# CP213_526LearnAndroid

