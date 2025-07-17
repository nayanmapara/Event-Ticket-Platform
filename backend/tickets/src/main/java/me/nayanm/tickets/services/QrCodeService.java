package me.nayanm.tickets.services;

import me.nayanm.tickets.domain.entities.QrCode;
import me.nayanm.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);

}
