package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.CategoryCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
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
        User loginUser = authenticationUtil.getCurrentUser();

        if(loginUser == null){
            throw new RuntimeException("Chưa có user login vào hệ thống");
        }

        Category category = categoryMapper.toEntity(req);
        category.setCreatedBy(loginUser.getUsername());
        category.setUpdatedBy(loginUser.getUsername());

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(CategoryCreateRequest req, long id) {

        Category updateCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found by ID = " + id));

        if (req.getName() != null) {
            updateCategory.setName(req.getName());
        }

        if (req.getType() != null) {
            updateCategory.setType(req.getType());
        }

        if (req.getIcon() != null) {
            updateCategory.setIcon(req.getIcon());
        }

        categoryRepository.save(updateCategory);

        return categoryMapper.toResponse(updateCategory);
    }

    @Override
    public void delete(Long id) {
        Category deteleCategory = categoryRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Category not found by ID = " + id));
        deteleCategory.setStatus(Constant.DELETED);
        categoryRepository.save(deteleCategory);
    }

    @Override
    public List<CategoryResponse> getAll() {

        String username = authenticationUtil.getCurrentUser().getUsername();
        List<Category> categories = categoryRepository.findAllByCreatedBy(username);

        return categoryMapper.toListResponse(categories);
    }

    @Override
    public CategoryResponse getDetails(Long categoryId) {
        Category category = categoryRepository.findByIdAndStatus(categoryId, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng mục tưng ứng id = " + categoryId));

        return categoryMapper.toResponse(category);
    }


    @Override
    public CategoryResponse getById() {
        return null;
    }

    @Override
    public void changePin(Long id) {
        Category category = categoryRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Category not found by ID = " + id));
        category.setPin(category.getPin() == 1 ? 0 : 1);
        categoryRepository.save(category);
    }

    @Override
    public Page<CategoryResponse> search(CategorySearchRequest request) {
        return categoryRepository.search(request);
    }

    @Override
    public List<CategoryResponse> getTop5MostUsedCategories() {
        return categoryRepository.getTop5MostUsedCategories();
    }

}
