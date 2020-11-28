package com.thehecklers.aircraftpositions;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class PositionController {
    //    private final AircraftRepository repository;
    private final PositionService service;
    private final RSocketRequester requester;
//    private WebClient client = WebClient.create("http://localhost:7634/aircraft");


    //public PositionController(AircraftRepository repository, RSocketRequester.Builder builder) {
    //public PositionController(PositionService service, RSocketRequester.Builder builder) {
    public PositionController(PositionService service, RSocketRequester requester) {
//        this.repository = repository;
        this.service = service;
        //this.requester = builder.connectTcp("localhost", 7635).block();
        this.requester = requester;
    }

//    @GetMapping("/aircraft")
//    public String getCurrentAircraftPositions(Model model) {
//        Flux<Aircraft> aircraftFlux = repository.deleteAll()
//                .thenMany(client.get()
//                        .retrieve()
//                        .bodyToFlux(Aircraft.class)
//                        .filter(plane -> !plane.getReg().isEmpty())
//                        .flatMap(repository::save));
//
//        model.addAttribute("currentPositions", aircraftFlux);
//        return "positions";
//    }

    @GetMapping("/aircraft")
    public String getCurrentAircraftPositions(Model model) {
        model.addAttribute("currentPositions", service.getAllAircraft());

        return "positions";
    }

/*
    @ResponseBody
    @GetMapping("/acpos")
    public Flux<Aircraft> getCurrentACPositions() {
        return service.getAllAircraft();
    }

    @ResponseBody
    @GetMapping("/acpos/search")
    public Publisher<Aircraft> searchForACPosition(@RequestParam Map<String, String> searchParams) {
        if (!searchParams.isEmpty()) {
            Map.Entry<String, String> setToSearch = searchParams.entrySet().iterator().next();

            if (setToSearch.getKey().equalsIgnoreCase("id")) {
                return service.getAircraftById(Long.valueOf(setToSearch.getValue()));
            } else {
                return service.getAircraftByReg(setToSearch.getValue());
            }
        } else {
            return Mono.empty();
        }
    }
*/

    @ResponseBody
    @GetMapping(value = "/acstream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Aircraft> getCurrentACPositionsStream() {
        return requester.route("acstream")
                .data("Requesting aircraft positions")
                .retrieveFlux(Aircraft.class);
    }
}

/*
        repository.deleteAll();

        client.get()
                .retrieve()
                .bodyToFlux(Aircraft.class)
                .filter(plane -> !plane.getReg().isEmpty())
                .toStream()
                .forEach(repository::save);
*/
