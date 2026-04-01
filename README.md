# ANC Rural Health - Android Application

A simple and user-friendly Android mobile application designed for rural areas to track Antenatal Care (ANC) visits, send reminders, and monitor compliance based on WHO 2016 guidelines.

## Overview

This application addresses poor attendance and health-seeking behavior for regular antenatal care visits in rural India by providing:
- Gestation-aware digital reminders
- Real-time compliance tracking
- Automated alerts for missed visits
- District-level pregnancy registry

## Features

### Core Functionality
- **Pregnancy Registration**: Capture demographic details, LMP/EDD, and calculate gestational age
- **ANC Schedule Engine**: Automatically schedules 4 ANC visits based on WHO 2016 guidelines
  - ANC1 (8-16 weeks): Confirmation and baseline screening
  - ANC2 (20-24 weeks): PIH and anemia screening
  - ANC3 (28-32 weeks): Multiple pregnancy exclusion
  - ANC4 (36-40 weeks): Birth preparedness
- **Reminder System**: Automated notifications 7 days and 2 days before scheduled visits
- **Compliance Tracking**: Tracks visit completion and flags defaulters
- **Dashboards**: Provider and district-level dashboards with key metrics

### Technical Features
- **Offline Capability**: Full offline support with Room database
- **Multi-language Support**: Hindi and English language support
- **Role-based Access Control**: Different access levels for:
  - Pregnant Women
  - ASHA/ANM/Nurse
  - Medical Officers
  - District Administrators
- **Data Encryption**: Secure data storage using Android Security library
- **Background Workers**: Automated reminder and compliance checks

## Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Background Tasks**: WorkManager
- **UI**: Material Design Components
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/anc/ruralhealth/
│   │   ├── data/
│   │   │   ├── database/      # Database and converters
│   │   │   ├── dao/           # Data Access Objects
│   │   │   └── entity/        # Database entities
│   │   ├── repository/        # Repository layer
│   │   ├── ui/                # UI components
│   │   ├── utils/             # Utility classes
│   │   ├── notification/      # Notification handling
│   │   └── worker/            # Background workers
│   └── res/
│       ├── layout/            # XML layouts
│       ├── values/            # Strings, colors, themes (English)
│       └── values-hi/         # Hindi translations
```

## Database Schema

### Main Entities
1. **PregnancyEntity**: Stores pregnancy registration details
2. **ANCVisitEntity**: Tracks scheduled and completed visits
3. **ReminderEntity**: Manages notification reminders
4. **UserEntity**: User management with role-based access
5. **ComplianceLogEntity**: Tracks compliance and generates alerts

## Key Components

### ANC Schedule Calculator
- Calculates gestational age from LMP
- Generates visit schedule based on WHO guidelines
- Calculates reminder dates
- Generates unique pregnancy IDs

### Notification System
- Push notifications for visit reminders
- Missed visit alerts
- Configurable notification channels

### Compliance Tracking
- Automatic detection of missed visits
- Alert escalation to providers and district administrators
- Compliance metrics and reporting

## Setup Instructions

1. **Prerequisites**
   - Android Studio Arctic Fox or later
   - JDK 17
   - Android SDK 34

2. **Clone and Build**
   ```bash
   git clone <repository-url>
   cd "Mobile App"
   ./gradlew build
   ```

3. **Run the Application**
   - Open project in Android Studio
   - Connect Android device or start emulator
   - Click Run button or use `./gradlew installDebug`

## User Roles

### Pregnant Woman
- View own pregnancy details
- Check upcoming visits
- Receive reminders

### ASHA/ANM/Nurse
- Register pregnancies
- Record visit details
- View assigned pregnancies
- Track compliance

### Medical Officer
- View all pregnancies in area
- Monitor high-risk cases
- Review compliance reports

### District Administrator
- District-wide dashboard
- Coverage metrics
- Compliance trends
- Export reports

## Design Principles for Rural Users

1. **Large Text**: 18sp minimum for easy reading
2. **Simple Navigation**: Bottom navigation with clear icons
3. **Visual Indicators**: Color-coded status (green=completed, red=missed, blue=upcoming)
4. **Minimal Input**: Pre-filled data where possible
5. **Offline First**: Works without internet connection
6. **Multi-language**: Hindi and English support

## Future Enhancements (Out of Scope for Phase 1)

- Clinical decision support
- Financial incentives tracking
- Telemedicine integration
- SMS gateway integration
- Advanced analytics

## Success Metrics

- Increased ANC attendance rates
- Reduced missed visits
- Improved provider response times
- Better compliance tracking

## License

[Add appropriate license]

## Contact

[Add contact information]

## Acknowledgments

Based on WHO 2016 ANC guidelines for improving maternal and neonatal health outcomes in rural India.