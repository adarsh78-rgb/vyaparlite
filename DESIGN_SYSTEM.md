# Vyapar Lite Design System

## UI Design System
- Style: Material 3 + business-minimal
- Visual language: rounded cards, soft elevation, gradient accents
- Motion: quick fade/scale transitions for splash and dashboard reveal
- Offline-first hierarchy: high-contrast text, large touch targets, simple one-handed flows
- Language friendliness: short labels that work in English + Hindi context

## Color Palette
- Primary Gradient: `#16A34A` (green) -> `#0EA5E9` (blue)
- Mint Accent: `#BBF7D0`
- Sky Accent: `#BAE6FD`
- Background: `#F4F7F7`
- Card Surface: `#FFFFFF`
- Text Primary: `#0F172A`
- Text Secondary: `#475569`
- Success: `#15803D`
- Warning: `#F59E0B`
- Danger: `#DC2626`

## Fonts
- Family: Sans-serif (clean, readable on low-end devices)
- Headline: 28-32sp, semibold/bold
- Title: 16-20sp, medium/semibold
- Body: 14-16sp, regular
- Tap labels: minimum 14sp

## Layout Grid
- Base spacing scale: 4, 8, 12, 16, 24dp
- Screen padding: 16dp
- Card corner radius: 16-22dp
- Minimum touch target: 52dp height
- Card elevation: 4-8dp soft shadow
- Two-column quick actions on dashboard

## Component Library
- `MetricCard`: KPI summary with soft gradient tint
- `QuickActionTile`: icon + label action card
- `StockIndicatorBar`: animated stock health progress
- `MiniBarChart`: compact bar chart (day/week/month)
- `BannerAd`: bottom AdMob banner
- `NativeAdPlaceholder`: in-feed sponsored card slot

## Screen Design Notes
- Splash: centered logo + tagline on gradient background
- Dashboard: KPI stack, quick actions grid, native ad between content blocks, banner ad at bottom
- Inventory: searchable product cards with icon + stock indicator + price
- Sales Entry: minimal form, item selection, quantity stepper, profit auto-calc
- Employees: card list with salary and attendance toggle
- Analytics: revenue chart + expense summary + top products + banner ad
- Settings: currency, backup/restore, categories guidance, ad preview toggle
