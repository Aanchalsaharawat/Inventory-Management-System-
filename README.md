<<<<<<< HEAD
# 📦 Inventory Management System (IMS)

A **fully functional capstone project** built with:
- **Core Java + OOP** (Encapsulation, Inheritance, Abstraction, Polymorphism)
- **Java Swing** GUI with dark theme
- **Firebase Realtime Database** (REST API via `HttpURLConnection`)
- **org.json** for JSON parsing
- **Role-Based Access Control** (RBAC) – admin vs user privileges

---

## 🗂 Project Structure

```
IMS/
├── src/
│   ├── Main.java                        ← Application entry point
│   ├── DataSeeder.java                  ← Seed sample data into Firebase
│   ├── model/
│   │   ├── Product.java
│   │   ├── Supplier.java
│   │   ├── Sale.java
│   │   └── User.java
│   ├── view/
│   │   ├── UITheme.java                 ← Centralized design tokens
│   │   ├── LoginView.java
│   │   ├── DashboardView.java           ← Role-aware home with clickable tiles
│   │   ├── ProductView.java
│   │   ├── SupplierView.java
│   │   ├── BillingView.java
│   │   ├── StockView.java
│   │   ├── ReportsView.java
│   │   └── UserManagementView.java      ← Admin-only user CRUD
│   ├── controller/
│   │   ├── LoginController.java
│   │   ├── ProductController.java
│   │   ├── SupplierController.java
│   │   ├── BillingController.java
│   │   ├── ReportsController.java
│   │   └── UserController.java          ← User CRUD operations
│   └── firebase/
│       ├── FirebaseConfig.java          ← Set your DATABASE_URL here
│       └── FirebaseHelper.java          ← REST GET/POST/PUT/PATCH/DELETE
├── lib/
│   └── json-20231013.jar                ← Auto-downloaded by build script
├── pom.xml                              ← Maven configuration
├── build.sh                             ← Mac/Linux build & run
├── build.bat                            ← Windows build & run
├── FIREBASE_SETUP.md
└── README.md
```

---

## ⚡ Quick Start

### Prerequisites
- **JDK 11+** installed
- **Maven** installed ([download here](https://maven.apache.org/download.cgi))

### Step 1 – Configure Firebase
Edit `src/firebase/FirebaseConfig.java` and set your database URL:
```java
public static final String DATABASE_URL = "https://YOUR-PROJECT-ID-default-rtdb.firebaseio.com";
```
See `FIREBASE_SETUP.md` for complete Firebase setup instructions.

### Step 2 – Build & Run (Mac/Linux)
```bash
cd IMS
chmod +x build.sh
bash build.sh
```

### Step 2 – Build & Run (Windows)
```cmd
cd IMS
build.bat
```

Or manually with Maven:
```bash
mvn clean compile exec:java
```

### Step 3 – Login
Default admin account (auto-created on first run):
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** `admin`

Default user account (seeded via `DataSeeder`):
- **Username:** `user`
- **Password:** `user123`
- **Role:** `user`

---

## 🖥 GUI Screens

| Screen | Description | Access |
|--------|-------------|--------|
| **Login** | Authenticate with Firebase credentials | All |
| **Dashboard** | Colorful tile grid; click to jump to any module | All |
| **Products** | Full CRUD – add, edit, delete, view products | All |
| **Suppliers** | Add and manage suppliers | Admin only |
| **Billing** | Select product → +/- qty → calculate → confirm sale → receipt | All |
| **Stock** | Real-time stock view with low-stock alerts (qty < 5) | Admin only |
| **Reports** | Daily sales + revenue summary + inventory status | Admin only |
| **Users** | Admin can add/delete system users | Admin only |

---

## 🔐 Role-Based Access Control

| Feature | Admin | User |
|---------|:-----:|:----:|
| Products CRUD | ✅ | ✅ |
| Billing & Sales | ✅ | ✅ |
| Supplier Management | ✅ | ❌ |
| Stock Monitoring | ✅ | ❌ |
| Reports & Analytics | ✅ | ❌ |
| User Management | ✅ | ❌ |

- Tabs are automatically hidden for non-admin users.
- Dashboard tiles are filtered by role.

---

## ✨ Key Features

### Dashboard
- **2×3 colorful square tile grid** for admin (1×2 for users)
- Each tile is **clickable** with hover effects and navigates directly to the module
- Tiles are **role-aware** — restricted services are hidden for regular users

### Billing & Sales
- **+ / − stepper buttons** for quantity input
- Real-time stock display with color-coded availability
- **Auto-generated receipt** after confirming a sale
- **Stock rollback** if Firebase sale save fails
- Form state is preserved after sale; click **Reload** to reset

### Product Management
- Supplier dropdown auto-syncs when selecting a product row
- Low-stock products highlighted in the table

### Admin User Management
- Add new users with custom or auto-generated ID
- Assign role (`admin` or `user`)
- Delete existing users
- All changes sync to Firebase in real time

### Data Seeding
- Run `DataSeeder.java` once to populate Firebase with:
  - 2 users (admin + regular)
  - 3 suppliers
  - 6 products (1 low-stock)
  - 4 sample sales

---

## 🔵 OOP Concepts Demonstrated

| Concept | Where |
|---------|-------|
| **Encapsulation** | All model classes (`Product`, `Supplier`, `Sale`, `User`) |
| **Abstraction** | All controllers hide Firebase logic from views |
| **Inheritance** | All views extend `JPanel`; `JTable` subclassed in `StockView` |
| **Polymorphism** | `SwingWorker<T>` generics; `JTable` renderer overrides |
| **Composition** | `DashboardView` embeds sub-views in a tabbed pane |

---

## 🔧 Technical Details

- **Maven** build system – dependency management via `pom.xml`
- **No external database driver** needed – Firebase is accessed via HTTPS REST
- **No Firebase SDK** – pure `HttpURLConnection` calls
- **Thread safety** – all Firebase calls done in `SwingWorker` threads; UI updates via `SwingUtilities.invokeLater()`
- **Rollback** – billing controller rolls back stock if sale save fails
- **Auto-seed** – default admin user created on first launch with role patching for legacy data

---

## 📋 Firebase Database Structure

```
/users/{userId}
    userId, username, password, role

/products/{productId}
    productId, name, quantity, price, supplierId

/suppliers/{supplierId}
    supplierId, name, contact

/sales/{saleId}
    saleId, productId, productName, quantity, totalPrice, date
```

----

## ⚠️ Notes

- Passwords stored as **plain text** – acceptable for dev/demo; use hashing in production
- Firebase rules set to **open** during development (see `FIREBASE_SETUP.md`)
- Requires **JDK 11+**
- If upgrading from an older version without roles, the app will auto-patch the admin role on startup
=======
# Inventory-Management-System-
>>>>>>> 10424e6798d0222be576c5450eb1ec8ce27e88f6
