# spring-cloud-demo
A simple store project built with Spring Cloud Stack.

Developed during [Alura](https://alura.com.br) platform's course.

## Built With

* Spring Netflix Eureka Service Discovery to provide scalable service discovery.
* Spring Config Server to offer centralized configuration management.
* Spring Open Feign to consume REST APIs of adjacents microservices.
  * Netflix Ribbon as a Client Side Load-Balancer.
* Spring Cloud Sleuth to provide Distributed Tracing.
* Netflix Hystrix to enable Circuit Breaker and Bulkhead splitted by REST operations.
* Netflix Zuul as an API Gateway.
* Spring Security and Spring Cloud OAuth to provide simple authentication and authorization.