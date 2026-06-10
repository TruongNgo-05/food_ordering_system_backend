package com.example.project_back.controller.staff ;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.dto.request.admin.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.spec.OrderRequestParam;
import com.example.project_back.dto.response.staff.OrderStaffOffLineResponse;
import com.example.project_back.dto.response.staff.OrderStaffOnLineResponse;
import com.example.project_back.dto.response.staff.OrderDetailStaffResponse;
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

@RestController
@AllArgsConstructor
@RequestMapping("api/staff/")
public class StaffController {

    private final OrderService orderService;


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
}
