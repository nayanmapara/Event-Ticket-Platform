package me.nayanm.tickets.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.nayanm.tickets.domain.CreateEventRequest;
import me.nayanm.tickets.domain.UpdateEventRequest;
import me.nayanm.tickets.domain.UpdateTicketTypeRequest;
import me.nayanm.tickets.domain.entities.Event;
import me.nayanm.tickets.domain.entities.EventStatusEnum;
import me.nayanm.tickets.domain.entities.TicketType;
import me.nayanm.tickets.domain.entities.User;
import me.nayanm.tickets.exceptions.EventNotFoundException;
import me.nayanm.tickets.exceptions.EventUpdateException;
import me.nayanm.tickets.exceptions.TicketTypeNotFoundException;
import me.nayanm.tickets.exceptions.UserNotFoundException;
import me.nayanm.tickets.repositories.EventRepository;
import me.nayanm.tickets.repositories.UserRepository;
import me.nayanm.tickets.services.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event createEvent(UUID organizerId, CreateEventRequest event) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id %s not found", organizerId))
                );

        Event eventToCreate = new Event();

        List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(
                ticketType -> {
                    TicketType ticketTypeToCreate = new TicketType();
                    ticketTypeToCreate.setName(ticketType.getName());
                    ticketTypeToCreate.setPrice(ticketType.getPrice());
                    ticketTypeToCreate.setDescription(ticketType.getDescription());
                    ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                    ticketTypeToCreate.setEvent(eventToCreate);
                    return ticketTypeToCreate;
                }).toList();

        eventToCreate.setName(event.getName());
        eventToCreate.setStart(event.getStart());
        eventToCreate.setEnd(event.getEnd());
        eventToCreate.setVenue(event.getVenue());
        eventToCreate.setSalesStart(event.getSalesStart());
        eventToCreate.setSalesEnd(event.getSalesEnd());
        eventToCreate.setStatus(event.getStatus());
        eventToCreate.setOrganizer(organizer);
        eventToCreate.setTicketTypes(ticketTypesToCreate);

        return eventRepository.save(eventToCreate);
    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID eventId) {
        return eventRepository.findByIdAndOrganizerId(eventId, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID eventId, UpdateEventRequest event) {
        if(null == event.getId()) {
            throw new EventUpdateException("Event ID cannot be null");
        }

        if(!eventId.equals(event.getId())) {
            throw new EventUpdateException("Cannot update the ID of the event");
        }

        Event existingEvent = eventRepository
                .findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with id '%s' not found", eventId))
                );

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(
                existingTicketType -> !requestTicketTypeIds.contains(existingTicketType.getId())
        );

        Map<UUID, TicketType> existingTicketTypesIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for (UpdateTicketTypeRequest ticketType : event.getTicketTypes()) {
            if(null == ticketType.getId()) {
                // Create
                TicketType newTicketType = new TicketType();
                newTicketType.setName(ticketType.getName());
                newTicketType.setPrice(ticketType.getPrice());
                newTicketType.setDescription(ticketType.getDescription());
                newTicketType.setTotalAvailable(ticketType.getTotalAvailable());
                newTicketType.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(newTicketType);

            } else if(existingTicketTypesIndex.containsKey(ticketType.getId())) {
                //Update
                TicketType existingTicketType = existingTicketTypesIndex.get(ticketType.getId());
                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());


            } else {
                throw new TicketTypeNotFoundException(String.format(
                        "Ticket type with id '%s' does not exist", ticketType.getId()
                ));
            }
        }

        return eventRepository.save(existingEvent);

    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID eventId) {
        getEventForOrganizer(organizerId, eventId).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> searchPublishedEvents(String query, Pageable pageable) {
        return null;
    }

}
