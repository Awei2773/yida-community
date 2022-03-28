package com.waigo.yida.community.controller;

import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.config.properties.CommunityProperties;
import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.constant.StatusCode;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.exception.ReqException;
import com.waigo.yida.community.log.annotation.LogUserOpt;
import com.waigo.yida.community.log.enums.UserOption;
import com.waigo.yida.community.service.*;
import com.waigo.yida.community.util.RandomCodeUtil;
import com.waigo.yida.community.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * author waigo
 * create 2021-10-04 19:33
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    @Qualifier("email-captcha")
    KaptchaService emailCaptchaService;
    @Autowired
    CommunityProperties communityProperties;
    @Autowired
    UserHolder userHolder;
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    FollowService followService;
    @Autowired
    LikeService likeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(HttpServletResponse response) {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload/headUrl")
    public String uploadHeader(MultipartFile headerImage, HttpServletResponse response, Model model) {
        if (headerImage == null || headerImage.isEmpty() || headerImage.getOriginalFilename() == null) {
            Status failure = Status.failure();
            failure.addAttribute("error", "文件不能为空！！！");
            model.addAttribute("status", failure);
            return "/site/setting";//失败返回
        }
        //类型和大小校验headerImage.getContentType(),输出MimeType格式
        String contentType = headerImage.getContentType();
        LOGGER.debug(contentType);
        if (!MimeTypeUtils.IMAGE_PNG_VALUE.equals(contentType) && !MimeTypeUtils.IMAGE_JPEG_VALUE.equals(contentType) && !MimeTypeUtils.IMAGE_GIF_VALUE.equals(contentType)) {
            Status failure = Status.failure();
            failure.addAttribute("error", "这不是图片！！！");
            model.addAttribute("status", failure);
            return "/site/setting";//失败返回
        }
        //1.文件名
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = RandomCodeUtil.getRandomCode(0) + suffix;
        //2.存储图片
        Status status = fileUploadService.saveSingleFile(headerImage, fileName, communityProperties.getSavePath());
        if (!status.isSuccess()) {
            model.addAttribute("status", status);
            return "/site/setting";//失败返回
        }
        //3.成功了就修改用户的headerUrl
        User user = userHolder.getUser();
        //host:port/contextPath/files/{fileName}

        userService.updateHeaderUrl(user.getId(), communityProperties.SERVER_CONTEXT_PATH + "/user/header/" + fileName);
        //成功就返回
        LOGGER.debug("用户{}，修改头像成功！！！", user.getUsername());
        return "redirect:/index";
    }

    /**
     * 获取头像图片
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/header/{fileName}")
    public void getHeaderImg(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        FileInputStream fis = null;
        OutputStream os;
        try {
            fis = new FileInputStream(communityProperties.getSavePath() + "/" + fileName);
            os = response.getOutputStream();
            byte[] buf = new byte[1024];
            //慢慢写出去
            int b;
            while ((b = fis.read(buf)) != -1) {
                os.write(buf, 0, b);
            }
            os.flush();
        } catch (IOException e) {
            LOGGER.error("读取头像失败:{}", e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error("文件输入流关闭失败：{}", e.getMessage());
                }
            }
        }
    }

    @PostMapping("/password/update")
    @LoginRequired
    public String updatePassword(String newPassword, String oldPassword, Model model, @CookieValue("ticket") String ticket) {
        if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(oldPassword)) {
            Status status = Status.failure().lineAddAttribute("oldPasswordMsg", "新密码和原密码都不能为空！！！");
            model.addAttribute("status", status);
            return "/site/setting";
        }
        if (!isPasswordLenValid(newPassword)) {
            Status status = Status.failure().lineAddAttribute("newPasswordMsg", "密码长度要在8到64之间！！！");
            model.addAttribute("status", status);
            return "/site/setting";
        }
        User user = userHolder.getUser();
        if (!SecurityUtil.encryptPassword(user.getUsername(), oldPassword, user.getSalt()).equals(user.getPassword())) {
            Status status = Status.failure().lineAddAttribute("confirmPasswordMsg", "两次输入的密码不一致！！！");
            model.addAttribute("status", status);
            return "/site/setting";
        }
        //1.原密码匹配上了，新密码合法，就修改密码吧
        String nowPassword = SecurityUtil.encryptPassword(user.getUsername(), newPassword, user.getSalt());

        //2.两步写库操作，需要进行事务控制
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            userService.changePassword(user.getId(), nowPassword);
            //修改成功需要退出登录，然后跳转到登录页面
            authService.logout(ticket);
        });

        //成功就跳转到登录页
        Status status = Status.success().lineAddAttribute("jumpText", "恭喜您，密码修改成功,请重新登录~~~").lineAddAttribute("path", "/login");
        model.addAttribute("status", status);
        return "/site/operate-result";
    }

    private boolean isPasswordLenValid(String newPassword) {
        return !(newPassword.length() < CommunityConstant.PASSWORD_MINIMUM_LENGTH || newPassword.length() > CommunityConstant.PASSWORD_MAXIMUM_LENGTH);
    }

    @GetMapping("/forget")
    public String getForgetPage() {
        return "/site/forget";
    }

    @PostMapping("/forget/password")
    @LogUserOpt(UserOption.CHANGE_PASSWORD)
    public String replacePassword(String email, String verifyCode, String newPassword, Model model, HttpSession session) {
        if (StringUtils.isBlank(email)) {
            model.addAttribute("emailMsg", "邮箱不能为空！！！");
            return "/site/forget";
        }
        if (StringUtils.isBlank(email)) {
            model.addAttribute("verifyCodeMsg", "验证码不能为空！！！");
            return "/site/forget";
        }
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newPasswordMsg", "新密码不能为空！！！");
            return "/site/forget";
        }
        if (!isPasswordLenValid(newPassword)) {
            model.addAttribute("newPasswordMsg", "密码长度要在8到64之间！！！");
            return "/site/forget";
        }
        if (!emailCaptchaService.checkKaptcha(verifyCode, session)) {
            model.addAttribute("verifyCodeMsg", "验证码错误！！！");
            return "/site/forget";
        }
        User user = userService.getUserByEmail(email);
        if (user == null) {
            model.addAttribute("emailMsg", "用户不存在！！！");
            return "/site/forget";
        }
        userService.changePassword(user.getId(), SecurityUtil.encryptPassword(user.getUsername(), newPassword, user.getSalt()));
        //成功就跳转到登录页
        Status status = Status.success().lineAddAttribute("jumpText", "恭喜您，密码修改成功,请重新登录~~~").lineAddAttribute("path", "/login");
        model.addAttribute("status", status);
        return "/site/operate-result";
    }

    /**
     * 个人主页:
     * 不登录也是能看的
     */
    @GetMapping("/profile/{userId}")
    @LoginRequired
    public String getUserProfilePage(@PathVariable("userId") int userId,Model model) {
        //1.查出这个对应的用户信息
        User user = userService.getUser(userId);
        if (user == null) {
            throw new ReqException("查无此人", StatusCode.BAD_REQUEST);
        }
        //2.查出这个用户关注的人数
        long followeesCount = followService.findUserFolloweesCountByType(userId,CommunityConstant.USER);
        //3.查出这个用户的粉丝数量
        long followersCount = followService.findEntityFollowersCount(CommunityConstant.USER,userId);
        //4.查出这个用户获得的赞数量
        long userLikedCount = likeService.findUserLikeCount(userId);
        //5.查出当前登录用户是否关注过这个页面所属的用户
        User curLoginUser = userHolder.getUser();
        //6.当前登录用户存在，并且不是目标用户，并且关注过才能说是关注过
        boolean isFollower = curLoginUser !=null&&curLoginUser.getId()!=userId&&followService.isFollower(CommunityConstant.USER,userId,curLoginUser.getId());
        model.addAttribute("followeesCount",followeesCount);
        model.addAttribute("followersCount",followersCount);
        model.addAttribute("userLikedCount",userLikedCount);
        model.addAttribute("isFollower",isFollower);
        model.addAttribute("targetUser",user);
        return "/site/profile";
    }
}
