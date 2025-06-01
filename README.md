# WealthWave - Personal Finance Tracker
## Overview
**WealthWave** is a Personal Finance Tracker **mobile application** developed for the IT2010 – Mobile Application Development course as part of the BSc (Hons) in Information Technology, 2nd Year, 2nd Semester, 3rd MAD Project, Faculty of Computing, SLIIT (2025 – Lab Exam 03). The app is built using Kotlin and XML in Android Studio, offering an intuitive interface to help users manage their finances by tracking income, expenses, and savings. It includes features like transaction management, category-wise spending analysis, monthly budget setup, data persistence, backup/restore functionality, push notifications, and a dark theme for enhanced user experience.
This project demonstrates real-world mobile development concepts such as **data persistence** using **SharedPreferences**, internal storage for **backups**, **push notifications** via Android’s Notification Manager, and a clean, user-friendly UI/UX design.

## Features
### 1. Core Features
- Transaction Management
- **Add**, **edit**, and **delete** income and expense transactions.
- Each transaction includes a **title**, **amount**, **category** (e.g., Food, Transport, Bills, Entertainment), and **date and time**.
- Transaction analysis

### 2. Category-wise Spending Analysis
- Categorize transactions and view a summary of expenses per category.
- Visualize spending habits to help users make informed financial decisions.

### 3. Monthly Budget Setup
- Set a monthly budget and track spending progress.
- Receive warnings when spending approaches or exceeds the budget limit.

### 4. Data Persistence
- Save user preferences (e.g., currency type, budget settings) using SharedPreferences.
- Maintain transaction history that persists across app restarts.

### 5. Bonus Features
- Data Backup and Restore
- Export transaction data as a text file or JSON to internal storage.
- Restore data from a backup file for seamless recovery.

### 6. Push Notifications
- Receive alerts when nearing or exceeding the monthly budget.
- Optional reminders to record daily expenses.

### 7. Dark Theme
- Toggle between light and dark themes for better accessibility and user comfort.

## Technical Requirements

- **Programming Language**: Kotlin
- **UI Design**: XML layouts for a simple and intuitive user interface
- **Data Persistence**: **SharedPreferences** for **storing user preferences** and **transaction history**
- **Backup Storage**: Internal Storage for exporting and importing transaction data
- **Notifications**: Android Notification Manager for budget alerts and reminders
- **Platform**: Android (developed and tested on Android Studio)

## Installation and Setup
To run the WealthWave app on your device or emulator, follow these steps:

### Prerequisites
- **Android Studio**: Version 2023.3.1 or later (e.g., Koala or Ladybug)
- **JDK**: Version 17 or later
- **Android Device/Emulator**: Android API level 21 (Lollipop) or higher
- **Git**: To clone the repository

## Steps to Run

1. Clone the Repositorygit 
  ```bash
    clone https://github.com/SupunPrabodha/WealthWave.git
```
2. Open in Android Studio
3. Launch Android Studio and select Open an existing project.
4. Navigate to the cloned WealthWave directory and select it.
5. Sync Project
  - Click Sync Project with Gradle Files to download dependencies.
  - Ensure you have an active internet connection for Gradle to resolve dependencies.
6. Configure Emulator or Device
  - **Emulator**: Set up an Android Virtual Device (AVD) with API level 21 or higher.
  - **Physical Device**: Enable Developer Options and USB Debugging on your Android device, then connect it via USB.
7. **Run the App**
8. Select your target device/emulator from the device dropdown in Android Studio.
9. Click the Run button (green play icon) to build and install the app.
10. Grant Permissions
  - The app may request permissions for notifications and storage. Grant these permissions to enable backup/restore and push notification features.
11. Explore the App
  - Launch the app, set your monthly budget, add transactions, and explore features like spending analysis, dark theme, and backup/restore.

## Build and Run Notes

- Ensure the minSdkVersion in app/build.gradle is set to 21 or higher.
- If you encounter build issues, try File > Invalidate Caches / Restart in Android Studio.
- For **backup/restore**, ensure the device/emulator has sufficient internal storage.

## Project Structure

- `app/src/main/java/`: Contains Kotlin source code for app logic, including activities, fragments, and utility classes.
- `app/src/main/res/layout/`: XML layout files for the user interface.
- `app/src/main/res/values/`: Resource files for strings, colors, and themes (including dark theme support).
- `app/build.gradle`: Gradle configuration for dependencies and build settings.

## Screenshots
![1](https://github.com/user-attachments/assets/115d4574-c7bf-407f-a95f-17814054aba2)
![2](https://github.com/user-attachments/assets/d2300d87-5b0b-465e-9804-fe8a5cc9d2af)
![3](https://github.com/user-attachments/assets/ad704939-85b5-4de1-bbbb-f3ae2be7bae2)

## Contributing
This project was developed as part of a coursework assignment. Contributions are welcome for bug fixes or additional features. To contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes and commit (`git commit -m "Add feature"`).
4. Push to your branch (`git push origin feature-branch`).
5. Create a pull request.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments
- Faculty of Computing, SLIIT, for providing the project guidelines and evaluation criteria.
- Android Developer Documentation for Kotlin and XML best practices.
- Open-source libraries used in the project (listed in `app/build.gradle`).

## Contact
For inquiries or feedback, contact the project maintainer at [supunprabodha@gmail.com] or via GitHub issues.
