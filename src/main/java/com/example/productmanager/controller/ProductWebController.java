package com.example.productmanager.controller;

import com.example.productmanager.model.Product;
import com.example.productmanager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductWebController {

    @Autowired
    private ProductService service;  // اسم الحقل هو service

    // عرض قائمة المنتجات
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", service.getAllProducts());
        model.addAttribute("newProduct", new Product());
        return "product-list";
    }

    // إضافة منتج جديد
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product) {
        service.saveProduct(product);
        return "redirect:/products";
    }

    // حذف منتج
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return "redirect:/products";
    }

    // عرض نموذج التعديل
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = service.getProductById(id);  // استخدم service
        if (product == null) {
            return "redirect:/products";  // إذا لم يوجد المنتج
        }
        model.addAttribute("product", product);
        return "product-edit";
    }

    // معالجة التحديث
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product) {
        service.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = service.getProductById(id);
        if (product == null) {
            model.addAttribute("errorMessage", "Produit avec ID " + id + " non trouvé.");
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "product-view";
    }
}