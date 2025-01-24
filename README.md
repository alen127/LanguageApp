# About
A simple language learning desktop application created using Java and JavaFX for one of my university classes.

# Running the app

## Setting up the database

1. Run `docker compose up -d` from the root directory of the project.

2. Open http://localhost:81 and click **Connect** (or select embedded mode and adjust the jdbc url in the [properties file](dat/database.properties))

3. Copy and paste the contents of [this sql script](export_202403251640.sql) into the empty field and click **Run**


## Running the desktop application

1. Run `mvn clean install` to install all the neccessary dependencies.

2. Run `mvn javafx:run`

## Logging in

Two accounts are created by default: **admin** and **regular**

### Logging in as admin
```
  username: admin
  password: admin
```

### Logging in as a regular user
```
  username: regular
  password: regular
```
