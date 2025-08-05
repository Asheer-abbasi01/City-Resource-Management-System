Smart City Resource Management System
A Java Swing-based GUI application developed as a semester project to simulate and manage city services such as transport units, power stations, and emergency services.
The project demonstrates the use of Object-Oriented Programming (OOP) concepts like inheritance, polymorphism, interfaces, and composition.

📌 Features
✅ Manage transport units (e.g., buses)

⚡ Monitor power stations and simulate outages

🚑 Handle emergency services and simulate responses

🧠 Real-time alerting and reporting in the GUI

💾 Data persistence using serialization

🧪 Simulation support (traffic, power outages, emergency calls)

🧱 Technologies Used
Java (OOP)

Java Swing (for GUI)

Serialization (for saving/loading data)

🧠 OOP Concepts Applied
Inheritance – Base class CityResource inherited by TransportUnit, PowerStation, and EmergencyService

Interfaces – Alertable for emergency alerts, Reportable for usage reporting

Polymorphism – Handling resources through common parent/interface types

Composition – Classes like CityZone and ResourceHub contain multiple resources

📂 Project Structure

City-Resource-Management-System/
│
├── smartcity/
│   ├── CityResource.java           // Abstract base class
│   ├── TransportUnit.java          // Transport resource
│   ├── PowerStation.java           // Power resource
│   ├── EmergencyService.java       // Emergency units
│   ├── Consumer.java               // Power consumers
│   ├── ResourceHub.java            // Grouping transport
│   ├── CityZone.java               // Area-wise grouping
│   ├── Alertable.java              // Interface for alerts
│   ├── Reportable.java             // Interface for reports
│   ├── CityRepository.java         // Data storage and serialization
│   └── SmartCityGUI.java           // Main GUI application
│
└── README.md                       // This file

🖥️ How to Run
Install Java (Java 8+ recommended)

Open project in any Java IDE (e.g., IntelliJ IDEA, Eclipse)

Run SmartCityGUI.java to launch the application

🎮 Simulation Features
Simulation Type	Description
🚗 Traffic Changes	Adjust passenger counts dynamically
⚠️ Power Outage	Trigger and respond to outage alerts
🚨 Emergency Dispatch	Simulate emergency calls and response handling
🧾 Cost Verification	View detailed maintenance and usage reports

📈 Sample Output (GUI)
Real-time logs of alerts (e.g., power outage)

Resource status updates (e.g., "Responding", "Available")

Usage and cost reports displayed in the GUI

✅ Results
Total maintenance cost across resources

Passenger and transport stats

Power usage and outage handling

Emergency response efficiency

Predictive maintenance using AI

📚 Academic Info
Course: Object-Oriented Programming

Level: University Semester Project

Language: Java (OOP + Swing)

👨‍💻 Author
Asheer Hidayat
Computer Science Department, COMSATS University Islamabad
Java Enthusiast | Tech Learner

