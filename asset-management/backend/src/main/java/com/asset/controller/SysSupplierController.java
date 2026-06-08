package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.SysSupplier;
import com.asset.service.SysSupplierService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统供应商控制器
 */
@RestController
@RequestMapping("/suppliers")
public class SysSupplierController {

    private final SysSupplierService sysSupplierService;

    public SysSupplierController(SysSupplierService sysSupplierService) {
        this.sysSupplierService = sysSupplierService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('basic:supplier')")
    public Result<List<SysSupplier>> getSupplierList() {
        return sysSupplierService.getSupplierList();
    }

    @GetMapping("/enabled")
    @PreAuthorize("hasAuthority('basic:supplier')")
    public Result<List<SysSupplier>> getEnabledSupplierList() {
        return sysSupplierService.getEnabledSupplierList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('basic:supplier:create')")
    public Result<Void> createSupplier(@RequestBody SysSupplier supplier) {
        return sysSupplierService.createSupplier(supplier);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('basic:supplier:edit')")
    public Result<Void> updateSupplier(@RequestBody SysSupplier supplier) {
        return sysSupplierService.updateSupplier(supplier);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('basic:supplier:delete')")
    public Result<Void> deleteSupplier(@PathVariable Long id) {
        return sysSupplierService.deleteSupplier(id);
    }
}
