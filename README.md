# 存储桶管理

```java
// 创建存储桶
s3Client.createBucket(builder -> builder.bucket("test"));

// 列出存储桶
s3Client.listBuckets().buckets().forEach(bucket -> {
    ZonedDateTime zonedDateTime = bucket.creationDate().atZone(ZoneId.systemDefault());
    String datetime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    System.out.println("存储桶: " + bucket.name() + ", 创建时间: " + datetime);
});

// 删除存储桶
s3Client.deleteBucket(builder -> builder.bucket("test"));
```

# 对象管理

```java
// 上传对象
s3Client.putObject(builder -> builder
                .bucket("test")
                .key("files/test.png"),
        RequestBody.fromFile(new File("C:\\profile.png")));

// 复制对象
s3Client.copyObject(builder -> builder
        .sourceBucket("test")
        .sourceKey("files/test.png")
        .destinationBucket("new")
        .destinationKey("img/a.png"));

// 删除对象
s3Client.deleteObject(builder -> builder
        .bucket("new")
        .key("img/a.png"));

// 下载对象
s3Client.getObject(builder -> builder
                .bucket("test")
                .key("files/test.png"),
        ResponseTransformer.toFile(new File("C:\\Users\\xybsz\\a.png")));

// 列出对象
s3Client.listObjects(builder -> builder
                .bucket("ids")
                .prefix("files")).contents()
        .forEach(s3Object -> System.out.println(s3Object.key()));
```

# 搭建 MinIO S3 服务

```shell
docker run -itd --name minio --restart always \
    -p 9000:9000 -p 9090:9090 \
    -e "MINIO_ROOT_USER=admin" \
    -e "MINIO_ROOT_PASSWORD=12345678" \
    -v /etc/localtime:/etc/localtime:ro \
    minio/minio:RELEASE.2025-07-23T15-54-02Z \
    server /data --console-address ":9090"
```

> MinIO 后台管理员的账号至少要 3 个字符，密码至少要 8 个字符。