package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.CategoryCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.exception.ForbiddenException;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.mapper.CategoryMapper;
import com.minhpt.smart_wallet_service.model.Category;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.CategoryRepository;
import com.minhpt.smart_wallet_service.service.CategoryService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public CategoryResponse create(CategoryCreateRequest req) {
        Category category = categoryMapper.toEntity(req);
        category.setNameEng(req.getName());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(CategoryCreateRequest req, long id) {
        User loginUser = authenticationUtil.getCurrentUser();
        Category updateCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found by ID = " + id));
        if (updateCategory.getCreatedBy().equals(Constant.USER_DEFAULT)) {
            throw new ForbiddenException("Không thể cập nhật hạng mục do được tạo bởi hệ thống");
        }
        if (!updateCategory.getCreatedBy().equals(loginUser.getUsername())) {
            throw new ForbiddenException("Không thể cập nhật hạng mục do không phải người tạo");
        }
        updateCategory.setName(req.getName());
        updateCategory.setType(req.getType());
        updateCategory.setIcon(req.getIcon());
        updateCategory.setColor(req.getColor());

        categoryRepository.save(updateCategory);

        return categoryMapper.toResponse(updateCategory);
    }

    @Override
    public void delete(Long id) {
        User loginUser = authenticationUtil.getCurrentUser();
        Category deleteCategory = categoryRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found by ID = " + id));
        if (deleteCategory.getCreatedBy().equals(Constant.USER_DEFAULT)) {
            throw new ForbiddenException("Không thể xóa hạng mục do được tạo bởi hệ thống");
        }
        if (!deleteCategory.getCreatedBy().equals(loginUser.getUsername())) {
            throw new ForbiddenException("Không thể xóa hạng mục do không phải người tạo");
        }

        deleteCategory.setStatus(Constant.DELETED);
        categoryRepository.save(deleteCategory);
    }

    @Override
    public List<CategoryResponse> getAll() {
        String username = authenticationUtil.getCurrentUser().getUsername();
        List<Category> categories = categoryRepository.getAll(username);
        return categoryMapper.toListResponse(categories);
    }

    @Override
    public CategoryResponse getDetail(Long categoryId) {
        Category category = categoryRepository.findByIdAndStatus(categoryId, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found by ID = " + categoryId));
        return categoryMapper.toResponse(category);
    }

    @Override
    public Page<CategoryResponse> search(String lang, CategorySearchRequest request) {
        return categoryRepository.search(lang, request);
    }
}
