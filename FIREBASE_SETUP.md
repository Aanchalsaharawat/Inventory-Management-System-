# 🔥 Firebase Setup Guide for IMS

Follow these steps to connect the Inventory Management System to Firebase.

---

## Step 1 – Create a Firebase Project

1. Go to [https://console.firebase.google.com](https://console.firebase.google.com)
2. Click **"Add project"**
3. Enter a project name (e.g., `ims-capstone`)
4. Disable Google Analytics (optional for demo)
5. Click **"Create project"**

---

## Step 2 – Enable Realtime Database

1. In your Firebase console, select your project
2. In the left sidebar, click **"Build" → "Realtime Database"**
3. Click **"Create Database"**
4. Choose **"Start in test mode"** (allows open read/write for development)
5. Select your region and click **"Enable"**

---

## Step 3 – Copy Your Database URL

After enabling, you'll see a URL like:
```
https://ims-capstone-default-rtdb.firebaseio.com/
```

Copy this URL (without the trailing `/`).

---

## Step 4 – Update FirebaseConfig.java

Open `IMS/src/firebase/FirebaseConfig.java` and replace:
```java
public static final String DATABASE_URL = "https://YOUR-PROJECT-ID-default-rtdb.firebaseio.com";
```
with your actual URL:
```java
public static final String DATABASE_URL = "https://ims-capstone-default-rtdb.firebaseio.com";
```

---

## Step 5 – Set Database Rules (Development)

In Firebase Console → Realtime Database → **Rules** tab, set:
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```
Click **Publish**.

> ⚠️ **Note:** These open rules are only for development/demo.  
> For production, restrict access with authentication rules.

---

## Step 6 – Seed Initial Data (Optional)

The app auto-creates the default admin user on first launch.  
You can also manually add data in Firebase Console → **Data** tab:

```
users/
  admin001/
    username: "admin"
    password: "admin123"
```

---

## Step 7 – Test Connection

Run the app (`bash build.sh`) and try logging in with:
- **Username:** `admin`
- **Password:** `admin123`

If login fails, check:
1. `DATABASE_URL` is correct in `FirebaseConfig.java`
2. Firebase rules are set to open (Step 5)
3. Your internet connection is active

---

## 🗄 Full Database Schema

```
/users/
  {userId}/
    username    : String
    password    : String

/products/
  {productId}/
    productId   : String
    name        : String
    quantity    : Number
    price       : Number
    supplierId  : String

/suppliers/
  {supplierId}/
    supplierId  : String
    name        : String
    contact     : String

/sales/
  {saleId}/
    saleId      : String
    productId   : String
    productName : String
    quantity    : Number
    totalPrice  : Number
    date        : String (yyyy-MM-dd HH:mm:ss)
```

---

## 🌐 How REST API Calls Work

The app communicates with Firebase using HTTP:

| Operation | HTTP Method | Example URL |
|-----------|-------------|-------------|
| Read all products | GET | `{DB_URL}/products.json` |
| Add/replace product | PUT | `{DB_URL}/products/P001.json` |
| Update fields | PATCH | `{DB_URL}/products/P001.json` |
| Delete product | DELETE | `{DB_URL}/products/P001.json` |
| Add sale | PUT | `{DB_URL}/sales/SALE001.json` |

No Firebase SDK required — all via standard `HttpURLConnection`.
