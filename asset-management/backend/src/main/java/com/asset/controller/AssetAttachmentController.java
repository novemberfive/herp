package com.asset.controller;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetAttachment;
import com.asset.repository.AssetAttachmentMapper;
import com.asset.util.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attachments")
public class AssetAttachmentController {

    private final AssetAttachmentMapper assetAttachmentMapper;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    public AssetAttachmentController(AssetAttachmentMapper assetAttachmentMapper) {
        this.assetAttachmentMapper = assetAttachmentMapper;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('archive:attachment')")
    public Result<PageResult<AssetAttachment>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String assetCode,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) Integer attachmentType) {
        Page<AssetAttachment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetAttachment::getDeleted, 0);
        if (StringUtils.hasText(assetCode)) {
            wrapper.like(AssetAttachment::getAssetCode, assetCode.trim());
        }
        if (StringUtils.hasText(assetName)) {
            wrapper.like(AssetAttachment::getAssetName, assetName.trim());
        }
        if (attachmentType != null) {
            wrapper.eq(AssetAttachment::getAttachmentType, attachmentType);
        }
        wrapper.orderByDesc(AssetAttachment::getUploadTime).orderByDesc(AssetAttachment::getCreateTime);
        Page<AssetAttachment> result = assetAttachmentMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('archive:attachment')")
    public Result<AssetAttachment> detail(@PathVariable Long id) {
        AssetAttachment attachment = assetAttachmentMapper.selectById(id);
        if (attachment == null || Integer.valueOf(1).equals(attachment.getDeleted())) {
            return Result.error("附件不存在");
        }
        return Result.success(attachment);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('archive:attachment')")
    public Result<Void> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long assetId,
            @RequestParam String assetCode,
            @RequestParam String assetName,
            @RequestParam Integer attachmentType,
            @RequestParam(required = false) String remark) throws Exception {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename());
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String storedName = UUID.randomUUID() + "-" + originalName;
        Path directory = Path.of(uploadPath, "attachment-files", datePart).toAbsolutePath().normalize();
        Files.createDirectories(directory);
        Path target = directory.resolve(storedName).normalize();
        file.transferTo(target);

        AssetAttachment attachment = new AssetAttachment();
        attachment.setAssetId(assetId);
        attachment.setAssetCode(assetCode);
        attachment.setAssetName(assetName);
        attachment.setAttachmentName(originalName);
        attachment.setAttachmentType(attachmentType);
        attachment.setFileName(originalName);
        attachment.setFilePath(target.toString());
        attachment.setFileUrl("/api/attachment-files/" + datePart + "/" + storedName);
        attachment.setFileSize(file.getSize());
        attachment.setUploadUser(UserContextUtil.getCurrentUsername());
        attachment.setUploadTime(LocalDateTime.now());
        attachment.setRemark(remark);
        attachment.setDeleted(0);
        assetAttachmentMapper.insert(attachment);
        return Result.success("附件上传成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('archive:attachment')")
    public Result<Void> delete(@PathVariable Long id) {
        AssetAttachment attachment = assetAttachmentMapper.selectById(id);
        if (attachment == null || Integer.valueOf(1).equals(attachment.getDeleted())) {
            return Result.error("附件不存在");
        }
        attachment.setDeleted(1);
        assetAttachmentMapper.updateById(attachment);
        return Result.success("附件删除成功", null);
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('archive:attachment')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        if (ids != null) {
            ids.forEach(this::delete);
        }
        return Result.success("附件批量删除成功", null);
    }
}
