package com.metoo.sqlite.manager.utils.file;
;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 常见压缩文件格式及其后缀：
 * .zip
 *
 * 标准的压缩格式，广泛支持，能够压缩多个文件和文件夹。
 * .rar
 *
 * 高效的压缩格式，常用于大文件的压缩和分卷压缩。需要特定的软件（如 WinRAR）来解压。
 * .7z
 *
 * 由 7-Zip 软件使用的压缩格式，支持高压缩比和多种压缩算法。
 * .tar
 *
 * 不进行压缩，只是将多个文件打包成一个文件，通常与压缩格式结合使用。
 * .tar.gz 或 .tgz
 *
 * 先用 tar 打包，再用 gzip 压缩，常用于 Linux 和 Unix 系统。
 * .tar.bz2
 *
 * 先用 tar 打包，再用 bzip2 压缩，提供比 gzip 更高的压缩比。
 * .tar.xz
 *
 * 先用 tar 打包，再用 xz 压缩，提供高压缩比和较快的解压速度。
 * .gzip 或 .gz
 *
 * 使用 gzip 压缩单个文件，常用于 Linux 和 Unix 系统，通常与 tar 结合使用（如 .tar.gz）。
 * .bz2
 *
 * 使用 bzip2 压缩单个文件，通常提供比 gzip 更高的压缩比。
 * .xz
 *
 * 使用 xz 压缩单个文件，提供高压缩比和较快的解压速度。
 * .ace
 *
 * 较少使用的压缩格式，曾经由 WinACE 软件使用。
 * .lz 或 .lzo
 *
 * 使用 LZO 算法进行压缩，通常用于需要快速解压的场景。
 * .cab
 *
 * Windows 系统中使用的压缩文件格式，主要用于安装程序包。
 * .iso
 *
 * 光盘映像文件，有时使用压缩算法进行压缩。
 */
@Slf4j
public class FileUploadUtils {


    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("zip", "rar");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("application/zip", "application/rar");

    private static final String INVALID_CHARACTERS_REGEX = "[\\\\/:*?\"<>|]";
    private static final Pattern INVALID_CHARACTERS_PATTERN = Pattern.compile(INVALID_CHARACTERS_REGEX);


    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            // 如果没有扩展名，则返回原始文件名
            return fileName;
        }

        return fileName.substring(0, index);
    }

    public static Result upload(MultipartFile file){

        if (file == null || file.isEmpty()) {
            log.warn("上传的文件为空或未选择文件");
            return ResponseUtil.fail("文件为空或未选择文件");
        }

        String uploadDir = getUploadDir();
        Path path = Paths.get(uploadDir);

        Result result = validateFile(file);
        if(result != null){
            return result;
        }

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseUtil.fail("创建目录失败");
            }
        }

        try {
            Path filePath = path.resolve(file.getOriginalFilename());
            file.transferTo(filePath.toFile());
            return ResponseUtil.ok("文件上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件上传失败", e);
            return ResponseUtil.fail("文件上传失败");
        }
    }

    public static Result validateFile(MultipartFile file) {
        if (file == null) {
            return ResponseUtil.fail("文件为空或未选择文件");
        }

        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();

        if (!validateFileName(fileName)) {
            return ResponseUtil.fail("文件名包含非法字符");
    }

        if (!validateFileExtension(fileName)) {
            return ResponseUtil.fail("不允许的文件扩展名");
        }

        if (!validateFileSize(fileSize, 1073741824)) { // 1 GB = 1073741824 bytes
            return ResponseUtil.fail("文件大小超过限制");
        }
        return null;
    }

    private static boolean validateFileName(String fileName) {
        // 使用正则表达式校验文件名中是否包含非法字符
        return fileName != null && !INVALID_CHARACTERS_PATTERN.matcher(fileName).find();
    }

    // 校验文件信息
    public static boolean validateFileExtension(String fileName){
        String fileExtension = getFileExtension(fileName);
        return validateFileExtension(fileExtension, ALLOWED_EXTENSIONS);
    }

    // 获取文件后缀
    private static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index > 0) ? fileName.substring(index + 1) : "";
    }

    // 校验文件后缀
    public static boolean validateFileExtension(String fileExtension, String... allowedExtensions) {
        for (String ext : allowedExtensions) {
            if (fileExtension.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateFileExtension(String fileExtension, List<String> allowedExtensions) {
        for (String ext : allowedExtensions) {
            if (fileExtension.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }


    // 校验文件大小
    public static boolean validateFileSize(long fileSize, long maxSizeBytes){
        return fileSize <= maxSizeBytes;
    }

    public static String getUploadDir(){
        String rootDirectory = System.getProperty("user.dir");
        Path uploadPath = Paths.get(rootDirectory, "files");
        return uploadPath.toString();
    }



    public static void main(String[] args) {
        // 获取应用程序的工作目录，通常是项目根目录
        String rootDirectory = System.getProperty("user.dir");
        Path uploadPath = Paths.get(rootDirectory, "uploads");
        log.info("打印项目根目录: UploadPath {}", uploadPath.toString());

        String workingDirectory = Paths.get("").toAbsolutePath().toString();
        log.info("打印项目根目录: workingDirectory {}", workingDirectory);

        ApplicationHome home = new ApplicationHome(FileUploadUtils.class);
        File jarDir = home.getSource().getParentFile();
        log.info("打印项目根目录: workingDirectory {}", jarDir.getAbsolutePath());

    }

}
