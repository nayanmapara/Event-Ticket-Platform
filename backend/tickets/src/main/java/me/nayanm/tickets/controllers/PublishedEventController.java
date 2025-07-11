package me.nayanm.tickets.controllers;

import lombok.RequiredArgsConstructor;
import me.nayanm.tickets.domain.dtos.ListPublishedEventResponseDto;
import me.nayanm.tickets.domain.entities.Event;
import me.nayanm.tickets.mappers.EventMapper;
import me.nayanm.tickets.services.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q,
            Pageable pageable) {

        Page<Event> events;
        if(q != null && !q.isEmpty()) {
            events = eventService.searchPublishedEvents(q, pageable);
        } else {
            events = eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(eventService.listPublishedEvents(pageable)
                .map(eventMapper::toListPublishedEventResponseDto));
    }
}
