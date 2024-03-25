# About
A simple language learning java application created for one of my university classes.
The project required that we store the users in a text file to illustrate how much easier it is to work with a database, obviously if that was not the case the logical thing to do would be to store them in the database.

# Running the app
Run `mvn clean install` to install all the neccessary dependencies.
After that, run the installed h2 database and execute the sql script provided in the repository to insert some data.
Adjust the variables in the `dat/database.properties` file and run `mvn javafx:run` to start the application.
You can login with 2 user accounts:
```
  username: admin
  password: admin
```
and
```
  username: regular
  password: regular
```
