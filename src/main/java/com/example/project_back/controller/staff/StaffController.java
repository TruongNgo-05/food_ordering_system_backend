package com.example.project_back.controller.staff ;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.dto.request.admin.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.spec.OrderRequestParam;
import com.example.project_back.dto.response.staff.*;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.service.FoodService;
import com.example.project_back.service.OrderService;
import com.example.project_back.service.TableService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/staff/")
public class StaffController {

    private final OrderService orderService;
    private final TableService tableService;

    //    order
    @GetMapping("orders-online")
    public Page<OrderStaffOnLineResponse> getOnlineOrders(
            OrderRequestParam param,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return orderService.getOnlineOrders(param,pageable);
    }

    @GetMapping("orders-offline")
    public Page<OrderStaffOffLineResponse> getTableOrders(OrderRequestParam param, Pageable pageable) {
        return orderService.getTableOrders(param,pageable);
    }

    @GetMapping("orders/{id}")
    public OrderDetailStaffResponse getOrderDetail(@PathVariable Long id) {
        return orderService.getOrderStaffDetail(id);
    }

    @PutMapping("orders/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {

        orderService.updateStatus(id, status);

        return "Cập nhật trạng thái thành công";
    }


//    booking

    // ================= DANH SÁCH ĐẶT BÀN =================

    @GetMapping("table-reservations")
    public ResponseEntity<BaseResponse<Page<ReservationStaffResponse>>> getAllReservations(Pageable pageable) {

        return ResponseEntity.ok(
                new BaseResponse<>(
                        tableService.getAllReservations(pageable),
                        "Lấy danh sách đặt bàn thành công"
                )
        );
    }

    // ================= chi tiet dat ban ĐẶT BÀN =================
    @GetMapping("table-reservations/{id}")
    public ResponseEntity<BaseResponse<ReservationDetailStaffResponse>> getDetailReservations(@PathVariable Integer id) {
        return ResponseEntity.ok(BaseResponse.success(tableService.getDetailReservations(id)));
    }

    // ================= XÁC NHẬN ĐẶT BÀN =================

    @PutMapping("table-reservations/{id}/confirm")
    public ResponseEntity<BaseResponse<String>> confirmReservation(@PathVariable Integer id) {

        tableService.confirmReservation(id);

        return ResponseEntity.ok(BaseResponse.success("Xác nhận đặt bàn thành công"));
    }

    // ================= KHÁCH NHẬN BÀN =================

    @PutMapping("table-reservations/{id}/check-in")
    public ResponseEntity<BaseResponse<String>> checkInReservation(@PathVariable Integer id) {

        tableService.checkInReservation(id);

        return ResponseEntity.ok(
                BaseResponse.success("Check-in thành công")
        );
    }

    // ================= HỦY ĐẶT BÀN =================

    @PutMapping("table-reservations/{id}/cancel")
    public ResponseEntity<BaseResponse<String>> cancelReservation(@PathVariable Integer id) {

        tableService.cancelReservation(id);

        return ResponseEntity.ok(
                BaseResponse.success("Hủy đặt bàn thành công")
        );
    }

    // ================= HOÀN THÀNH =================

    @PutMapping("table-reservations/{id}/complete")
    public ResponseEntity<BaseResponse<String>> Recompleteservation(@PathVariable Integer id) {

        tableService.completeReservation(id);

        return ResponseEntity.ok(BaseResponse.success("Hoàn thành đơn đặt bàn"));
    }

//table
@GetMapping("tables")
public ResponseEntity<BaseResponse<List<StaffTableResponse>>> getAllTables() {

    return ResponseEntity.ok(
            BaseResponse.success(
                    tableService.getAllStaffTables()
            )
    );
}
// ================= NHẬN KHÁCH =================

    @PutMapping("tables/{id}/receive")
    public ResponseEntity<BaseResponse<String>> receiveCustomer(
            @PathVariable Integer id
    ) {

        tableService.receiveCustomer(id);

        return ResponseEntity.ok(
                BaseResponse.success("Đã nhận khách")
        );
    }

    // ================= HỦY NHẬN KHÁCH =================

    @PutMapping("tables/{id}/cancel-receive")
    public ResponseEntity<BaseResponse<String>> cancelReceiveCustomer(
            @PathVariable Integer id
    ) {

        tableService.cancelReceive(id);

        return ResponseEntity.ok(
                BaseResponse.success("Đã trả bàn về trạng thái trống")
        );
    }

    // ================= THANH TOÁN BÀN =================

    @PutMapping("tables/{id}/checkout")
    public ResponseEntity<BaseResponse<String>> checkoutTable(
            @PathVariable Integer id
    ) {

        orderService.checkoutTable(id);

        return ResponseEntity.ok(
                BaseResponse.success("Thanh toán thành công")
        );
    }
}
