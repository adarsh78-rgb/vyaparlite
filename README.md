# Vyapar Lite (Android, Offline-First)

Vyapar Lite is a minimalist Android app for small Indian shop owners (kirana, medical, vendors, local stores) to run daily operations without login/signup.

## 1) Full App Architecture

- `presentation` (Jetpack Compose)
- `data` (Room + repositories)
- `navigation` (bottom-tab route graph)
- `ads` (AdMob banner integration)
- `utils` (formatters)

### Layered structure

- `ui/screens/*`:
  - `DashboardScreen`
  - `InventoryScreen`
  - `EmployeesScreen`
  - `SalesScreen`
  - `AnalyticsScreen`
  - `SettingsScreen`
- `data/local/*`:
  - Room entities, DAOs, database
- `data/Repositories.kt`:
  - Inventory, employee, sales, analytics, settings repositories
- `ui/navigation/*`:
  - Route definitions and main nav host
- `ads/BannerAd.kt`:
  - Reusable AdMob banner composable

## 2) Screens List

- Dashboard
- Inventory Management
- Employee Management
- Sales Tracking
- Analytics Dashboard
- Settings (Customization + Backup/Restore)

## 3) Database Structure (Room)

- `categories`
  - `id`, `name`, `isDefault`
- `products`
  - `id`, `name`, `categoryId`, `quantity`, `purchasePrice`, `sellingPrice`, `lowStockThreshold`, `updatedAt`
- `employees`
  - `id`, `name`, `role`, `salary`, `contactNumber`, `isPresent`, `attendanceUpdatedAt`
- `salary_payments`
  - `id`, `employeeId`, `amount`, `paidAt`, `note`
- `sales`
  - `id`, `totalAmount`, `totalProfit`, `createdAt`
- `sale_items`
  - `id`, `saleId`, `productId`, `quantity`, `salePrice`, `purchasePrice`
- `expenses`
  - `id`, `title`, `amount`, `createdAt`
- `app_settings`
  - `key`, `value`

## 4) UI Layout (Minimalist)

- Clean Material 3 compose UI
- Large action buttons on dashboard
- List-card layout for all core modules
- Search/filter in inventory
- Low stock visual text alert
- Non-intrusive ad banners in dashboard + analytics

## 5) Feature Flow

- App launch:
  - Direct open to Dashboard, no auth
- Inventory:
  - Add/edit/delete products
  - Category filter + search
  - Low stock count and alerts
- Employees:
  - Add/edit/delete employees
  - Attendance present/absent toggle
  - Salary payment entry
- Sales:
  - Item-wise sale entry
  - Daily expense entry
  - Profit computed from purchase vs sale price
  - Stock auto-reduced after sale
- Analytics:
  - Daily/weekly/monthly revenue
  - Monthly profit vs expenses
  - Top-selling products
  - Inventory summary
- Settings:
  - Category rename/delete
  - Currency symbol change (default `₹`)
  - Custom field names add/remove
  - Local backup and restore JSON

## 6) AdMob Integration Plan

- Dependency: `com.google.android.gms:play-services-ads`
- App ID in `AndroidManifest.xml` meta-data
- `MobileAds.initialize(this)` in `MainActivity`
- Banner composable `BannerAd` based on `AdView`
- Placement:
  - Bottom section of dashboard
  - Analytics screen block
- Current IDs are Google test IDs; replace with production Ad Unit IDs before release.

## 7) Step-by-Step Build Plan

1. Create Android project with Kotlin + Compose + Room.
2. Add local entities/DAOs/repositories for inventory, employees, sales, expenses, settings.
3. Build dashboard-first no-login navigation shell.
4. Implement CRUD inventory and category filter/search.
5. Implement employee + attendance + salary payment flow.
6. Implement sale + sale items + expenses and stock reduction logic.
7. Add analytics aggregation with charts/cards.
8. Add customization: currency, categories, custom fields.
9. Add local backup/restore JSON support.
10. Integrate AdMob test banners and verify placement.
11. QA for offline use, large touch targets, fast flow for non-technical users.
12. Replace AdMob test IDs + sign release APK.

## Build

From `VyaparLite/`:

```bash
./gradlew assembleDebug
```

If wrapper is missing, generate it using a local Gradle install:

```bash
gradle wrapper
./gradlew assembleDebug
```
