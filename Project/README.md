# FinancialAssistant (Android)

แอป Android สำหรับช่วยบันทึกรายรับรายจ่าย วิเคราะห์พฤติกรรมการเงิน และแนะนำแนวทางด้วย AI  
พัฒนาด้วย Kotlin + Jetpack Compose + Room

---

## 1) ภาพรวมโปรเจกต์

ฟีเจอร์หลัก:

- บันทึกรายรับ/รายจ่าย (Quick Add)
- ดู/แก้ไข/ลบประวัติธุรกรรม (History)
- ดูกราฟวิเคราะห์การเงิน (Analytics)
- จัดการหมวดหมู่ (Categories)
- ตั้งเป้าหมายการเงิน (Goals)
- คำแนะนำ AI (Welcome/Analytics/Goals)
- ตั้งชื่อผู้ใช้, ชื่อผู้ช่วย, ไอคอนผู้ช่วย (Settings)

---

## 2) โครงสร้างภายใน `Project/`

```text
Project/
├─ app/
│  ├─ src/main/
│  │  ├─ java/com/example/financialassistant/
│  │  │  ├─ WelcomeActivity.kt
│  │  │  ├─ QuickAddActivity.kt
│  │  │  ├─ HistoryActivity.kt
│  │  │  ├─ AnalyticsActivity.kt
│  │  │  ├─ CategoryActivity.kt
│  │  │  ├─ GoalActivity.kt
│  │  │  ├─ SettingsActivity.kt
│  │  │  ├─ FinancialViewModel.kt
│  │  │  ├─ SharedPrefsUtils.kt
│  │  │  ├─ ai/
│  │  │  └─ data/
│  │  ├─ res/
│  │  └─ AndroidManifest.xml
│  └─ build.gradle.kts
├─ build.gradle.kts
├─ local.properties   # local-only (ห้าม push key)
└─ server/            # ตัวอย่าง proxy (ถ้าต้องการ)
```

---

## 3) Setup บนคอมเครื่องใหม่

## 3.1 ติดตั้งเครื่องมือ

- Android Studio
- Android SDK
- Git

## 3.2 Clone และเปิดโปรเจกต์

```bash
git clone https://github.com/Ratchanon-oof12/CP213_526LearnAndroid.git
cd CP213_526LearnAndroid
```

จากนั้นเปิดโฟลเดอร์ `Project/` ใน Android Studio

## 3.3 ตั้งค่า `local.properties`

สร้าง/แก้ไฟล์ `Project/local.properties`:

```properties
sdk.dir=C\:\\Users\\<YOUR_USER>\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=YOUR_RESTRICTED_KEY
GEMINI_MODEL=gemma-4-26b-a4b-it
```

> ควรใช้ key ที่ restrict แล้ว (package name + SHA-1)

## 3.4 Run

- Sync Gradle
- เลือก emulator/มือถือจริง
- กด Run

---

## 4) หมายเหตุความปลอดภัย

- อย่า commit `local.properties`
- อย่าแชร์ API key
- ถ้าจะปล่อยแอปสาธารณะ แนะนำเรียก AI ผ่าน backend/proxy

