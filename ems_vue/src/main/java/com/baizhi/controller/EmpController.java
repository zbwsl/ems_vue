package com.baizhi.controller;

import com.baizhi.entity.Emp;
import com.baizhi.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.jsf.FacesContextUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("emp")
@CrossOrigin
@Slf4j
public class EmpController {
    @Autowired
    private EmpService empService;

    @Value("${photo.dir}")
    private  String realPath;

    //查询一个
    @GetMapping("findOne")
    public Emp findOne(String id){
        return empService.findOne(id);
    }
    //删除
    @GetMapping("delete")
    public  Map<String,Object> delete(String id){
        log.info("删除id:[{}]",id);
        Map<String,Object>map=new HashMap<>();
        try {
            //图像
            Emp emp=empService.findOne(id);
            File file=new File(realPath,emp.getPath());
            if(file.exists())file.delete();//删除头像
            //员工信息
            empService.delete(id);
            map.put("state",true);
            map.put("msg","删除成功");
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","删除失败");
        }
        return  map;

    }
    //获取员工列表
    @GetMapping("findAll")
    public List<Emp> fiindAll(){
        return empService.findAll();
    }
    //插入员工
    @PostMapping("save")
    public Map<String,Object> save(Emp emp, MultipartFile photo) throws IOException {
        log.info("员工信息：[{}]",emp.toString());
        log.info("图片信息:[{}]",photo.getOriginalFilename());
        Map<String,Object>map = new HashMap<>();

        try {
            //头像保存
            String newFileName= UUID.randomUUID().toString()+"."+ FilenameUtils.getExtension(photo.getOriginalFilename());
            photo.transferTo(new File(realPath,newFileName));
            //设置头像地址
            emp.setPath(newFileName);
            //保存员工信息
            empService.save(emp);
            map.put("state",true);
            map.put("msg","员工信息保存成功");
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","员工信息保存失败");
        }
        return  map;
    }
    //修改员工
    @PostMapping("update")
    public Map<String,Object> update(Emp emp, MultipartFile photo) throws IOException {
        log.info("员工信息：[{}]",emp.toString());
        Map<String,Object>map = new HashMap<>();
        try {
            if(photo!=null&&photo.getSize()!=0){
                log.info("图片信息:[{}]",photo.getOriginalFilename());
                //头像保存
                String newFileName= UUID.randomUUID().toString()+"."+ FilenameUtils.getExtension(photo.getOriginalFilename());
                photo.transferTo(new File(realPath,newFileName));
                //设置头像地址
                emp.setPath(newFileName);
            }

            //保存员工信息
            empService.update(emp);
            map.put("state",true);
            map.put("msg","员工信息保存成功");
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","员工信息保存失败");
        }
        return  map;
    }
}
