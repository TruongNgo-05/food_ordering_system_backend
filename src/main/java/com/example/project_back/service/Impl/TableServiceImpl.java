package com.example.project_back.service.Impl;

import com.example.project_back.constant.TableStatus;
import com.example.project_back.dto.request.user.table.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.user.table.DinnerSetTableRequest;
import com.example.project_back.dto.response.user.FoodTableResponse;
import com.example.project_back.dto.response.user.MenuTableResponse;
import com.example.project_back.dto.response.user.TableDetailResponse;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.entity.Food;
import com.example.project_back.entity.TableDetail;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.FoodMapper;
import com.example.project_back.mapper.TableMapper;
import com.example.project_back.repository.FoodRepository;
import com.example.project_back.repository.TableDetailRepository;
import com.example.project_back.service.TableService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TableServiceImpl implements    TableService {
    private final TableDetailRepository tableDetailRepository;
    private final FoodRepository foodRepository;
    private final QRCodeService qrCodeService;

    @Override
    public List<TableResponse> getListTables() {
        List<TableDetail> tableDetails = tableDetailRepository.findAll();
        List<TableResponse> tableResponseList = new ArrayList<>();
        for (TableDetail tableDetail : tableDetails) {
            tableResponseList.add(TableMapper.toTableResponse(tableDetail));
        }
        return tableResponseList;
    }

    @Override
    public TableResponse createTable(CreateAndUpdateTableRequest create) {

        TableDetail tableDetail = new TableDetail();

        tableDetail.setTableNumber(create.getTableNumber());

        tableDetail.setStatus(TableStatus.AVAILABLE);

        tableDetail.setCreatedAt(LocalDateTime.now());

        String qrUrl = qrCodeService.generateQRCode(create.getTableNumber());

        tableDetail.setQrCode(qrUrl);

        tableDetailRepository.save(tableDetail);

        return TableMapper.toTableResponse(tableDetail);
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
        tableDetail.setUpdatedAt(LocalDateTime.now());
        tableDetailRepository.save(tableDetail);
        return TableMapper.toTableResponse(tableDetail);
    }

    @Override
    public TableDetailResponse tableDetail(Integer id) {
        Optional<TableDetail> tableDetailOptional = tableDetailRepository.findById(id);
        if(tableDetailOptional.isEmpty()){
            throw new ApplicationException(" K tim thay ban");
        }
        TableDetail tableDetail = tableDetailOptional.get();
       return TableMapper.toTableDetailResponse(tableDetail);
    }

    @Override
    public String deleteTable(Integer id) {
        Optional<TableDetail> tableDetailOptional = tableDetailRepository.findById(id);
        if(tableDetailOptional.isEmpty()){
            throw new ApplicationException(" K tim thay ban");
        }
        TableDetail tableDetail = tableDetailOptional.get();
        // xóa qr trước
        if (tableDetail.getQrCode() != null) {
            qrCodeService.deleteQRCode(tableDetail.getQrCode());
        }

        tableDetailRepository.deleteById(id);
        return "delete success";
    }

    @Override
    public MenuTableResponse getMenuByTable(
            String tableNumber
    ) {

        TableDetail table =
                tableDetailRepository.findByTableNumber(tableNumber)
                        .orElseThrow(() ->
                                new ApplicationException("Không tìm thấy bàn"));

        List<Food> foods = foodRepository.findByStatus(true);

        List<FoodTableResponse> foodResponses =
                foods.stream()
                        .map(FoodMapper::toFoodTableResponse)
                        .toList();

        MenuTableResponse response = new MenuTableResponse();

        response.setTable(TableMapper.toTableResponse(table));

        response.setFoods(foodResponses);

        return response;
    }

    @Override
    public TableResponse dinnerSet(DinnerSetTableRequest dinnerSetTableRequest) {

        TableDetail tableDetails = tableDetailRepository
                .findByTableNumber(dinnerSetTableRequest.getTableNumber())
                .orElseThrow(() ->
                        new ApplicationException("Không tìm thấy bàn"));

        if (tableDetails.getStatus().equals(TableStatus.OCCUPIED)) {
            throw new ApplicationException("Bàn đang sử dụng");
        }

        if (tableDetails.getStatus().equals(TableStatus.RESERVED)) {
            throw new ApplicationException("Bàn này đã được đặt trước");
        }

        tableDetails.setStatus(TableStatus.RESERVED);

        tableDetailRepository.save(tableDetails);

        return TableMapper.toTableResponse(tableDetails);
    }
}
