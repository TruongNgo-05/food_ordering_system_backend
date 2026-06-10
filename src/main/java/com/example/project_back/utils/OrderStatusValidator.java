package com.example.project_back.utils;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.entity.Order;

public class OrderStatusValidator {
    public static boolean isValidTransition(
            Order order,
            OrderStatus next
    ) {

        OrderStatus current = order.getStatus();
        boolean isTableOrder = order.getAddress() == null;

        if (current == next) {
            return false;
        }

        // OFFLINE
        if (isTableOrder) {
            return switch (current) {
                case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.REJECTED;
                case CONFIRMED -> next == OrderStatus.PREPARING;
                case PREPARING -> next == OrderStatus.COMPLETED;
                case COMPLETED, REJECTED, CANCELED -> false;
                default -> false;
            };
        }

        // ONLINE
        return switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.REJECTED;
            case CONFIRMED -> next == OrderStatus.PREPARING;
            case PREPARING -> next == OrderStatus.DELIVERING;
            case DELIVERING -> next == OrderStatus.COMPLETED;
            case COMPLETED, REJECTED, CANCELED -> false;
        };
    }

    private OrderStatusValidator() {
    }
}
