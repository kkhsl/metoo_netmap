package com.metoo.sqlite.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.enums.VersionResultType;
import com.metoo.sqlite.entity.License;
import com.metoo.sqlite.entity.Version;
import com.metoo.sqlite.gather.utils.VersionUtils;
import com.metoo.sqlite.manager.Gather6ManagerController;
import com.metoo.sqlite.manager.api.remote.VersionManagerRemote;
import com.metoo.sqlite.manager.api.remote.VersionStatusUpdateRemote;
import com.metoo.sqlite.manager.utils.file.FileVersionUtils;
import com.metoo.sqlite.service.ILicenseService;
import com.metoo.sqlite.service.IVersionService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.encryption.AesEncryptUtils;
import com.metoo.sqlite.utils.license.SystemInfoUtils;
import com.metoo.sqlite.utils.version.DownloadAndExecuteBatFromZip;
import com.metoo.sqlite.vo.LicenseVo;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 升级版本服务
 *
 * @author zzy
 * @version 1.0
 * @date 2024/9/21 14:10
 */
@Service
@Slf4j
public class UpdateVersionService {
    @Autowired
    private VersionManagerRemote versionManagerRemote;
    @Autowired
    private VersionStatusUpdateRemote versionStatusUpdateRemote;
    @Autowired
    private ILicenseService licenseService;
    @Autowired
    private IVersionService versionService;
    @Autowired
    private AesEncryptUtils aesEncryptUtils;
    @Autowired
    private GatherAllInOneService allInOneService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Value("${version.download.url}")
    private String downloadUrl;
    private final ReentrantLock lock = new ReentrantLock();

    private Thread threadVersion;

