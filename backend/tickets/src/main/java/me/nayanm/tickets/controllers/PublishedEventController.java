package me.nayanm.tickets.controllers;

import me.nayanm.tickets.domain.dtos.ListPublishedEventResponseDto;
import me.nayanm.tickets.mappers.EventMapper;
import me.nayanm.tickets.services.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/published-events")
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDto>> listPublishedEvents(Pageable pageable) {
        return ResponseEntity.ok(eventService.listPublishedEvents(pageable)
                .map(eventMapper::toListPublishedEventResponseDto));
    }
}
