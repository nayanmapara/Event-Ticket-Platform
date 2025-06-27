package me.nayanm.tickets.services;

import me.nayanm.tickets.domain.CreateEventRequest;
import me.nayanm.tickets.domain.entities.Event;

import java.util.UUID;

public interface EventService {
    Event createEvent(UUID organizerId, CreateEventRequest event);
}
