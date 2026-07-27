package me.nayanm.tickets.services.impl;

import me.nayanm.tickets.domain.entities.Ticket;
import me.nayanm.tickets.domain.entities.TicketStatusEnum;
import me.nayanm.tickets.domain.entities.TicketType;
import me.nayanm.tickets.domain.entities.User;
import me.nayanm.tickets.repositories.TicketRepository;
import me.nayanm.tickets.repositories.TicketTypeRepository;
import me.nayanm.tickets.repositories.UserRepository;
import me.nayanm.tickets.services.QrCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketTypeServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private QrCodeService qrCodeService;

    private TicketTypeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketTypeServiceImpl(
                userRepository,
                ticketTypeRepository,
                ticketRepository,
                qrCodeService
        );
    }

    @Test
    void purchaseTicketCreatesPurchasedTicketAndQrCodeWhenInventoryIsAvailable() {
        UUID userId = UUID.randomUUID();
        UUID ticketTypeId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        TicketType ticketType = new TicketType();
        ticketType.setId(ticketTypeId);
        ticketType.setTotalAvailable(10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketTypeRepository.findByIdWithLock(ticketTypeId)).thenReturn(Optional.of(ticketType));
        when(ticketRepository.countByTicketTypeId(ticketTypeId)).thenReturn(9);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = service.purchaseTicket(userId, ticketTypeId);

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository, times(2)).save(ticketCaptor.capture());
        verify(qrCodeService).generateQrCode(result);
        assertEquals(TicketStatusEnum.PURCHASED, result.getStatus());
        assertSame(user, result.getPurchaser());
        assertSame(ticketType, result.getTicketType());
        assertSame(result, ticketCaptor.getAllValues().getFirst());
        assertSame(result, ticketCaptor.getAllValues().getLast());
    }
}
