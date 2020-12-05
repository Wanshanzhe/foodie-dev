package com.wsz.controller.center;

import com.wsz.controller.BaseController;
import com.wsz.pojo.Users;
import com.wsz.pojo.bo.center.CenterUserBO;
import com.wsz.pojo.vo.UsersVO;
import com.wsz.resource.FileUpload;
import com.wsz.service.center.CenterUserService;
import com.wsz.utils.CookieUtils;
import com.wsz.utils.DateUtil;
import com.wsz.utils.IMOOCJSONResult;
import com.wsz.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author by 完善者
 * @date 2020/10/8 19:54
 * @DESC
 */

@Api(value = "用户信息接口",tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "用户头像修改",notes = "用户头像修改",httpMethod = "POST")
    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(@ApiParam(name = "userId",value = "用户id",required = true)
                                      @RequestParam String userId,
                                      @ApiParam(name = "file",value = "用户头像",required = true)
                                      MultipartFile file,
                                      HttpServletRequest request,
                                      HttpServletResponse response
                                      ) {

        //定义头像保存的地址
//        String fileSpace = IMAGE_USER_FACE_LOCATION;
        String fileSpace = fileUpload.getImageUserFaceLocation();
        //在路径上为每一个用户增加一个userid，用于区分不同用户上传
        String uploadPathPrefix = "/" + userId;
        //开始文件上传
        if (file != null){
            FileOutputStream  fileOutputStream = null;
            try {
                //获取上传文件的名称
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)){
                    //文件重命名 imooc-face.png -> ["imooc-face","png"]
                    String[] fileNameArr = fileName.split("\\.");
                    //获取文件的后缀名
                    String suffix = fileNameArr[fileNameArr.length-1];
                    if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")){
                        return IMOOCJSONResult.errorMsg("图片格式不正确！");
                    }
                    //face-{userid}.png
                    //文件名称重组 覆盖式上传，增量式：额外拼接当前时间
                    String newFileName = "face-" + userId + "." + suffix;
                    //上传的头像最终保存的位置
                    String finalFacePath = fileSpace + uploadPathPrefix + "/" + newFileName;
                    //用于提供给web服务的地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null){
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    //文件输出保存到目录
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream );
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try{
                    if (fileOutputStream != null){
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }else {
            return IMOOCJSONResult.errorMsg("文件不能为空！");
        }
        //更新用户头像到数据库
        String imageUrl = fileUpload.getImageServerUrl();
        //由于浏览器可能存在缓存的情况，所以在这里我们需要加上时间戳来保证更新后的图片能够及时刷新
        String finalUserFaceUrl = imageUrl + uploadPathPrefix + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        //更新头像到数据库
        Users userResult = centerUserService.updateUserFace(userId,finalUserFaceUrl);
        UsersVO usersVO = conventUsersVO(userResult);
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "修改用户信息",notes = "修改用户信息",httpMethod = "POST")
    @PostMapping("/update")
    public IMOOCJSONResult update(@ApiParam(name = "userId",value = "用户id",required = true)
                                  @RequestParam String userId,
                                  @RequestBody CenterUserBO centerUserBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response){
        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);
        UsersVO usersVO = conventUsersVO(userResult);
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);
        return IMOOCJSONResult.ok(userResult);
    }

    private Users setNullProperty(Users usersResult){
        usersResult.setPassword(null);
        usersResult.setMobile(null);
        usersResult.setMobile(null);
        usersResult.setCreatedTime(null);
        usersResult.setUpdatedTime(null);
        usersResult.setBirthday(null);
        return usersResult;
    }

}
