package com.example.project_back.repository;

import com.example.project_back.constant.BookingStatus;
import com.example.project_back.entity.TableReservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TableReservationsRepository extends JpaRepository<TableReservations, Integer> {
    @Query("""
        SELECT r
        FROM TableReservations r
        WHERE r.status IN :statuses
        AND r.reservationTime <= :expiredTime
    """)
    List<TableReservations> findExpiredReservations(
            @Param("statuses") List<BookingStatus> statuses,
            @Param("expiredTime") LocalDateTime expiredTime
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM table_reservations r
    WHERE r.table_id = :tableId
      AND r.status IN ('PENDING','CONFIRMED','CHECKED_IN')
      AND ABS(
            TIMESTAMPDIFF(
                MINUTE,
                r.reservation_time,
                :reservationTime
            )
          ) < 120
    """, nativeQuery = true)
    long countBookingConflict(
            @Param("tableId") Integer tableId,
            @Param("reservationTime") LocalDateTime reservationTime
    );
}
