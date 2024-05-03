# Project Name:Tic Track
## Description
This is an Android Studio application. It includes features such as user registration, login, dashboard view, adding timesheet entries, viewing timesheet entries, and uploading photos associated with timesheet entries. The project demonstrates the use of Firebase Authentication, Firestore, and Storage for user authentication, data storage, and file storage respectively.

## Installation
To run this project, follow these steps:

* Clone the repository.
* Open the project in Android Studio.
* Connect the project to a Firebase project.
* Build and run the project on an Android device or emulator.
  
# Usage
## SplashActivity
* Displays a splash screen for a specified duration.
* Automatically transitions to the MainActivity.
  
## MainActivity
* Displays buttons for registering and logging in.
* Allows users to navigate to the RegisterActivity or LoginActivity.

## RegisterActivity
* Allows users to register with an email and password.
* Validates input fields and saves user data to Firestore upon successful registration.
* Upon successful registration, navigates to the DashboardActivity.

## LoginActivity
* Allows users to log in with their registered email and password.
* Validates input fields and navigates to the DashboardActivity upon successful login.

## DashboardActivity
* Displays a bottom navigation view for navigating between profile, dashboard, and settings fragments.
* Uses fragments to display different views.
* Allows users to switch between fragments by clicking on the bottom navigation items.

## CategoryActivity
* Allows users to add categories.
* Validates input and saves categories to Firestore.

## TimesheetActivity
* Allows users to add timesheet entries including category, description, dates, times, and goals.
* Users can optionally add a photo to the timesheet entry.
* Validates input and saves timesheet entries and associated photos to Firestore and Storage respectively.

## ViewEntriesActivity
* Displays a list of timesheet entries retrieved from Firestore.
* Allows users to view details of timesheet entries.
* Retrieves and displays associated photos from Firebase Storage.

# Dependencies
* Firebase Authentication
* Firebase Firestore
* Firebase Storage

# License
This project is licensed under the MIT License.
