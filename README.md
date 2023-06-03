# SocialHive

## What is SocialHive?
### social hive is a super app intended for social networking.
### The system includes 4 main mini-apps:
- Events mini app - users can create public events.
- Dating mini app - users can create a profile and search for other users.
- Marketplace mini app - users can sell and buy items.
## How to use SocialHive?
just register and start using the app.
## Technologies
### we used the following technologies:
- Rest API (Spring Boot)
- Flutter (IOS, Android, and Web) client.
- Mongo Database.
- Firebase Storage.
## The Team
- Lior Ariely - Team leader
- Yosef Shalom Seada - System architect, technical writer
- Omer Landa - DBA
- Ido Ben Yaakov - UI/UX designer
- Yaniv Ben David - QA Manager
- Daniel Ben Avi - Scrum Master

## How to install in Linux afeka machine
### Mongo

1. In command line

	``` bash
	docker run --name my-mongo-container -p27017:27017 -d mongo:latest
	```

### Server

1. Extract "projectName".zip file
2. open STS
	- File -> open project from file system.
	- Find project folder.
	- Click finish
	- Right click on the project -> configure -> add gradle nature and wait until finish downloading
	- Right click on project/src/main/java/aplication.java -> Run As -> java application
