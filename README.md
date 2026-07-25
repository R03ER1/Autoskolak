# Autoškolák 🚗

> Autoškolák is a gamified, Duolingo-style Android application designed to help students efficiently prepare for Czech driving school theoretical exams.

<p align="center">
  <img width="22%"  alt="signal-2026-07-25-16-28-50-120_002" src="https://github.com/user-attachments/assets/9d560fa5-b978-4575-81c2-858a88acc6c7" />
  <img width="22%"  alt="signal-2026-07-25-16-28-50-120_003" src="https://github.com/user-attachments/assets/19f31fe0-aa1c-4037-8393-f0354f4262d3" />
  <img width="22%"  alt="signal-2026-07-25-16-28-50-120_004" src="https://github.com/user-attachments/assets/4c4e891d-fb0e-4865-a2ee-1f42731a8f67" />
</p>

<p align="center">
<img width="22%"  alt="signal-2026-07-25-16-28-50-120_005" src="https://github.com/user-attachments/assets/562421d9-6f17-439f-b9f0-954d19b6438b" />
<img width="22%"  alt="signal-2026-07-25-16-28-50-120_006" src="https://github.com/user-attachments/assets/20b5dcaf-6be3-417d-8888-3ab70bfcd576" />
<img width="22%"  alt="signal-2026-07-25-16-28-50-120_007" src="https://github.com/user-attachments/assets/8546f9d8-2d16-40f1-8c13-a08b0cbb34dc" />
<img width="22%"  alt="signal-2026-07-25-16-28-50-120_008" src="https://github.com/user-attachments/assets/eadfee4d-6c1f-4194-b3c7-ea532b196e64" />
</p>

## 💡 The Motivation
I started this project with zero prior programming knowledge. My goal was simple: dive into the deep end of software development by building a real, shippable product from scratch instead of just following tutorial islands. 

Over a year of development, I leveraged AI coding assistants as a pair programmer. This accelerated my learning curve exponentially, allowing me to bypass basic syntax roadblocks and focus entirely on high-level architecture, state management, and real-world debugging.

## ✨ Key Features
* **Gamified Progression:** Interactive lesson paths, daily streaks, and a virtual pet (Alex the Lion) whose hunger mechanics require users to maintain consistent study habits to earn in-game currency.
* **Mock Exams:** Timed 30-minute simulations matching the real government exam algorithms, complete with historical performance tracking and average score analytics.
* **Targeted Practice (Spaced Repetition):** Automatically tracks incorrect answers and creates custom practice sessions to target the user's specific weak points.
* **Modern UI/UX:** Fully responsive Jetpack Compose interface featuring glassmorphism elements, dynamic theming (Light/Dark mode), Lottie animations, and custom haptic feedback.

## 🛠 Tech Stack
This project evolved significantly over its lifecycle, culminating in a complete migration to a modern Android architecture:

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3), accompanied by Coil for async images and Lottie Compose for complex animations.
* **Architecture:** Single-Activity architecture utilizing Navigation Compose and MVVM patterns.
* **Local Data:** Room Database (SQLite) for exam history and `SharedPreferences` / `StateFlow` for immediate UI state reactivity.
* **Background Processing:** `WorkManager` for idempotent weekly summary calculations and custom Notification Services for virtual pet mechanics.
* **App Delivery:** Google Play Feature Delivery (Dynamic Feature Modules). Implemented `SplitInstall` API to decouple heavy media assets (videos/images) from the base APK to bypass the 200MB Google Play limit.
* **Monetization & Privacy:** Google Mobile Ads SDK (AdMob) with strict User Messaging Platform (UMP) integration for GDPR compliance.

## 🚀 Key Technical Takeaways & Lessons Learned
The biggest challenge was wrapping my head around the Android ecosystem's core concepts as a beginner. 
1. **The UI Migration:** Halfway through development, I realized the limitations of XML/Activity-based layouts and executed a complete rewrite of the UI layer into Jetpack Compose, forcing me to understand declarative UI paradigms and state hoisting.
2. **Asset Management at Scale:** Dealing with hundreds of megabytes of driving school video questions taught me how to handle module boundaries and dynamic delivery, ensuring the app remained performant and installable on lower-end devices.
3. **Lifecycle & Concurrency:** Debugging background tasks, preventing memory leaks during navigation, and ensuring smooth UI thread performance during database operations (Room IO threads) bridged the gap between writing code that "works" and code that is production-ready.
