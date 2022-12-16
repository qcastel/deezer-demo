# Getting Started

## Pre-requisite

You will need Java 17

## Start the application

Start the Java application. 

```bash
 ./mvnw spring-boot:run
```

## Start the demo

Go to:

```
http://localhost:8081/
```

=> You will be redirected to the Deezer login page

- Login to deezer and accept the consent page.

=> You are redirected to the home page of the app

You should have a message like:

```
Hello world quentin!You're not an admin
```

You don't see the flag, as only the admin can see it. The email of the admin user is `"qcastel+deezeradmin@wearehackerone.com"`

## Get the flag

- Login to Deezer
- Change your email to `qcastel+deezeradmin@wearehackerone.com`

=> You will get a code to your current deezer address. As you own this email address, no problem there
- Enter the code you received by email

=> The account is associated with the email address `qcastel+deezeradmin@wearehackerone.com`, eventhough you don't own this address!


- Retry the demo
=> You should now see a message with the flag `Hello world quentin!You're the admin. Here is the secret flag: $CTF[****]`
