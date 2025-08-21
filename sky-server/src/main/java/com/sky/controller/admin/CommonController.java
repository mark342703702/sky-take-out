package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Api(tags = "通用接口")
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

     @PostMapping("/upload")
     public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}", file);
         try {
             //原始文件名
             String originalFilename = file.getOriginalFilename();
             //截取原始文件名
             int lastIndex = originalFilename.lastIndexOf(".");
             //扩展
             String extension = originalFilename.substring(lastIndex + 1);

             //构造新的文件名
             String objName = UUID.randomUUID().toString() + "." + extension;
             log.info("文件名后缀为:{}", objName);

             return Result.success("https://dummyimage.com/300x300.png");
         } catch (Exception e) {
             log.error("文件上传失败：{}",e );
         }
         return Result.error(MessageConstant.UPLOAD_FAILED);
     }
}
