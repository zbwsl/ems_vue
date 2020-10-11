package com.baizhi.controller;


import com.baizhi.entity.User;
import com.baizhi.service.UserService;
import com.baizhi.utils.VerifyCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin//允许跨域
@RequestMapping("user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;


    /**
     *
     * 用户登录
     */
    @PostMapping("login")
    public Map<String,Object> login(@RequestBody User user){
        log.info("当前用户信息:[{}]",user.toString());
        Map<String,Object> map = new HashMap<>();
        try {
            User userDB = userService.login(user);
            map.put("state",true);
            map.put("msg","提示:登录成功");
            map.put("user",userDB);
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg",e.getMessage());
        }
        return map;
    }
    /**
     *
     * 用来处理用户注册
     */
    @PostMapping("register")
    public Map<String,Object> register(@RequestBody User user , String code , HttpServletRequest request){
        log.info("用户信息：[{}]",user.toString());
        log.info("验证码：[{}]",code);
        Map<String,Object>map=new HashMap<>();
        //1.调用业务方法
        try{
            String key= (String) request.getServletContext().getAttribute("code");
            if (key.equalsIgnoreCase(code)) {
                log.info("用户信息：[{}]",user.toString());
                userService.register(user);
                map.put("state",true);
                map.put("msg","提示:注册成功");
            }
            else {
                throw  new RuntimeException("验证码错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","提示:"+e.getMessage());
        }
        return map;
    }


    /**
     * 生成验证码
     */

    @GetMapping("getImage")
    public String getImageCode(HttpServletRequest request) throws IOException {
        //使用工具类生成验证码
        VerifyCode vc = new VerifyCode();
        //转换为base64
        String base64 = null;
        try {
            BufferedImage image = vc.getImage();
            Integer width = image.getWidth();
            Integer height = image.getHeight();
            System.out.println("宽：" + width + " 高:"+height);

            //输出流
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            base64 = "data:image/png;base64,"+Base64Utils.encodeToString(stream.toByteArray());
            System.out.println(base64);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String code=vc.getText();
        log.info("验证码：[{}]",code);
        //放入servlet作用域
        request.getServletContext().setAttribute("code",code);
        return base64;
    }
}
