package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.manager.utils.jx.EncrypUtils;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files/encrypt/decrypt")
public class EncryptManagerController {

//    curl -F "file=@/path/to/your/file.txt" http://localhost:8080/files/upload

    private static final String UPLOAD_DIR = "uploads"; // 文件上传的根目录

    /**
     * Paths.get(path, fileName + ".txt")：构造要写入的文件路径。
     * data.getBytes()：将 data 字符串转换为字节数组。
     * StandardOpenOption.CREATE：如果文件不存在，则创建新文件。
     * StandardOpenOption.TRUNCATE_EXISTING：如果文件已经存在，则截断（清空）文件内容，然后写入新的数据。
     * @param file
     * @param path
     * @return
     */
    @PostMapping("/general")
    public Result uploadFile(@RequestParam("file") MultipartFile file, String path) {
        if (file.isEmpty()) {
            return ResponseUtil.fail("未选择文件");
        }
        if (StringUtil.isEmpty(path)) {
            return ResponseUtil.fail("未输入文件生成路径");
        }

        try {
            String data = "";
            String fileName = "";
            // 保存文件并获取文件所在目录
            String content = readFileContent(file);
            try {
                JSONObject.parseObject(content);
                try {
                    data = EncrypUtils.encrypt(content);
                    fileName = "encrypt";
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();

                try {
                    data = EncrypUtils.decrypt(content);
                    fileName = "decrypt";
                } catch (Exception j) {
                    j.printStackTrace();
                }
            }
            try {
                Files.write(Paths.get(path, fileName + ".txt"), data.getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                if(fileName.equals("decrypt")){
                    return ResponseUtil.okMsg("文件解密成功");
                }else if(fileName.equals("encrypt")){
                    return ResponseUtil.okMsg("文件加密成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseUtil.error("系统异常");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }

    private String readFileContent(MultipartFile file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
        }

        return contentBuilder.toString();
    }
}
