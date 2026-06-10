package com.example.project_back.service;

import com.example.project_back.dto.request.admin.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.user.table.BookTableRequest;
import com.example.project_back.dto.response.admin.TableAdminResponse;
import com.example.project_back.dto.response.user.MenuTableResponse;
import com.example.project_back.dto.response.user.TableBookResponse;
import com.example.project_back.dto.response.user.TableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TableService {
    List<TableResponse> getListTables();

    TableResponse createTable(CreateAndUpdateTableRequest create);

    TableResponse updateTable(CreateAndUpdateTableRequest update, Integer id);

//    TableBookResponse tableDetail(Integer id);

    String deleteTable(Integer id);

    MenuTableResponse getMenuByTable(String tableNumber);

    TableResponse dinnerSet(BookTableRequest bookTableRequest);

//    admin
Page<TableAdminResponse> getListAdminTables(
        String tableNumber,
        Pageable pageable
);
}
