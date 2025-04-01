package net.fangyi.acmsb.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadFileUtil {
    @Getter
    private static String uploadDir;

    @Value("${spring.upload.dir}")
    public void setUploadDir(String uploadDir) {
        UploadFileUtil.uploadDir = uploadDir;
    }

    /**
     * 获取文件保存路径
     *
     * @return File
     * @throws FileNotFoundException
     */
    static List<File> getUploadDirectory(String dir) throws FileNotFoundException {
        // 开发环境获取 target/classes 目录：清理重新编译后就没有了
        File targetPath = new File(ResourceUtils.getURL("classpath:").getPath());
        // System.out.printf("项目运行的绝对路径：" + path.getAbsolutePath());
        // 输出 xx/sky-parent/sky-server/target/classes
        // 生产环境 不存在 target/classes 目录
        if (!targetPath.exists()) {
            // 获取当前运行目录
            targetPath = new File("");
        }

        // 开发环境 resources 目录：可永久保存
        String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";
        //System.out.printf("resources目录路径：" + resourcesPath);
        File path = new File(resourcesPath);


        File upload = new File(path.getAbsolutePath(), dir);
        File uploadTarget = new File(targetPath.getAbsolutePath(), dir);

        // 不存在则创建
        if (!upload.exists()) {
            upload.mkdirs();
        }
        if (!uploadTarget.exists()) {
            uploadTarget.mkdirs();
        }

        List<File> files = new ArrayList<File>();
        files.add(upload);
        files.add(uploadTarget);

        // System.out.printf("当前目录：" + files);

        return files;
    }

    // 上传文件，保存到 resources 和 target 两个目录，兼顾开发测试和永久保存
    public static String upload(MultipartFile myFile, String dir) throws IOException {
        String filePath = "";
        if (!myFile.isEmpty()) {
            try {
                String filename = myFile.getOriginalFilename();
                filename = UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));

                // 之所以保存到 resources 和 target 两个目录，兼顾开发测试和永久保存
                // 只保存到resources目录下每次上传了要重新编译下，target则清理打包后就没有了
                List<File> files = getUploadDirectory(dir);
                //System.out.printf("上传目录：" + files);
                // 注意这里一个文件不能循环同时写入多个目录，保存了第一个，第二个要复制过去
                File curFile = new File(files.get(0), filename);
                myFile.transferTo(curFile);
                FileCopyUtils.copy(curFile, new File(files.get(1), filename));
                //for (File f: files) {
                //File curFile = new File(f, filename);
                //myFile.transferTo(curFile);
                //}
//                filePath = getUploadDir() + "/upload/" + dir + "/" + filename;
                filePath = dir + "/" + filename;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

//    /**
//     * 获取头像文件保存路径
//     *
//     * @return File
//     * @throws FileNotFoundException
//     */
//    public static List<File> getUploadDirectory(String dir) throws FileNotFoundException {
//        // 开发环境 resources 目录：可永久保存
//        String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";
//        //System.out.printf("resources目录路径：" + resourcesPath);
//        File path = new File(resourcesPath);
//        File upload = new File(path.getAbsolutePath(), dir);
//        // 不存在则创建
//        if (!upload.exists()) {
//            upload.mkdirs();
//        }
//        List<File> files = new ArrayList<File>();
//        files.add(upload);
//        return files;
//    }



//    public static String upload(MultipartFile myFile, String dir) throws IOException {
//        String filePath = "";
//        if (!myFile.isEmpty()) {
//            try {
//                String filename = myFile.getOriginalFilename();
//                filename = UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));
//
//                List<File> files = getUploadDirectory(dir);
//                File curFile = new File(files.get(0), filename);
//                myFile.transferTo(curFile);
//                filePath = dir + "/" + filename;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return filePath;
//    }

}