    /**
     * 自动或手动升级版本入口
     *
     * @return
     */
    public int updateVersion() {
        if (lock.tryLock()) {
            // 保存当前线程的引用
            threadVersion = Thread.currentThread();
            try {
                //判断当前采集ipv6是否正在采集
                Result result = allInOneService.startGather(1);
                if (result.getCode() != null && result.getCode() == 1002) {
                    log.error("正在采集,当前状态不允许更新");
                    return VersionResultType.STATUS.getCode();
                } else if (result.getCode() != null && result.getCode() == 1001) {
                    // 判断版本号是否以下载
                    boolean updateFlag = true;
                    String readState = FileVersionUtils.readState(Global.version_state, Global.version_state_name);
                    if (StringUtil.isNotEmpty(readState)
                            && Math.abs(Integer.parseInt(readState)) > 1) {
                        //已下载过了 不在执行下载
                        updateFlag = false;
                    }
                    //当前状态允许更新
                    //  获取当前版本号和客户端编码
                    Version version = versionService.selectObjByOne();
                    Long unitId = getUnitId();
                    if (null == unitId) {
                        //获取不到单位编码
                        log.error("单位编码获取出现问题");
                        return VersionResultType.ERROR.getCode();
                    }
                    //  服务器端查询是否有版本更新
                    String versionResult = versionManagerRemote.call(versionManagerRemote.versionParam(unitId, version.getVersion()));
                    if (StrUtil.isNotEmpty(versionResult)) {
                        JSONObject json = JSONObject.parseObject(versionResult, JSONObject.class);
                        String appVersion = json.getString("appVersion");
                        Long appVersionId = json.getLong("appVersionId");
                        // 如果为补丁版本 列如：1.1.2.[0]
                        if (appVersion.contains("\\[")) {
                            //存在补丁版本
                            if (updateFlag) {
                                // 标记可以更新了
                                FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "1");
                                //版本是较新版本，下载升级
                                try {
                                    downloadPatchVersion(appVersionId);
                                    // 标记已下载状态
                                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "2");// 下载完成
                                } catch (Exception e) {
                                    //下载失败
                                    log.error("下载新版本失败：{}", e.getMessage());
                                    // 标记下载失败
                                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "-2");
                                }
                            }
                            try {
                                //已下载，则继续更新sql脚本
                                executeSqlFile(Global.versionPatchDb);
                                //标记为已更新完成
                                FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "3");
                                //更新版本号
                                FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_info_name, appVersion);
                                log.info("版本更新完成，版本号：{}", appVersion);
                                return VersionResultType.SUCCESS.getCode();
                            } catch (Exception e) {
                                log.error("执行版本升级出现错误：{}", e);
                                // 标记升级执行失败
                                FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state, "-3");
                            }
                        } else {
                            //其他非补丁版本
                            //  比较版本号，如果版本号不一致，则下载新版本
                            int matchResult = VersionUtils.compare(appVersion, version.getVersion());
                            if (matchResult > 0) {
                                if (updateFlag) {
                                    // 标记可以更新了
                                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "1");
                                    //版本是较新版本，下载升级
                                    try {
                                        downloadVersion(appVersionId);
                                        // 标记已下载状态
                                        FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "2");// 下载完成
                                    } catch (Exception e) {
                                        //下载失败
                                        log.error("下载新版本失败：{}", e.getMessage());
                                        // 标记下载失败
                                        FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "-2");
                                    }

                                }
                                try {
                                    //已下载
                                    String extractDirectory = Global.versionUnzip;
                                    // 执行 .bat 文件
                                    String batFilePath = extractDirectory + File.separator + Global.versionScriptName;
                                    DownloadAndExecuteBatFromZip.executeBatchFile(batFilePath);
                                    //标记为已更新完成
                                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "3");
                                    //更新版本号
                                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_info_name, appVersion);
                                    log.info("版本更新完成，版本号：{}", appVersion);
                                    return VersionResultType.SUCCESS.getCode();
                                } catch (Exception e) {
                                    log.error("执行版本升级出现错误：{}", e);
                                    // 标记升级执行失败
                                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state, "-3");
                                }
                            }
                        }
                    } else {
                        log.info("当前无版本更新");
                        return VersionResultType.NO.getCode();
                    }
                }
            } catch (Exception ex) {
                log.error("客户端定时任务更新定时任务出错：{}", ex);
                return VersionResultType.FAIL.getCode();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                if (threadVersion != null) {
                    // 清空线程引用
                    threadVersion = null;
                }
            }
        } else {
            log.info("版本正在更新");
            return VersionResultType.DOING.getCode();
        }
        return VersionResultType.STATUS.getCode();
    }

    /**
     * 下载更新版本
     *
     * @param versionId
     * @throws IOException
     */
    private void downloadVersion(Long versionId) throws IOException {
        String zipUrl = downloadUrl + "/" + versionId;
        String fileName = Global.versionName;
        String zipFilePath = Global.versionPath + File.separator + fileName;
        String extractDirectory = Global.versionUnzip;
        DownloadAndExecuteBatFromZip.ensureDirectoryExists(Global.versionPath);
        log.info("目录已确认存在或创建成功：" + Global.versionPath);
        DownloadAndExecuteBatFromZip.ensureDirectoryExists(Global.versionUnzip);
        log.info("目录已确认存在或创建成功：" + Global.versionUnzip);
        // 下载压缩包
        DownloadAndExecuteBatFromZip.downloadFile(zipUrl, zipFilePath);
        // 解压压缩包
        DownloadAndExecuteBatFromZip.unzip(zipFilePath, extractDirectory);
    }

    /**
     * 下载补丁版本
     *
     * @param versionId
     * @throws IOException
     */
    private void downloadPatchVersion(Long versionId) throws IOException {
        String zipUrl = downloadUrl + "/" + versionId;
        String fileName = Global.versionName;
        String zipFilePath = Global.versionPath + File.separator + fileName;
        String extractDirectory = Global.versionPatchUnZip;
        DownloadAndExecuteBatFromZip.ensureDirectoryExists(Global.versionPath);
        log.info("目录已确认存在或创建成功：" + Global.versionPath);
        // 下载压缩包
        DownloadAndExecuteBatFromZip.downloadFile(zipUrl, zipFilePath);
        // 解压压缩包
        DownloadAndExecuteBatFromZip.unzip(zipFilePath, extractDirectory);
    }

    /**
     * 执行sql文件
     *
     * @param filePath
     */
    public void executeSqlFile(String filePath) throws Exception {
        StringBuilder sql = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sql.append(line).append("\n");
            }
        }
        if (StrUtil.isNotEmpty(sql.toString())) {
            List<String> sqlStatements = Arrays.asList(sql.toString().split(";"));
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                Connection connection = sqlSession.getConnection();
                try (Statement statement = connection.createStatement()) {
                    for (String sqlStatement : sqlStatements) {
                        if (!sqlStatement.trim().isEmpty()) {
                            statement.execute(sqlStatement.trim());
                        }
                    }
                }
                sqlSession.commit();
            }
        }
    }

    /**
     * 获取单位编码
     *
     * @return
     */
    private Long getUnitId() throws Exception {
        List<License> licenseList = licenseService.query();
        if (licenseList.size() <= 0) {
            log.error("未经授权，不允许升级");
            return null;
        }
        License obj = licenseList.get(0);
        String uuid = SystemInfoUtils.getWindowsBiosUUID();
        if (!uuid.equals(obj.getSystemSN())) {
            log.error("未经授权，不允许升级");
            return null;
        }
        String licenseInfo = aesEncryptUtils.decrypt(obj.getLicense());
        if (StringUtil.isEmpty(licenseInfo)) {
            log.error("未经授权，不允许升级");
            return null;
        }
        LicenseVo license = JSONObject.parseObject(licenseInfo, LicenseVo.class);
        // TODO: 2024/9/21 默认为单位1的测试数据 
        return NumberUtil.isNumber(license.getUnit_id()) ? Long.parseLong(license.getUnit_id()) : 1L;
    }

    /**
     * 更新版本信息到服务器
     *
     * @param
     */
    public void updateVersionToServer() {
        try {
            //  获取当前版本号和客户端编码
            Version version = versionService.selectObjByOne();
            Long unitId = null;
            try {
                unitId = getUnitId();
            } catch (Exception e) {
                log.error("单位编码获取出现问题:{}", e);
            }
            if (null == unitId) {
                //获取不到单位编码
                log.error("单位编码获取出现问题");
            }
            versionStatusUpdateRemote.call(versionStatusUpdateRemote.versionUpdateParam(unitId, version.getVersion(), ""));
        } catch (Exception ex) {
            log.error("版本信息更新到服务器出现异常：{}", ex);
        }
    }

    /**
     * 获取最新版本信息
     *
     * @return
     */
    public String getLastVersionInfo() {
        //  获取当前版本号和客户端编码
        Version version = versionService.selectObjByOne();
        Long unitId = null;
        try {
            unitId = getUnitId();
        } catch (Exception e) {
            log.error("单位编码获取出现问题:{}", e);
        }
        if (null == unitId) {
            //获取不到单位编码
            log.error("单位编码获取出现问题");
        }
        //  服务器端查询是否有版本更新
        String versionResult = versionManagerRemote.call(versionManagerRemote.versionParam(unitId, version.getVersion()));
        if (StrUtil.isNotEmpty(versionResult)) {
            JSONObject json = JSONObject.parseObject(versionResult, JSONObject.class);
            String appVersion = json.getString("appVersion");
            //  比较版本号，如果版本号不一致，则下载新版本
            if (!StrUtil.equals(appVersion,version.getVersion())) {
                // 有最新版本
                return appVersion;
            }
        }
        return "";
    }
}

