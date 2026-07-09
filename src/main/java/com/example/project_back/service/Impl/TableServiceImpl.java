package com.example.project_back.service.Impl;

import com.example.project_back.constant.BookingStatus;
import com.example.project_back.constant.TableStatus;
import com.example.project_back.dto.request.admin.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.user.table.BookTableRequest;
import com.example.project_back.dto.response.admin.TableAdminResponse;
import com.example.project_back.dto.response.staff.ReservationDetailStaffResponse;
import com.example.project_back.dto.response.staff.ReservationStaffResponse;
import com.example.project_back.dto.response.staff.StaffTableResponse;
import com.example.project_back.dto.response.user.FoodTableResponse;
import com.example.project_back.dto.response.user.MenuTableResponse;
import com.example.project_back.dto.response.user.TableBookResponse;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.entity.Food;
import com.example.project_back.entity.TableDetail;
import com.example.project_back.entity.TableReservations;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.FoodMapper;
import com.example.project_back.mapper.TableMapper;
import com.example.project_back.repository.FoodRepository;
import com.example.project_back.repository.OrderRepository;
import com.example.project_back.repository.TableDetailRepository;
import com.example.project_back.repository.TableReservationsRepository;
import com.example.project_back.service.TableService;
import com.example.project_back.specification.TableSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TableServiceImpl implements TableService {
    private final TableDetailRepository tableDetailRepository;
    private final FoodRepository foodRepository;
    private final QRCodeService qrCodeService;
    private final OrderRepository orderRepository;
    private final TableReservationsRepository  tableReservationsRepository;
    private final ContentMailService  contentMailService;

//    test
    @Override
    public List<TableResponse> getListTablesTest() {
        List<TableDetail> tableDetails = tableDetailRepository.findAll();
        List<TableResponse> tableResponseList = new ArrayList<>();
        for (TableDetail tableDetail : tableDetails) {
            tableResponseList.add(TableMapper.toTableResponseTest(tableDetail));
        }
        return tableResponseList;
    }


//    admin
    @Override
    public Page<TableAdminResponse> getListAdminTables(
        String tableNumber,
        Pageable pageable
    ) {
    Specification<TableDetail> spec = Specification.unrestricted();

    if (tableNumber != null && !tableNumber.isBlank()) {
        spec = spec.and(TableSpecification.hasTableNumber(tableNumber));
    }
    return tableDetailRepository.findAll(spec, pageable).map(TableMapper::toTableAdminResponse);
}

    @Override
    public TableResponse createTable(CreateAndUpdateTableRequest create) {
        if(tableDetailRepository.existsByTableNumber(create.getTableNumber())){
            throw new ApplicationException("Số bàn đã tồn tại");
        }

        TableDetail tableDetail = new TableDetail();

        tableDetail.setTableNumber(create.getTableNumber());

        tableDetail.setStatus(TableStatus.AVAILABLE);

        tableDetail.setCreatedAt(LocalDateTime.now());

        tableDetail.setCapacity(create.getCapacity());

        String qrUrl = qrCodeService.generateQRCode(create.getTableNumber());

        tableDetail.setQrCode(qrUrl);

        tableDetailRepository.save(tableDetail);

        return TableMapper.toTableResponseTest(tableDetail);
    }

    @Override
    public TableResponse updateTable(CreateAndUpdateTableRequest update, Integer id) {
        Optional<TableDetail> tableDetailOptional = tableDetailRepository.findById(id);
        if(tableDetailOptional.isEmpty()){
            throw new ApplicationException(" K tim thay ban");
        }
        TableDetail tableDetail = tableDetailOptional.get();
        if(update.getTableNumber() != null){
        tableDetail.setTableNumber(update.getTableNumber());
        }
        if(update.getCapacity() != null){
            tableDetail.setCapacity(update.getCapacity());
        }
        tableDetail.setUpdatedAt(LocalDateTime.now());
        tableDetailRepository.save(tableDetail);
        return TableMapper.toTableResponseTest(tableDetail);
    }

    @Override
    public String deleteTable(Integer id) {
        Optional<TableDetail> tableDetailOptional = tableDetailRepository.findById(id);
        if(tableDetailOptional.isEmpty()){
            throw new ApplicationException(" K tim thay ban");
        }
        TableDetail tableDetail = tableDetailOptional.get();
        if (tableDetail.getQrCode() != null) {
            qrCodeService.deleteQRCode(tableDetail.getQrCode());
        }
        if (orderRepository.existsByTableId(id)) {
            throw new ApplicationException("Table đang có đơn hàng, không thể xóa");
        }
        tableDetailRepository.deleteById(id);
        return "delete success";
    }


//    user
    @Override
    public MenuTableResponse getMenuByTable(
            String tableNumber
    ) {

        TableDetail table = tableDetailRepository.findByTableNumber(tableNumber)
                        .orElseThrow(() -> new ApplicationException("Không tìm thấy bàn"));

        List<Food> foods = foodRepository.findByStatus(true);

        List<FoodTableResponse> foodResponses =
                foods.stream()
                        .map(FoodMapper::toFoodTableResponse)
                        .toList();

        MenuTableResponse response = new MenuTableResponse();

        response.setTable(TableMapper.toTableResponseTest(table));

        response.setFoods(foodResponses);

        return response;
    }

    // user booking
    @Override
    public List<TableBookResponse> getAllTableBook(Integer capacity) {

        Specification<TableDetail> spec = Specification.unrestricted();

        if (capacity != null) {
            spec = spec.and(TableSpecification.hasCapacity(capacity));
        }

        List<TableDetail> tableDetails = tableDetailRepository.findAll(spec);

        return tableDetails.stream()
                .map(TableMapper::tableBookResponse)
                .toList();
    }

    @Override
    @Transactional
    public void bookTable(BookTableRequest request) {

        TableDetail table = tableDetailRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));

        TableReservations reservation = new TableReservations();

        String code = "BK-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        reservation.setReservationCode(code);

        reservation.setCustomerName(request.getCustomerName());
        reservation.setCustomerPhone(request.getCustomerPhone());
        reservation.setCustomerEmail(request.getCustomerEmail());
        reservation.setReservationTime(request.getTimeComes());
        reservation.setNote(request.getNote());

        reservation.setStatus(BookingStatus.PENDING);

        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        reservation.setTable(table);

        table.setStatus(TableStatus.RESERVED);
        table.setUpdatedAt(LocalDateTime.now());
        if (request.getTimeComes().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian đặt bàn không hợp lệ");
        }
        tableReservationsRepository.save(reservation);
        tableDetailRepository.save(table);
        contentMailService.sendBookingSuccess(reservation);
    }




