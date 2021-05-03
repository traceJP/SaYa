package com.tracejp.saya.task;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tracejp.saya.model.entity.Volume;
import com.tracejp.saya.service.VolumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>用户容量刷新定时任务<p/>
 *
 * @author traceJP
 * @since 2021/5/2 16:36
 */
@Slf4j
@Component
public class VolumeResetTask {

    /**
     * 单次分页查询个数
     */
    private static final Integer PAGE_SIZE = 100;

    /**
     * 单组执行间隔时间（毫秒）
     */
    private static final Integer GROUP_EXECUTE_TIME = 3000;

    /**
     * 异常重试次数
     */
    private static final Integer ABNORMAL_RETRY_TAGE = 3;

    @Autowired
    private VolumeService volumeService;


    @Scheduled(cron = "@monthly")
    public synchronized void run() {
        log.info("用户cdn使用量刷新任务启动");
        int userCount = 0;
        int pageCount = 1;
        IPage<Volume> volumeIPage;
        try {
            do {
                volumeIPage = volumeService.listOfPage(pageCount, PAGE_SIZE);
                for (Volume volume : volumeIPage.getRecords()) {
                    if (!volume.getVolumeCdnTotal().equals(volume.getVolumeCdnUsed())) {
                        refreshCdn(volume);
                    }
                    userCount++;
                }
                pageCount++;
                // 下一分页组执行前等待时间
                super.wait(GROUP_EXECUTE_TIME);
            } while (pageCount <= volumeIPage.getPages());
        } catch (InterruptedException e) {
            log.warn("cdn容量刷新线程睡眠异常");
        }
        log.info("用户cdn使用量刷新任务结束，共计刷新{}个用户", userCount);
    }

    /**
     * 刷新修改用户记录，修改失败重试 ABNORMAL_RETRY_TAGE 次，间隔 3 秒
     * @param volume Volume
     */
    @Async
    public void refreshCdn(Volume volume) {
        volume.setVolumeCdnUsed(volume.getVolumeCdnTotal());
        for (int i = 1; i <= ABNORMAL_RETRY_TAGE; i++) {
            try {
                volumeService.updateById(volume);
                return;
            } catch (Exception e) {
                log.warn("cdn容量刷新异常：异常用户id={};异常原因={};重试次数={};", volume.getDriveId(), e, i);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    log.warn("cdn容量刷新线程睡眠异常");
                }
            }
        }
    }

}
