In my old 6h workshop (before Spring Framework 5 introduced reactive support, Spring Boot 2.0 and Spring Cloud Finchley) i would cover:

  - REST (servlet)
  - data access (JPA + H2)
  - observability w/ actuator 
  - service registration & discovery
  - client side load balancing w/ Eureka
  - reliability patterns w/ circuit breakers 
  - eventual consistency / messaging w/ Spring Cloud Stream and RabbitMQ 
  - REST clients w/ RestTemplate abd Feign 
  - distributed tracing w/ Sleuth Stream + ZIpkin Stream Server 
  - Circuit Breaker dashboard  (w/ no turbine)
  - SSO w/ Spring Cloud Security
  - microservice testing w/ Spring Cloud Contract 
  - Java 
  - edge services /microproxies/api gateways w Zuul

Now, in 2018, everything is bigger and better!

in my new 6h workshop (after reactive and Finchley) i will cover:

 - REST (reactive w/ WebFlux)
 - data access (reactive w/ MongoDB)
 - observability w/ actuator (still works, but its micrometer)
 - service registration & discovery w/ Consul
 - client side load balancing w/ Consul
 - reliability patterns w/ circuit breakers (works but now im using `HystrixCommands` and adapting a `Publisher<T>`)
 - eventual consistency / messaging w/ Spring Cloud Stream and Kakfa
 - REST clients w/ WebClient (Feign doesnt work; any alternatives? Does the stuff from Square work yet?)
 - distributed tracing w/ Zipkin client and Kafka
 - Circuit Breaker dashboard  (w/ Turbine - havent tried this yet)
 - SSO w/ Spring Cloud Security (whts this look like w/ Spring Security 5 and OIDC support)
 - microservice testing w/ Spring Cloud Contract (I haven't tries this yet in a reactive world)
 - Kotlin
 - edge services /microproxies/api gateways w Spring Cloud Gateway
