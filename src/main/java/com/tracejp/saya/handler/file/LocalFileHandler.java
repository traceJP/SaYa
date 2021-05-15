package com.tracejp.saya.handler.file;

import cn.hutool.core.util.IdUtil;
import com.tracejp.saya.model.enums.AttachmentType;
import com.tracejp.saya.model.params.UploadParam;
import com.tracejp.saya.model.properties.LocalPathProperties;
import com.tracejp.saya.model.support.TransportFile;
import com.tracejp.saya.model.support.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * <p>本地文件处理器<p/>
 *
 * @author traceJP
 * @since 2021/4/24 18:54
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "local.file", name = "enable", havingValue = "true", matchIfMissing = true)
public class LocalFileHandler implements FileHandler {

    /**
     * map其他参数key
     */
    private static final String FILENAME_MAP_KEY = "chunkName";

    @Autowired
    LocalPathProperties basePath;

    @Override
    public void upload(MultipartFile file, String fileKey) {
        String path = FileHandler.normalizeDirectory(basePath.getFileSave()) + fileKey;
        saveLocal(Paths.get(path), file);
    }

    @Override
    public UploadResult upload(UploadParam file, TransportFile initFile) {
        String filenameSuffix = IdUtil.fastSimpleUUID();
        String path = FileHandler.normalizeDirectory(basePath.getFileTmp()) +
                file.getIdentifier() + "-" + filenameSuffix;
        saveLocal(Paths.get(path), file.getFile());

        // 构造返回结果
        UploadResult result = new UploadResult();
        result.setChunkNumber(file.getChunkNumber());
        Map<String, Object> map = new HashMap<>();
        map.put(FILENAME_MAP_KEY, filenameSuffix);
        result.setOtherParam(map);
        return result;
    }

    @Override
    public void merge(List<UploadResult> results, TransportFile initFile) {

        // 文件保存路径
        String filePath = FileHandler.normalizeDirectory(basePath.getFileSave()) +
                initFile.getHash() + initFile.getExtension();

        // 临时文件保存路径前缀
        String prefix = FileHandler.normalizeDirectory(basePath.getFileTmp()) +
                initFile.getUploadId() + "-";

        // list排序
        results.sort(Comparator.comparing(UploadResult::getChunkNumber));

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] buffer = new byte[102400];
            for (UploadResult result : results) {
                String chunkPath = prefix + result.getOtherParam().get(FILENAME_MAP_KEY);
                try (InputStream in = new BufferedInputStream(new FileInputStream(chunkPath))) {
                    int len;
                    while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                        out.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    log.warn("临时分片文件流读取异常");
                }
            }
        } catch (IOException e) {
            log.warn("本地保存文件流读取异常");
        }

        // 清除所有临时分片
        abort(initFile);

        log.trace("文件合并成功");
    }

    @Override
    public void abort(TransportFile initFile) {
        String folderPath = FileHandler.normalizeDirectory(basePath.getFileTmp());
        File tmpFolder = new File(folderPath);
        String[] listAll = tmpFolder.list();
        if (Objects.nonNull(listAll)) {
            // 遍历本地临时文件夹
            for (String filename : listAll) {
                if (StringUtils.equals(filename.split("-")[0], initFile.getUploadId())) {
                   File file = new File(folderPath + filename);
                   if (!file.delete()) {
                       log.warn("临时文件删除失败");
                   }
                }
            }
        }

        log.trace("本地临时文件清除完成");
    }

    @Override
    public void download(String fileKey, Long start, Long end, HttpServletResponse response) {
        String path = FileHandler.normalizeDirectory(basePath.getFileSave()) + fileKey;
        try (InputStream in = new BufferedInputStream(new FileInputStream(path))) {
            OutputStream out = response.getOutputStream();
            if (in.skip(start) != start) {
                log.warn("下载文件：流跳过字节数未对齐");
            }
            byte[] buffer = new byte[102400];
            int rangeLength = Math.toIntExact(end - start + 1L);
            // 已读字节数
            int sum = 0;
            // 单次读取字节数
            int length = 0;
            while (sum < rangeLength || length == -1) {
                int len = Math.min((rangeLength - sum), buffer.length);
                length = in.read(buffer, 0, len);
                sum += length;
                out.write(buffer, 0, length);
                out.flush();
            }
        } catch (IOException e) {
            log.warn("本地文件流读取异常");
        }

        log.trace("文件下载结束");
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.LOCAL;
    }

    /**
     * 保存文件到本地目标路径
     * @param path 本地路径
     * @param file 文件
     */
    private void saveLocal(Path path, MultipartFile file) {
        try {
            file.transferTo(path);
        } catch (IOException e) {
            log.warn("上传保存至本地时出现异常");
        }
    }

}
