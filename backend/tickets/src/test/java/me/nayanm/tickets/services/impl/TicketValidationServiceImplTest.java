package me.nayanm.tickets.services.impl;

import me.nayanm.tickets.domain.entities.QrCode;
import me.nayanm.tickets.domain.entities.QrCodeStatusEnum;
import me.nayanm.tickets.domain.entities.Ticket;
import me.nayanm.tickets.domain.entities.TicketValidation;
import me.nayanm.tickets.domain.entities.TicketValidationMethod;
import me.nayanm.tickets.domain.entities.TicketValidationStatusEnum;
import me.nayanm.tickets.exceptions.QrCodeNotFoundException;
import me.nayanm.tickets.exceptions.TicketNotFoundException;
import me.nayanm.tickets.repositories.QrCodeRepository;
import me.nayanm.tickets.repositories.TicketRepository;
import me.nayanm.tickets.repositories.TicketValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketValidationServiceImplTest {

    @Mock
    private QrCodeRepository qrCodeRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketValidationRepository ticketValidationRepository;

    private TicketValidationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketValidationServiceImpl(
                qrCodeRepository,
                ticketRepository,
                ticketValidationRepository
        );
        lenient().when(ticketValidationRepository.save(any(TicketValidation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void validateTicketByQrCodeRecordsValidFirstUse() {
        UUID qrCodeId = UUID.randomUUID();
        Ticket ticket = ticketWithValidations();
        QrCode qrCode = new QrCode();
        qrCode.setTicket(ticket);
        when(qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE))
                .thenReturn(Optional.of(qrCode));

        TicketValidation result = service.validateTicketByQrCode(qrCodeId);

        assertEquals(TicketValidationStatusEnum.VALID, result.getStatus());
        assertEquals(TicketValidationMethod.QR_SCAN, result.getValidationMethod());
        assertSame(ticket, result.getTicket());
        verify(ticketValidationRepository).save(result);
    }

    @Test
    void validateTicketByQrCodeRecordsInvalidReplayAfterValidUse() {
        UUID qrCodeId = UUID.randomUUID();
        TicketValidation previousValidation = new TicketValidation();
        previousValidation.setStatus(TicketValidationStatusEnum.VALID);
        Ticket ticket = ticketWithValidations(previousValidation);
        QrCode qrCode = new QrCode();
        qrCode.setTicket(ticket);
        when(qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE))
                .thenReturn(Optional.of(qrCode));

        TicketValidation result = service.validateTicketByQrCode(qrCodeId);

        assertEquals(TicketValidationStatusEnum.INVALID, result.getStatus());
        assertEquals(TicketValidationMethod.QR_SCAN, result.getValidationMethod());
    }

    @Test
    void validateTicketManuallyUsesTicketIdAndManualMethod() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = ticketWithValidations();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        TicketValidation result = service.validateTicketManually(ticketId);

        assertEquals(TicketValidationStatusEnum.VALID, result.getStatus());
        assertEquals(TicketValidationMethod.MANUAL, result.getValidationMethod());
        assertSame(ticket, result.getTicket());
    }

    @Test
    void validateTicketByQrCodeRejectsMissingActiveQrCode() {
        UUID qrCodeId = UUID.randomUUID();
        when(qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(QrCodeNotFoundException.class, () -> service.validateTicketByQrCode(qrCodeId));
    }

    @Test
    void validateTicketManuallyRejectsMissingTicket() {
        UUID ticketId = UUID.randomUUID();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> service.validateTicketManually(ticketId));
    }

    private Ticket ticketWithValidations(TicketValidation... validations) {
        Ticket ticket = new Ticket();
        ticket.setValidations(new ArrayList<>(List.of(validations)));
        return ticket;
    }
}
