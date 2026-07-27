package me.nayanm.tickets.services.impl;

import me.nayanm.tickets.domain.entities.Ticket;
import me.nayanm.tickets.repositories.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    private TicketServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketServiceImpl(ticketRepository);
    }

    @Test
    void listTicketsForUserDelegatesPurchaserScopeAndPagination() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(2, 8);
        Page<Ticket> expected = new PageImpl<>(List.of(new Ticket()), pageable, 17);
        when(ticketRepository.findByPurchaserId(userId, pageable)).thenReturn(expected);

        Page<Ticket> result = service.listTicketsForUser(userId, pageable);

        assertSame(expected, result);
        verify(ticketRepository).findByPurchaserId(userId, pageable);
    }

    @Test
    void getTicketForUserDelegatesTicketAndPurchaserOwnershipScope() {
        UUID userId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        Optional<Ticket> expected = Optional.of(new Ticket());
        when(ticketRepository.findByIdAndPurchaserId(ticketId, userId)).thenReturn(expected);

        Optional<Ticket> result = service.getTicketForUser(userId, ticketId);

        assertSame(expected, result);
        verify(ticketRepository).findByIdAndPurchaserId(ticketId, userId);
    }
}
