package com.example.project_back.service;

import com.example.project_back.dto.request.admin.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.user.table.BookTableRequest;
import com.example.project_back.dto.response.admin.TableAdminResponse;
import com.example.project_back.dto.response.staff.ReservationDetailStaffResponse;
import com.example.project_back.dto.response.staff.ReservationStaffResponse;
import com.example.project_back.dto.response.staff.StaffTableResponse;
import com.example.project_back.dto.response.user.MenuTableResponse;
import com.example.project_back.dto.response.user.TableBookResponse;
import com.example.project_back.dto.response.user.TableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TableService {
    List<TableResponse> getListTablesTest();

    TableResponse createTable(CreateAndUpdateTableRequest create);

    TableResponse updateTable(CreateAndUpdateTableRequest update, Integer id);



    String deleteTable(Integer id);

    MenuTableResponse getMenuByTable(String tableNumber);

//    book
    List<TableBookResponse> getAllTableBook(Integer capacity);
     void bookTable(BookTableRequest request);

    //    admin
Page<TableAdminResponse> getListAdminTables(
        String tableNumber,
        Pageable pageable
);


//staff
//    quanr ly dat ban
Page<ReservationStaffResponse> getAllReservations(Pageable pageable);
    ReservationDetailStaffResponse getDetailReservations(Integer id);

    List<StaffTableResponse> getAllStaffTables();

    void receiveCustomer(Integer tableId);

    void cancelReceive(Integer tableId);
// dat don
    void confirmReservation(Integer id);

    void checkInReservation(Integer id);

    void cancelReservation(Integer id);

    void completeReservation(Integer id);



}
