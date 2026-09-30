Weather-Based Decision Service

A Java service designed for location-based decision-making using meteorological data. The project implements service integration via interfaces, dependency management with Apache Maven, and fault-tolerance strategies for non-reliable external services.

Tech Stack:
* Java 17+
* Apache Maven
* JUnit 5 & Mockito

Components and architecture:
* External service abstraction using `IWeatherService` and `IMapService` interfaces.
* Implementation of `RobustShouldIGoToService` to handle network errors and inconsistent data gracefully.
* Domain-specific exception handling via custom exception classes (`InvalidRainProbabilityException`, `InvalidMapServiceExecutionException`).

Repository structure:

.
├── pom.xml
└── src/
    ├── main/java/org/iis24/services/   # Source code and service interfaces
    └── test/java/org/iis24/services/   # Automated test suite

How to test:

From the terminal (requires Apache Maven):

mvn clean test