# SHAK

Organizing study documents correctly and effectively is anything but simple.
Sometimes students find it difficult to match notes with the proposed texts, for this reason we thought that having many more sources, grouping more notes taken and more references, can save time and allow students to focus more on better learning . But the purpose of our system is also to allow teachers to better organize their material and share it easily with students.

SHAK was born in an unripe version, but hides many potentials that broadly reflect the behavior of the major social networks in circulation today.
Used with discretion and precision, it could revolutionize the way many students study, allowing them to tear down library brick walls.
Collecting all the material concerning the subjects helps, first of all, not to spend useless time in a redundant search in the vast world of web. The evaluation of documents, the possibility of being able to easily retrieve them from favorites, the discussions in comments between users, can help the student to discover other material, that can lead to the solution of the problems sought in a more immediate way.

SHAK has an architecture client/server where
- **Front-End (Client)** was developed in **Angular 9.1.0, Materialize CSS**
- **Native Android App (Client)** was developed in **Android, Java**
- **Back-End (Server)** was developed in **NodeJS**

Made by Aldo Manco, Marco Brunetti, Francesco Ferrini

# Build Your Own SHAK 

## Installation & Configuration of MongoDB

Download the MongoDB Community .msi installer from the following link:  

https://www.mongodb.com/try/download/community?tck=docs_server

1. In the Version dropdown, select the version of MongoDB to download.
2. In the Platform dropdown, select Windows.
3. In the Package dropdown, select msi.
4. Click Download.
5. Go to the directory where you downloaded the MongoDB installer (.msi file). By default, this is your Downloads directory.
6. Double-click the .msi file.

7. During the installation:
   - Select **"Complete"** Setup
   - Select **"Install MongoD as a Service"**
   - Select **"Run service as Network Service user"**
   - Complete the installation without changing options

8. Open the terminal as administrator and run the following commands:  
`cd C:\`  
`md "\data\db"`  
`"C:\Program Files\MongoDB\Server\%YOUR_MONGODB_VERSION%\bin\mongod.exe" --dbpath="c:\data\db"`  

If you can see  
`[initandlisten] waiting for connections`  
then your MongoDB Database Server is ready to be used.

## Install NodeJS

1. To download and install **NodeJS**, go to the following link:

   https://nodejs.org/en/  
   
2. Download the latest version of NodeJS
3. Run the setup and complete installation without changing options
4. Open the Windows terminal (CMD)

To check if NodeJS is correctly installed, you can execute the following commands:  
`npm -v`  
if you can see the version of NodeJS, then it is installed correctly.

## Clone Project in your Computer

The git commands described below assume that you have a GitHub account

If you want to build your own version of SHAK, **you'll need to fetch the git sources both for Android App and Back-End.**

### Native Android Application

**Open the terminal and change directory, choose the folder where you want to create your own SHAK Android App.**

To initialize your local repository using the SHAK source tree, clone the main repository with this command:  
`git clone https://github.com/aldo-manco-jr/shak-android.git`  

When the Native Android App clone is finished, run the following commands:  
`cd shak-android`  

### Back-End

**Open the terminal and change directory, choose the folder where you want to create your own SHAK.**

To initialize your local repository using the SHAK source tree, clone the main repository with this command:  
`git clone https://github.com/aldo-manco-jr/shak-backend.git`  

When the back-end clone is finished, run the following commands:  
`cd shak-backend`  
`npm install`  
`npm install -g nodemon`  
such that all the packages that we have used in the back-end application, written in **package.json**, will be automatically installed.  
  
Now that the server is ready, **you have to write this command each time you want to run the server**:  
`nodemon start`  

If `nodemon start` doesn't work, try this command:  
`npm config get prefix`  
copy the path that terminal gives you back and replace `C:\Users\%USERNAME%\AppData\Roaming\npm;` of following command with it:  
`set PATH=%PATH%;C:\Users\%USERNAME%\AppData\Roaming\npm;`  
