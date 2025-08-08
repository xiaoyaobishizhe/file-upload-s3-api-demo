package com.yogurter.s3.service.impl;

import com.yogurter.s3.dto.s3.*;
import com.yogurter.s3.properties.S3Properties;
import com.yogurter.s3.service.S3Service;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public S3ServiceImpl(S3Client s3Client, S3Presigner s3Presigner, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.s3Properties = s3Properties;
    }

    @Override
    public CreateUploadDTO createMultipartUpload(CreateUploadRequestDTO dto) {
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(builder -> builder
                .bucket(s3Properties.getBucket())
                .key("uploads/" + UUID.randomUUID() + "-" + dto.getFileName())
                .contentType(dto.getContentType()));

        CreateUploadDTO result = new CreateUploadDTO();
        result.setUploadId(response.uploadId());
        result.setKey(response.key());
        return result;
    }

    @Override
    public ListPartsDTO listParts(ListPartsRequestDTO dto) {
        ListPartsResponse response = s3Client.listParts(builder -> builder
                .bucket(s3Properties.getBucket())
                .key(dto.getKey())
                .uploadId(dto.getUploadId()));

        List<ListPartsDTO.Part> parts = new ArrayList<>();
        for (Part part : response.parts()) {
            System.out.println(part.eTag());
            ListPartsDTO.Part p = new ListPartsDTO.Part();
            p.setPartNumber(part.partNumber());
            p.setSize(part.size());
            p.setEtag(part.eTag());
            parts.add(p);
        }

        ListPartsDTO result = new ListPartsDTO();
        result.setParts(parts);
        return result;
    }

    @Override
    public SignPartDTO signPart(SignPartRequestDTO dto) {
        String url = s3Presigner.presignUploadPart(builder -> builder
                .signatureDuration(Duration.ofMinutes(s3Properties.getDurationMinutes()))
                .uploadPartRequest(b -> b
                        .bucket(s3Properties.getBucket())
                        .key(dto.getKey())
                        .uploadId(dto.getUploadId())
                        .partNumber(dto.getPartNumber()))).url().toString();

        SignPartDTO result = new SignPartDTO();
        result.setUrl(url);
        return result;
    }

    @Override
    public void abortMultipartUpload(AbortUploadRequestDTO dto) {
        s3Client.abortMultipartUpload(builder -> builder
                .bucket(s3Properties.getBucket())
                .key(dto.getKey())
                .uploadId(dto.getUploadId()));
    }

    @Override
    public CompleteUploadDTO completeMultipartUpload(CompleteUploadRequestDTO dto) {
        List<CompletedPart> parts = new ArrayList<>();
        for (CompleteUploadRequestDTO.Part part : dto.getParts()) {
            parts.add(CompletedPart.builder()
                    .partNumber(part.getPartNumber())
                    .eTag(part.getEtag())
                    .build());
        }

        String location = s3Client.completeMultipartUpload(builder -> builder
                .bucket(s3Properties.getBucket())
                .key(dto.getKey())
                .uploadId(dto.getUploadId())
                .multipartUpload(b -> b.parts(parts))).location();

        CompleteUploadDTO result = new CompleteUploadDTO();
        result.setLocation(location);
        return result;
    }
}
