package com.example.reservationclient;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import lombok.Data;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.Routes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import rx.Observable;
import rx.RxReactiveStreams;

import static org.springframework.cloud.gateway.handler.predicate.RoutePredicates.path;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@EnableDiscoveryClient
public class ReservationClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationClientApplication.class, args);
    }

    @Bean
    DiscoveryClientRouteDefinitionLocator gatewayRoutes(DiscoveryClient client) {
        return new DiscoveryClientRouteDefinitionLocator(client);
    }

    @Bean
    RouteLocator gateway(RequestRateLimiterGatewayFilterFactory rateLimiter) {
        return Routes
                .locator()
                .route("rate_limited_route")
                .predicate(path("/edge-reservations"))
                .uri("lb://reservation-service/reservations")
                .build();
    }

    @Bean
    WebClient client(LoadBalancerExchangeFilterFunction lb) {
        return WebClient.builder().filter(lb).build();
    }


    @Bean
    RouterFunction<?> routes(WebClient client) {
        return route(GET("/reservations/names"),
                r -> {
                    Flux<String> res = client
                            .get()
                            .uri("http://reservation-service/reservations")
                            .retrieve()
                            .bodyToFlux(Reservation.class)
                            .map(Reservation::getReservationName);

//                    Publisher<String> cb = HystrixCommands.wrap("reservation-name", res, Flux.just("EEK"));
                    HystrixObservableCommand<String> cmd = new HystrixObservableCommand<String>(
                            HystrixCommandGroupKey.Factory.asKey("reservation-names")) {

                        @Override
                        protected Observable<String> resumeWithFallback() {
                            return RxReactiveStreams.toObservable(Flux.just("EEK!"));
                        }

                        @Override
                        protected Observable<String> construct() {
                            return RxReactiveStreams.toObservable(res);
                        }
                    };
                    Observable<String> observable = cmd.observe();
                    Publisher<String> publisher = RxReactiveStreams.toPublisher(observable);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(publisher, String.class);
                });
    }
}

@Data
class Reservation {
    private String reservationName, id;
}