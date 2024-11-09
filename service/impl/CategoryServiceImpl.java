package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepo;
import com.ecom.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	private CategoryRepo categoryRepo;
	
	@Override
	public Category saveCategory(Category category) {
	
		return categoryRepo.save(category);
	}

	@Override
	public List<Category> getAllCategory() {
	
		return categoryRepo.findAll();
	}

	@Override
	public Boolean existCategory(String name) {
		
		return categoryRepo.existsByName(name);
	}

	@Override
	public boolean deleteCategory(int id) {
		
		Category category = categoryRepo.findById(id).orElseGet(null);
		
		if (!ObjectUtils.isEmpty(category)) {
			
			categoryRepo.delete(category);
			
			return true;
		}
		
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		
		Category category = categoryRepo.findById(id).orElse(null);
		
		return category;
	}

	@Override
	public List<Category> getAllActiveCategory() {
	
		List<Category> categories = categoryRepo.findByIsActiveTrue();
		return categories;
	}

	@Override
	public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize) {
		
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		
		return categoryRepo.findAll(pageable);
	}

}
