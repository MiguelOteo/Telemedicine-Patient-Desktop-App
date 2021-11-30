# Telelepsia  
## TelemedicineProject (Java 11)
<div align="center">
<img src="resources/documentationDependencies/0.png" alt="drawing" width="200" padding=100%/>  
</div>

-------------------------------

### Index

1. Introduction
2. Admin & Installation Manual.
3. User Manual.  
3.1 Doctor Manual.  
3.2 Patient Manual.

-------------------------------

## 1. Introduction

This project is a telemedicine application whose purpose is the supervision from the patient’s home of a chronic disease, in our case Epilepsy.

The application works recieving data from a BITalino and sending it to a MySQL data base by a connection stablished with a Rest API ([Link to the Rest API used in the project, click on me](https://github.com/MiguelOteo/TelemedicineRestAPI)), as well as allowing the user to create two possible accounts, patients which send the BITalino bio-data and doctors who can see the stored patient's data.

It is important to emphasize that this project works together with TelemedicineRestAPI project, which link has been written previously. 

----------------------------------------

## 2. Admin & Instalation Manual

## JavaFX Project Maven dependencies

This project uses the following Maven dependencies

1. gson dependency: https://mvnrepository.com/artifact/com.google.code.gson/gson
2. json dependency: https://mvnrepository.com/artifact/org.json/json
3. jfoenix 8.0.8 dependency: https://mvnrepository.com/artifact/com.jfoenix/jfoenix

-----------------------------------------
-----------------------------------------

## 3. User Manual  

Once everything has been downloaded, installed and works properly, you only have to run the project and one window with a friendly user interface will be opened, as the following:

<div align="center">
<img src="resources/documentationDependencies/1.png" alt="drawing" width="600" padding=100%/>  
</div>

Depending on your role you can interact with the application in a different way, you can use the application as a Doctor or as a Patient.

-----

### 3.1 Doctor Manual:

1. If you haven't got an account created yet (keep reading, in the other case go to step 4), you have to click in the "Sign Up" button, to register a new account. 

2. Once you clicking on the button a new tab is shown requesting to know some data to create the new account. It's important to bear in mind that every bound must be filled with the data required. After all was be completed you can click on the "Create account" button.

<div align="center">
<img src="resources/documentationDependencies/2_1.png" width="600" border-radius=5% padding=100% />  
</div>  

3.  After all were being completed, you can click on the "Create account" button and a message will appear, indicating that the Doctor account have been created, that's mean that everything goes successfully.

<div align="center">
<img src="resources/documentationDependencies/3_1.png" width="600" border-radius=5% padding=100% />  
</div>  

4. Once, you, as a Doctor, have an account created you can proceed to do the "Login" process, in the main menu. You only have to introduce your email and the password introduced in the "Sign up" process.

5. When you are in, a Doctor number identification is required. This is the id that each Doctor must to have as a professional. The identification number consists of 8 digits following by a capital letter.  
For example: "12345678L".

<div align="center">
<img src="resources/documentationDependencies/2.png" width="600" border-radius=5% padding=100% />  
</div>  

6. After introducing your Doctor number identification a workspace is shown, where you can do a variety of actions; for example update your initial data proportioned when you sign up, or change the password often as a good cybersecurity custom. You can see your actual information below the doctor picture.

<div align="center">
<img src="resources/documentationDependencies/5.png" width="600" border-radius=5% padding=100% />  
</div>  




------

### 3.1 Patient Manual:

Before the application installation and the use of it, it's important to say to the patient that something to bear in mind is that this system has a Hardware dependency; this application works with the BITalino board to recover physiological data of the patient.
<div align="center">
<img src="resources/documentationDependencies/bita.jpeg" width="300" border-radius=5% padding=100% />  
</div>  


1. If you haven't got an account created yet (keep reading, in the other case go to step 4), you have to click in the "Sign Up" button, to register a new account. 

2. Once you clicking on the button a new tab is shown requesting to know some data to create the new account. It's important to bear in mind that every bound must be filled with the data required. After all was be completed you can click on the "Create account" button.

<div align="center">
<img src="resources/documentationDependencies/2_2.png" width="600" border-radius=5% padding=100% />  
</div>  

3.  After all were being completed, you can click on the "Create account" button and a message will appear, indicating that the Patient account have been created, that's mean that everything goes successfully.

<div align="center">
<img src="resources/documentationDependencies/3_2.png" width="600" border-radius=5% padding=100% />  
</div>  

4. Once, you, as a Patient, have an account created you can proceed to do the "Login" process, in the main menu. You only have to introduce your email and the password introduced in the "Sign up" process.

5. When you are in, a Health Insurance number  is required. This is the id that each Patient must to have as a part of a society with healthcare system. The Health Insurance number consists of 8 digits following by a capital letter.  
For example: "12345678T".

<div align="center">
<img src="resources/documentationDependencies/4.png" width="600" border-radius=5% padding=100% />  
</div>  