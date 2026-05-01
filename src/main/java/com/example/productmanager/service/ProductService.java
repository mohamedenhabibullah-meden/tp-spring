//package com.example.productmanager.service;
//
//import com.example.productmanager.model.Product;
//import com.example.productmanager.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;

//@Service
//public class ProductService {
//    @Autowired
//    private ProductRepository repository;
//
//    public List<Product> getAllProducts() {
//        return repository.findAll();
//    }
//
//    public Product getProductById(Long id) {
//        return repository.findById(id).orElse(null);
//    }
//
//    public Product saveProduct(Product product) {
//        return repository.save(product);
//    }
//
//    public void deleteProduct(Long id) {
//        repository.deleteById(id);
//    }
//}

package com.example.productmanager.service;

import com.example.productmanager.model.Product;
import com.example.productmanager.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j  // لتسجيل الأحداث والمساعدة في تتبع الأخطاء
@Service
@Transactional  // ضمان أن عمليات الكتابة تتم ضمن معاملة واحدة
public class ProductService {

    @Autowired
    private ProductRepository repository;

    // عملية قراءة فقط - تحسين الأداء
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("Récupération de tous les produits");
        return repository.findAll();
    }

    // عملية قراءة فقط
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.info("Recherche du produit avec ID : {}", id);
        // إرجاع null إذا لم يوجد (يمكن تطويره لرمي استثناء مخصص)
        return repository.findById(id).orElse(null);
    }

    // إضافة أو تحديث منتج مع التحقق من صحة البيانات الأساسية
    public Product saveProduct(Product product) {
        if (product == null) {
            log.error("Tentative d'enregistrement d'un produit null");
            throw new IllegalArgumentException("Le produit ne peut pas être null");
        }

        // مثال للتحقق من صحة السعر (اختياري)
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }

        // إذا كان المنتج موجوداً (id != null) وليس موجوداً في قاعدة البيانات، نمنع الإنشاء التلقائي
        if (product.getId() != null && !repository.existsById(product.getId())) {
            log.warn("Tentative de mise à jour d'un produit inexistant avec ID : {}", product.getId());
            throw new IllegalArgumentException("Produit avec ID " + product.getId() + " n'existe pas, utilisez save pour un nouveau produit");
        }

        Product saved = repository.save(product);
        log.info("Produit sauvegardé avec succès : {}", saved);
        return saved;
    }

    // حذف منتج مع التحقق من وجوده أولاً
    public void deleteProduct(Long id) {
        if (id == null) {
            log.error("Tentative de suppression avec ID null");
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }

        if (!repository.existsById(id)) {
            log.warn("Tentative de suppression d'un produit inexistant avec ID : {}", id);
            throw new IllegalArgumentException("Produit avec ID " + id + " n'existe pas");
        }

        repository.deleteById(id);
        log.info("Produit supprimé avec succès, ID : {}", id);
    }

    // méthode utilitaire pour vérifier l'existence (peut être utile dans le controller)
    @Transactional(readOnly = true)
    public boolean productExists(Long id) {
        return id != null && repository.existsById(id);
    }
}