//    staff


    @Override
    public List<StaffTableResponse> getAllStaffTables() {

        List<TableDetail> tables = tableDetailRepository.findAll();

        return tables.stream().map(table -> {

            StaffTableResponse res = new StaffTableResponse();

            res.setId(table.getId());
            res.setTableNumber(table.getTableNumber());
            res.setCapacity(table.getCapacity());
            res.setStatus(table.getStatus());

            switch (table.getStatus()) {

                case AVAILABLE:
                    res.setStatusText("Còn trống");
                    break;

                case OCCUPIED:
                    res.setStatusText("Đang sử dụng");
                    break;

                case RESERVED:

                    tableReservationsRepository
                            .findFirstByTableIdAndStatusInOrderByReservationTimeAsc(
                                    table.getId(),
                                    List.of(
                                            BookingStatus.PENDING,
                                            BookingStatus.CONFIRMED
                                    )
                            )
                            .ifPresentOrElse(reservation -> {

                                Duration duration = Duration.between(
                                        LocalDateTime.now(),
                                        reservation.getReservationTime()
                                );

                                long minutes = duration.toMinutes();

                                if (minutes == 0) {
                                    res.setStatusText("Khách sắp đến");
                                } else if (minutes < 0) {

                                    long lateMinutes = Math.abs(minutes);

                                    if (lateMinutes < 60) {
                                        res.setStatusText("Khách đã đến muộn " + lateMinutes + " phút");
                                    } else {

                                        long hours = lateMinutes / 60;
                                        long remain = lateMinutes % 60;

                                        if (remain == 0) {
                                            res.setStatusText("Khách đã đến muộn " + hours + " giờ");
                                        } else {
                                            res.setStatusText(
                                                    "Khách đã đến muộn "
                                                            + hours + " giờ "
                                                            + remain + " phút"
                                            );
                                        }
                                    }

                                } else if (minutes < 60) {

                                    res.setStatusText("Đã đặt trước - còn " + minutes + " phút");

                                } else {

                                    long hours = minutes / 60;
                                    long remain = minutes % 60;

                                    if (remain == 0) {
                                        res.setStatusText("Đã đặt trước - còn " + hours + " giờ");
                                    } else {
                                        res.setStatusText(
                                                "Đã đặt trước - còn "
                                                        + hours + " giờ "
                                                        + remain + " phút"
                                        );
                                    }
                                }

                            }, () -> res.setStatusText("Đã đặt trước"));

                    break;
            }

            return res;

        }).toList();
    }
