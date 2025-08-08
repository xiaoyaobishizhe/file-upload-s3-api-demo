package com.yogurter.s3.service;

import com.yogurter.s3.dto.s3.*;

public interface S3Service {
    CreateUploadDTO createMultipartUpload(CreateUploadRequestDTO dto);

    ListPartsDTO listParts(ListPartsRequestDTO dto);

    SignPartDTO signPart(SignPartRequestDTO dto);

    void abortMultipartUpload(AbortUploadRequestDTO dto);

    CompleteUploadDTO completeMultipartUpload(CompleteUploadRequestDTO dto);
}
