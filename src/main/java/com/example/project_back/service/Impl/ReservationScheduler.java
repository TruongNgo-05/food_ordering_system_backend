package com.example.project_back.service.Impl;

import com.example.project_back.constant.BookingStatus;
import com.example.project_back.constant.TableStatus;
import com.example.project_back.entity.TableDetail;
import com.example.project_back.entity.TableReservations;
import com.example.project_back.repository.TableDetailRepository;
import com.example.project_back.repository.TableReservationsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final TableReservationsRepository reservationRepository;
    private final TableDetailRepository tableRepository;
    private final ContentMailService contentMailService;

    @Scheduled(fixedRate = 60000) // chạy mỗi 1 phút
    @Transactional
    public void autoCancelExpiredReservations() {

        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(30);

        List<TableReservations> reservations = reservationRepository.findExpiredReservations(
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                        expiredTime
                );

        if (reservations.isEmpty()) {
            return;
        }

        for (TableReservations reservation : reservations) {

            reservation.setStatus(BookingStatus.CANCELED);
            reservation.setUpdatedAt(LocalDateTime.now());

            TableDetail table = reservation.getTable();

            if (table != null) {
                table.setStatus(TableStatus.AVAILABLE);
                table.setUpdatedAt(LocalDateTime.now());

                tableRepository.save(table);
            }

            try {

                if (reservation.getCustomerEmail() != null && !reservation.getCustomerEmail().isBlank()) {

                    contentMailService.sendAutoCanceled(reservation);

                }

            } catch (Exception e) {

                log.error("Không gửi được email cho booking {}", reservation.getReservationCode(), e);

            }

            log.info("Auto canceled reservation {}", reservation.getReservationCode());
        }

        reservationRepository.saveAll(reservations);
    }

}
