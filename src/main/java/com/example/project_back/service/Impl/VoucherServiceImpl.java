package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.response.admin.VoucherAdminDetailResponse;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import com.example.project_back.dto.response.customer.OrderCheckResponse;
import com.example.project_back.dto.response.customer.voucher.VoucherGetResponse;
import com.example.project_back.dto.response.customer.voucher.VoucherResponse;
import com.example.project_back.entity.Cart;
import com.example.project_back.entity.CartItem;
import com.example.project_back.entity.User;
import com.example.project_back.entity.Voucher;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.VoucherMapper;
import com.example.project_back.repository.CartRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.repository.VoucherRepository;
import com.example.project_back.service.VoucherService;
import com.example.project_back.specification.VoucherSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {
        private final VoucherRepository voucherRepository;
        private final CartRepository cartRepository;
        private final UserRepository userRepository;
//admin
    @Override
    public Page<VoucherAdminResponse> getVouchers(VoucherRequestParam param, Pageable pageable){
        String code =  param.getCode();
        LocalDateTime startDate = param.getStartDate();
        LocalDateTime endDate = param.getEndDate();

        Specification<Voucher> spec =Specification.unrestricted();

        if(code!=null && !code.isEmpty()){
            spec=spec.and(VoucherSpecification.hasVoucherCode(code));
        }
        if(startDate!=null &&  endDate!=null){
            spec=spec.and(VoucherSpecification.hasDateVoucher(startDate,endDate));
        }
        return voucherRepository.findAll(spec,pageable).map(VoucherMapper::toVoucherAdminResponse);
    }

    @Override
    public VoucherAdminDetailResponse getVoucherById(Integer id){
        Optional<Voucher> voucher = voucherRepository.findById(id);
        if(voucher.isEmpty()){
            throw new ApplicationException("k tim thay");
        }
        return VoucherMapper.toVoucherAdminDetailResponse(voucher.get());
    }

    @Transactional
    @Override
    public VoucherAdminResponse createVoucher(VoucherCreateAndUpdateRequest create){
        if(voucherRepository.existsByCode(create.getVoucherCode())){
            throw new ApplicationException("Code already exists");
        }
        Voucher voucher = VoucherMapper.toVoucherAdminCreateResponse(create);
        Voucher saved = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherAdminResponse(saved);
    }

    @Transactional
    @Override
    public VoucherAdminResponse updateVoucher(Integer id, VoucherCreateAndUpdateRequest update){
        Optional<Voucher> vouchers = voucherRepository.findById(id);
        if(vouchers.isEmpty()){
            throw new ApplicationException("k tim thay");
        }
        if(voucherRepository.existsByCodeAndIdNot(update.getVoucherCode(), id)){
            throw new ApplicationException("Code already exists");
        }
        Voucher voucher  = vouchers.get();
        VoucherMapper.toVoucherAdminUpdateResponse(update,voucher);
        return VoucherMapper.toVoucherAdminResponse(voucherRepository.save(voucher));
    }

    @Transactional
    @Override
    public String deleteVoucher(Integer id){
        Optional<Voucher> voucher = voucherRepository.findById(id);
        if(voucher.isEmpty()){
            throw new ApplicationException("k tim thay");
        }
        voucherRepository.deleteById(id);
        return "success";
    };


//    CUSTOMER

//    voucher
    @Transactional
    @Override
public VoucherResponse checkVoucherCode(String voucherCode){
    String username = SecurityUtils.getCurrentUsername();

    if (username == null || username.equals("anonymousUser")) {
        throw new ApplicationException("Bạn chưa đăng nhập");
    }

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ApplicationException("User không tồn tại"));

    Cart cart = cartRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new ApplicationException("Cart không tồn tại"));

    //  tổng tiền giỏ hàng
    double total = 0.0;
    for (CartItem item : cart.getItems()) {
        total += item.getFood().getPrice() * item.getQuantity();
    }
        //  CASE KHÔNG DÙNG VOUCHER
        if (voucherCode == null || voucherCode.trim().isEmpty()) {

            VoucherResponse res = new VoucherResponse();
            res.setDescription("Không áp dụng voucher");
            res.setDiscount(0.0);
            res.setTotalPrice(total);
            res.setTotalAfter(total);
            return res;
        }
    //  có voucher thì xử lý bình thường
    Voucher voucher = voucherRepository.findByCode(voucherCode)
            .orElseThrow(() -> new ApplicationException("Không tìm thấy voucher"));

    LocalDateTime now = LocalDateTime.now();

    if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
        throw new ApplicationException("Voucher chưa bắt đầu");
    }

    if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
        throw new ApplicationException("Voucher đã hết hạn");
    }

    if (voucher.getMinOrderValue() != null && total < voucher.getMinOrderValue()) {
        throw new ApplicationException("Chưa đủ giá trị đơn hàng");
    }

    int used = voucher.getUsedCount() == null ? 0 : voucher.getUsedCount();
    Integer limit = voucher.getUsageLimit();

    if (limit != null && limit > 0 && used >= limit) {
        throw new ApplicationException("Voucher đã hết lượt");
    }
        //  giảm tiền
        double discountAmount = voucher.getDiscount();
        if (discountAmount > total) {
            discountAmount = total;
        }
        double totalAfter = total - discountAmount;
    VoucherResponse voucherResponse = new VoucherResponse();
    voucherResponse.setDescription(voucher.getDescription());
    voucherResponse.setDiscount(voucher.getDiscount());
    voucherResponse.setTotalPrice(total);
    voucherResponse.setTotalAfter(totalAfter);
    return voucherResponse;
}

    //voucher
    @Override
    public List<VoucherGetResponse> getVoucherCustomer() {
        List<Voucher> vouchers = voucherRepository.findAll();

        // Sắp xếp id giảm dần
        vouchers.sort((v1, v2) -> Long.compare(v2.getId(), v1.getId()));

        List<VoucherGetResponse> res = new ArrayList<>();

        for (Voucher voucher : vouchers) {
            if (voucher.getUsedCount() < voucher.getUsageLimit()) {
                VoucherGetResponse result = new VoucherGetResponse();
                result.setVoucherId(voucher.getId());
                result.setVoucherCode(voucher.getCode());
                res.add(result);
            }
        }  return res;
    }
}