@Override
public Page<ReservationStaffResponse> getAllReservations(Pageable pageable) {

    return tableReservationsRepository.findAll(pageable)
            .map(item -> {
                ReservationStaffResponse res = new ReservationStaffResponse();

                res.setId(item.getId());
                res.setReservationCode(item.getReservationCode());
                res.setCustomerName(item.getCustomerName());
                res.setCustomerPhone(item.getCustomerPhone());
                res.setCustomerEmail(item.getCustomerEmail());
                res.setReservationTime(item.getReservationTime());
                res.setStatus(item.getStatus());

                if (item.getTable() != null) {
                    res.setTableNumber(item.getTable().getTableNumber());
                }

                return res;
            });
}

    @Override
    public ReservationDetailStaffResponse getDetailReservations(Integer id) {

        TableReservations reservation = tableReservationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt bàn"));

        ReservationDetailStaffResponse res = new ReservationDetailStaffResponse();

        res.setReservationCode(reservation.getReservationCode());
        res.setCustomerName(reservation.getCustomerName());
        res.setCustomerPhone(reservation.getCustomerPhone());
        res.setCustomerEmail(reservation.getCustomerEmail());
        res.setReservationTime(reservation.getReservationTime());
        res.setStatus(reservation.getStatus());
        res.setNote(reservation.getNote());

        if (reservation.getTable() != null) {
            res.setTableNumber(reservation.getTable().getTableNumber());
            res.setCapacity(reservation.getTable().getCapacity());
        }

        return res;
    }

    @Transactional
    @Override
    public void confirmReservation(Integer id) {

        TableReservations reservation =
                tableReservationsRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        reservation.setStatus(BookingStatus.CONFIRMED);

        reservation.setUpdatedAt(LocalDateTime.now());

        tableReservationsRepository.save(reservation);
    }

    @Transactional
    @Override
    public void checkInReservation(Integer id) {

        TableReservations reservation = tableReservationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        reservation.setStatus(BookingStatus.CHECKED_IN);

        TableDetail table = reservation.getTable();

        table.setStatus(TableStatus.OCCUPIED);

        reservation.setUpdatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());

        tableReservationsRepository.save(reservation);
        tableDetailRepository.save(table);

        contentMailService.sendCheckIn(reservation);
    }

    @Transactional
    @Override
    public void cancelReservation(Integer id) {

        TableReservations reservation =
                tableReservationsRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        reservation.setStatus(BookingStatus.CANCELED);

        TableDetail table = reservation.getTable();

        table.setStatus(TableStatus.AVAILABLE);

        reservation.setUpdatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());

        tableReservationsRepository.save(reservation);
        tableDetailRepository.save(table);

      contentMailService.sendCanceled(reservation);
    }


    @Transactional
    @Override
    public void completeReservation(Integer id) {

        TableReservations reservation = tableReservationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        reservation.setStatus(BookingStatus.COMPLETED);

        TableDetail table = reservation.getTable();

        table.setStatus(TableStatus.AVAILABLE);

        reservation.setUpdatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());

        tableReservationsRepository.save(reservation);
        tableDetailRepository.save(table);

    contentMailService.sendCompleted(reservation);
    }

}
