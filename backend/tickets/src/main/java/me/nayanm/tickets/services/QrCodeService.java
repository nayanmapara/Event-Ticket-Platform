package me.nayanm.tickets.services;

import me.nayanm.tickets.domain.entities.QrCode;
import me.nayanm.tickets.domain.entities.Ticket;

public interface QrCodeService {
    QrCode generateQrCode(Ticket ticket);
}
