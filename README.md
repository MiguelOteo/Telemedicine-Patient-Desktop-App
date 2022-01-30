# Telelepsia Patient Desktop App
## TelemedicineProject (Java 11)
<div align="center">
<img src="readme/documentationDependencies/logo.png" alt="drawing" width="350" padding=100%/>  
</div>

-------------------------------

### Index

1. Introduction
2. About the Project
3. Project Set-Up 
3. Patient Manual

-------------------------------
-------------------------------

## 1. Introduction

This is part of a teledicine project together with two other repositories whose purpose is the supervision from the patient’s home of a chronic disease, in our case Epilepsy. This part of the project is the desktop Java application used by a medical personnel to access the medical data of patients as well ass their data recorded using a BITalino which is stored in a remote server.

### Project Repositories

1. Telemedicine-Doctor-Desktop-App: https://github.com/MiguelOteo/Telemedicine-Doctor-Desktop-App
2. Telemedicine-Patient-Desktop-App: https://github.com/MiguelOteo/Telemedicine-Patient-Desktop-App
3. Telemedicine-Rest-API: https://github.com/MiguelOteo/Telemedicine-Rest-API

-------------------------------
-------------------------------

## 2. About the Project

### About the programming languages:

* Java, version 11 to use more updated libraries
* FXML for the layout files
* XML in the pom.xml for maven project structure, dependencies and compilation process

### About the operation system:
This application can run in every operation system (Windows, OSX, Linux...) but the connection with BITalino only works in Windows, becasue the libraries. If this program will run in a Doctor computer this will not be a problem, but if the program need the BITalino connection to register data, as in Patients computers, only can be used with Windows.

### About the BITalino:
There is a Library needed for the BITalino connection "bluecove-2.1.1.jar". (For Windows).

### JavaFX Project Maven dependencies

This project uses the following Maven dependencies

1. gson dependency: https://mvnrepository.com/artifact/com.google.code.gson/gson
2. json dependency: https://mvnrepository.com/artifact/org.json/json
3. jfoenix 9.0.1 dependency: https://mvnrepository.com/artifact/com.jfoenix/jfoenix/9.0.1
4. bluecove 2.1.1 dependency: https://mvnrepository.com/artifact/io.ultreia/bluecove/2.1.1
5. chartfx-chart 11.5.1 dependency: https://mvnrepository.com/artifact/de.gsi.chart/chartfx-chart/11.1.5
6. slf4j-simple 2.0.0-alpha0 dependency: https://mvnrepository.com/artifact/org.slf4j/slf4j-simple/2.0.0-alpha0
7. controlsfx 11.1.0 dependency: https://mvnrepository.com/artifact/org.controlsfx/controlsfx/11.1.0

-------------------------------
-------------------------------

## 3. Project Set-Up and compilation

### Project Set-Up

#### On Eclipse
The repository constains the .classpath file from Eclipse IDE, this project should build itself when opened with this IDE without any additional steps.

#### On other IDEs
There could be problems with the structure of the project when opened from a different IDE, you might need to stablish the structure of the project manually.

### Porject compilation

The pom.xml file is designed to compile the project into a fat-jar constaining all the dependencies needed for the project to work, remember that the project will only run on Java 11 or newer.

-------------------------------
-------------------------------

## 4. Patient Manual  

Once everything has been downloaded, installed and works properly, you only have to run the project and one window with a friendly user interface will be opened, as the following:

<div align="center">
<img src="readme/documentationDependencies/login_view.png" alt="drawing" width="600" padding=100%/>  
</div>

Depending on your role you can interact with the application in a different way, you can use the application as a Doctor or as a Patient.

------

### 3.1 Patient Manual:

Before the application installation and the use of it, it's important to say to the patient that something to bear in mind is that this system has a Hardware dependency; this application works with the BITalino board to recover physiological data of the patient.
<div align="center">
<img src="readme/documentationDependencies/bita.jpeg" width="300" border-radius=5% padding=100% />  
</div>  


1. If you haven't got an account created yet (keep reading, in the other case go to step 4), you have to click in the "Sign Up" button, to register a new account. 

2. Once you clicking on the button a new tab is shown requesting to know some data to create the new account. It's important to bear in mind that every bound must be filled with the data required. After all was be completed you can click on the "Create account" button.

<div align="center">
<img src="readme/documentationDependencies/patient_regis_view.png" width="600" border-radius=5% padding=100% />  
</div>  

3.  After all were being completed, you can click on the "Create account" button and a message will appear, indicating that the Patient account have been created, that's mean that everything goes successfully.

<div align="center">
<img src="readme/documentationDependencies/patient_created_view.png" width="600" border-radius=5% padding=100% />  
</div>  

4. Once, you, as a Patient, have an account created you can proceed to do the "Login" process, in the main menu. You only have to introduce your email and the password introduced in the "Sign up" process.

5. When you are in, a Health Insurance number  is required. This is the id that each Patient must to have as a part of a society with healthcare system. The Health Insurance number consists of 8 digits following by a capital letter.  
For example: "12345678T".

<div align="center">
<img src="readme/documentationDependencies/patient_id_view.png" width="600" border-radius=5% padding=100% />  
</div>  

6. [**BITalino Connection**]
 If everything has gone well, you are now in the main window of the patient. Here you can do a lot of things, but, if it is the first time that you enter you must connect your BITalino board to the application.  
 In this process you should have your BITalino board in hand, and follow the next steps:  
 6.1 Switch on the board.
 6.2 Enter into the BITalino Connection in Telelepsia Application.
 6.3 Wait 10 seconds while the program search BITalinos around you.
 6.4 Your BITalino should appear on the screen with its corresponding MAC address. 

<div align="center">
<img src="readme/documentationDependencies/treetableview.png" width="600" border-radius=5% padding=100% />  
</div>  


7.[**BITalino Recording**]
To start to record your physiological data you have to connect your BITalino first. So if you don't, back to the last step to do it.
Once the BITalino is connected you must go to the "Parameters record" tab. At this moment you can click on the "START RECORDING" button, and the recorder process will start.

In this interface, you can observe the graph in the first half of the page and in the second one 2 spaces showing, in the fist one, our "START RECORDING" button to record the ECG, and, in the second space we can see a list with the information received from the BITalino in packet form and with a view button of each one to see the information of each packet received.

In the following screen shot we can observe data about an ECG patient test, but we can do the same patient test with an EMG.


<div align="center">
<img src="readme/documentationDependencies/param_ecg.png" width="600" border-radius=5% padding=100% />  
</div>  

If we click on the "Show ECG Recording" or in the "Show EMG Recording" button we can see the graph with better resolution and we can zoom it.
