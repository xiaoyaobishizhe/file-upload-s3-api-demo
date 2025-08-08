package com.yogurter.s3.controller;

import com.yogurter.s3.dto.s3.*;
import com.yogurter.s3.service.impl.S3ServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3/multipart")
public class S3Controller {
    private final S3ServiceImpl s3Service;

    public S3Controller(S3ServiceImpl s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/create")
    public CreateUploadDTO createMultipartUpload(@RequestBody CreateUploadRequestDTO dto) {
        return s3Service.createMultipartUpload(dto);
    }

    @PostMapping("/list-parts")
    public ListPartsDTO listParts(@RequestBody ListPartsRequestDTO dto) {
        return s3Service.listParts(dto);
    }

    @PostMapping("/sign-part")
    public SignPartDTO signPart(@RequestBody SignPartRequestDTO dto) {
        return s3Service.signPart(dto);
    }

    @PostMapping("/abort")
    public void abortMultipartUpload(@RequestBody AbortUploadRequestDTO dto) {
        s3Service.abortMultipartUpload(dto);
    }

    @PostMapping("/complete")
    public CompleteUploadDTO completeMultipartUpload(@RequestBody CompleteUploadRequestDTO dto) {
        return s3Service.completeMultipartUpload(dto);
    }
}
