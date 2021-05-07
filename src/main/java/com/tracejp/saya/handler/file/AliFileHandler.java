package com.tracejp.saya.handler.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.tracejp.saya.model.enums.AttachmentType;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.properties.AliOssProperties;
import com.tracejp.saya.model.support.TransportFile;
import com.tracejp.saya.model.support.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * <p>阿里文件处理器<p/>
 *
 * @author traceJP
 * @since 2021/4/29 21:22
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "ali.oss", name = "enable", havingValue = "true")
public class AliFileHandler implements FileHandler {

    /**
     * 分片上传初始化保存map-key
     */
    private final String UPLOAD_ID_KEY = "upload";

    /**
     * 分片上传返回MD5结果保存
     */
    private final String UPLOAD_RESULT_KEY = "partetag";

    @Autowired
    private AliOssProperties aliOssProperties;


    @Override
    public void upload(MultipartFile file, String fileKey) {
        OSS client = null;
        try (InputStream in = file.getInputStream()) {
            client = createClient();
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliOssProperties.getBucketName(), fileKey, in);
            client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.warn("阿里OSS服务获取MultipartFile文件Input流出现异常");
        } finally {
            if (Objects.nonNull(client)) {
                client.shutdown();
            }
        }
    }

    @Override
    public UploadResult upload(UploadParam file, TransportFile initFile) {
        OSS client = createClient();
        // 构建上传当前分片请求
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(aliOssProperties.getBucketName());
        uploadPartRequest.setKey(initFile.getHash() + initFile.getExtension());
        uploadPartRequest.setUploadId(file.getIdentifier());
        try (InputStream in = file.getFile().getInputStream()) {
            uploadPartRequest.setInputStream(in);
        } catch (IOException e) {
            log.warn("阿里OSS服务获取MultipartFile文件Input流出现异常");
        }
        uploadPartRequest.setPartSize(file.getCurrentChunkSize());
        uploadPartRequest.setPartNumber(file.getChunkNumber());
        UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);

        // 构建返回数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put(UPLOAD_RESULT_KEY, uploadPartResult.getPartETag());
        UploadResult result = new UploadResult();
        result.setChunkNumber(file.getChunkNumber());
        result.setOtherParam(resMap);

        return result;
    }

    @Override
    public Map<String, Object> initUpload(String fileKey) {
        Map<String, Object> map = new HashMap<>();
        OSS client = createClient();
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(aliOssProperties.getBucketName(),
                fileKey);
        InitiateMultipartUploadResult upResult = client.initiateMultipartUpload(request);
        map.put(UPLOAD_ID_KEY, upResult.getUploadId());
        return map;
    }

    @Override
    public void merge(List<UploadResult> results, TransportFile initFile) {
        OSS client = createClient();
        String fileKey = initFile.getHash() + initFile.getExtension();
        String ossUploadId = (String) initFile.getOtherParam().get(UPLOAD_ID_KEY);
        List<PartETag> partETags = new ArrayList<>();
        for (UploadResult result : results) {
            PartETag partETag = (PartETag) result.getOtherParam().get(UPLOAD_RESULT_KEY);
            partETags.add(partETag);
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(aliOssProperties.getBucketName(), fileKey, ossUploadId, partETags);
        client.completeMultipartUpload(completeMultipartUploadRequest);
        client.shutdown();
    }

    @Override
    public void abort(TransportFile initFile) {
        OSS client = createClient();
        String fileKey = initFile.getHash() + initFile.getExtension();
        String ossUploadId = (String) initFile.getOtherParam().get(UPLOAD_ID_KEY);
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(aliOssProperties.getBucketName(), fileKey, ossUploadId);
        client.abortMultipartUpload(abortMultipartUploadRequest);
        client.shutdown();
    }

    @Override
    public void download(String fileKey, Long totalSize, HttpServletResponse response) {
        OSS client = createClient();
        OSSObject ossObject = client.getObject(aliOssProperties.getBucketName(), fileKey);
        try {
            downloadStream(ossObject.getObjectContent(), response.getOutputStream());
        } catch (IOException e) {
            log.warn("获取响应流出现异常");
        }
        client.shutdown();
    }

    @Override
    public void download(String fileKey, Long start, Long end, HttpServletResponse response) {
        OSS ossClient = createClient();
        GetObjectRequest getObjectRequest = new GetObjectRequest(aliOssProperties.getBucketName(), fileKey);
        getObjectRequest.setRange(start, end);
        OSSObject ossObject = ossClient.getObject(getObjectRequest);
        try {
            downloadStream(ossObject.getObjectContent(), response.getOutputStream());
        } catch (IOException e) {
            log.warn("获取响应流出现异常");
        }
        ossClient.shutdown();
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.ALIOSS;
    }

    /**
     * 创建阿里oss客户端对象
     */
    private OSS createClient() {
        return new OSSClientBuilder().build(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());
    }

    /**
     * 下载流方法，将输入流字节写到输出流字节中
     * @param in InputStream
     * @param out OutputStream
     */
    private void downloadStream(InputStream in, OutputStream out) {
        byte[] buffer = new byte[102400];
        try {
            int n;
            while ((n = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, n);
                out.flush();
            }
        } catch (IOException e) {
            log.warn("阿里OSS下载流出现异常");
        } finally {
            if (Objects.nonNull(in)) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("阿里OSS下载流关闭出现异常");
                }
            }
        }
    }

}
