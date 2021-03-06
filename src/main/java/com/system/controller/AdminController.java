package com.system.controller;

import com.system.exception.CustomException;
import com.system.mapper.SysuserMapper;
import com.system.po.*;
import com.system.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * Created by Jacey on 2017/6/30.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource(name = "studentServiceImpl")
    private StudentService studentService;

    @Resource(name = "courseServiceImpl")
    private CourseService courseService;

    @Resource(name = "collegeServiceImpl")
    private CollegeService collegeService;

    @Resource(name = "userloginServiceImpl")
    private UserloginService userloginService;

    @Resource
    private SysuserService sysuserService;

    @Resource
    private CourseScService courseScService;

    @Resource
    private CourseClassService courseClassService;

    @Autowired
    private SysuserMapper sysuserMapper;

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<学生操作>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    //  学生信息显示
    @RequestMapping("/showStudent")
    public String showStudent(Model model, Integer page) throws Exception {

        List<Sysuser> list = null;
        //页码对象
        PagingVO pagingVO = new PagingVO();
        //设置总页数
        pagingVO.setTotalCount(sysuserService.getCount());
        if (page == null || page == 0) {
            pagingVO.setToPageNo(1);
            //当前页码
                list = sysuserService.findByPaging(1);
        } else {
            pagingVO.setToPageNo(page);
            list = sysuserService.findByPaging(page);
        }

        model.addAttribute("studentList", list);
        model.addAttribute("pagingVO", pagingVO);

        return "admin/showStudent";

    }

    //  添加学生信息页面显示
    @RequestMapping(value = "/addStudent", method = {RequestMethod.GET})
    public String addStudentUI(Model model) throws Exception {

        return "admin/addStudent";
    }

     // 添加学生信息操作
    @RequestMapping(value = "/addStudent", method = {RequestMethod.POST})
    public String addStudent(Sysuser sysuser, Model model) throws Exception {

        Boolean result = sysuserService.save(sysuser);

        if (!result) {
            model.addAttribute("message", "学号重复");
            return "error";
        }
        //添加成功后，也添加到登录表
        Userlogin userlogin = new Userlogin();
        userlogin.setUsername(sysuser.getUsername());
        userlogin.setPassword("123");
        userlogin.setRole(2);
        userloginService.save(userlogin);

        //重定向
        return "redirect:/admin/showStudent";
    }

    // 修改学生信息页面显示
    @RequestMapping(value = "/editStudent", method = {RequestMethod.GET})
    public String editStudentUI(Integer id, Model model) throws Exception {
        if (id == null) {
            //加入没有带学生id就进来的话就返回学生显示页面
            return "redirect:/admin/showStudent";
        }
        Sysuser studentCustom = sysuserService.selectByPrimaryKey(id);
        if (studentCustom == null) {
            throw new CustomException("未找到该名学生");
        }
        model.addAttribute("student", studentCustom);


        return "admin/editStudent";
    }

    // 修改学生信息处理
    @RequestMapping(value = "/editStudent", method = {RequestMethod.POST})
    public String editStudent(Sysuser sysuser) throws Exception {

        sysuserService.updateByPrimaryKey(sysuser);

        //重定向
        return "redirect:/admin/showStudent";
    }

    // 删除学生
    @RequestMapping(value = "/removeStudent", method = {RequestMethod.GET} )
    private String removeStudent(Integer id) throws Exception {
        if (id == null) {
            //加入没有带学生id就进来的话就返回学生显示页面
            return "admin/showStudent";
        }
        Sysuser user = sysuserService.selectByPrimaryKey(id);
        sysuserService.deleteByPrimaryKey(id);
        userloginService.removeByName(user.getUsername());

        return "redirect:/admin/showStudent";
    }

    // 搜索学生
    @RequestMapping(value = "selectStudent", method = {RequestMethod.POST})
    private String selectStudent(String findByName, Model model) {

        List<Sysuser> list = sysuserService.findByName(findByName);

        model.addAttribute("studentList", list);
        return "admin/showStudent";
    }

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<教师操作>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    // 班级管理
    @RequestMapping("/showClass")
    public String showTeacher(Model model, Integer page) throws Exception {

        List<ClassCourse> list = null;
        //页码对象
        PagingVO pagingVO = new PagingVO();
        //设置总页数
        pagingVO.setTotalCount(sysuserService.getClassCount());
        if (page == null || page == 0) {
            pagingVO.setToPageNo(1);
            list = sysuserService.findCSByPaging(1);
        } else {
            pagingVO.setToPageNo(page);
            list = sysuserService.findCSByPaging(page);
        }

        model.addAttribute("teacherList", list);
        model.addAttribute("pagingVO", pagingVO);

        return "admin/showTeacher";

    }

    // 排课
    @RequestMapping(value = "/scheduleCourse", method = {RequestMethod.GET})
    public String addTeacherUI(Model model,String classname) throws Exception {

        //查找改班级下的课程数，如果超过四条将可以排课
        Boolean flag = courseScService.scheduleCourse(classname);
        if(flag){
            return "redirect:/admin/showClass";
        }else{
            model.addAttribute("message", "该班级下课程少于4门或者大于20门，无法排课");
            return "error";
        }

    }

    // 查看班级下的课程
    @RequestMapping(value = "/editTeacher", method = {RequestMethod.GET})
    public String editTeacherUI(String classname, Model model) throws Exception {
        if(null==classname ||"".equals(classname)){
            Subject subject = SecurityUtils.getSubject();
            String username = (String) subject.getPrincipal();
            List<Sysuser> sysusers = sysuserMapper.selectByUsername(username);
            classname = sysusers.get(0).getClassname();
        }
        List<CourseWeek> courseWeeks = courseClassService.selectByClassname(classname);

        model.addAttribute("weeks", courseWeeks);
        model.addAttribute("classname", classname);

        return "admin/showWeeks";
    }

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<课程操作>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    // 课程信息显示
    @RequestMapping("/showCourse")
    public String showCourse(Model model, Integer page) throws Exception {

        List<CourseSc> list = null;
        //页码对象
        PagingVO pagingVO = new PagingVO();
        //设置总页数
        pagingVO.setTotalCount(courseService.getCountCouse());
        if (page == null || page == 0) {
            pagingVO.setToPageNo(1);
            list = courseScService.findByPaging(1);
        } else {
            pagingVO.setToPageNo(page);
            list = courseScService.findByPaging(page);
        }

        model.addAttribute("courseList", list);
        model.addAttribute("pagingVO", pagingVO);

        return "admin/showCourse";

    }

    //添加课程
    @RequestMapping(value = "/addCourse", method = {RequestMethod.GET})
    public String addCourseUI(Model model) throws Exception {


        return "admin/addCourse";
    }

    // 添加课程信息处理
    @RequestMapping(value = "/addCourse", method = {RequestMethod.POST})
    public String addCourse(CourseSc courseSc, Model model) throws Exception {

        Boolean result = courseScService.save(courseSc);

        if (!result) {
            model.addAttribute("message", "改班级的这个课程以及存在");
            return "error";
        }


        //重定向
        return "redirect:/admin/showCourse";
    }

    // 修改课程信息页面显示
    @RequestMapping(value = "/editCourse", method = {RequestMethod.GET})
    public String editCourseUI(Integer id, Model model) throws Exception {
        if (id == null) {
            return "redirect:/admin/showCourse";
        }
        CourseSc courseSc = courseScService.selectByPrimaryKey(id);
        if (courseSc == null) {
            throw new CustomException("未找到该课程");
        }
        model.addAttribute("course", courseSc);
        return "admin/editCourse";
    }

    // 修改课程信息页面处理
    @RequestMapping(value = "/editCourse", method = {RequestMethod.POST})
    public String editCourse(CourseSc courseSc) throws Exception {

        courseScService.updateByPrimaryKey(courseSc);

        //重定向
        return "redirect:/admin/showCourse";
    }

    // 删除课程信息
    @RequestMapping("/removeCourse")
    public String removeCourse(Integer id) throws Exception {
        if (id == null) {
            //加入没有带教师id就进来的话就返回教师显示页面
            return "admin/showCourse";
        }
        courseScService.deleteByPrimaryKey(id);

        return "redirect:/admin/showCourse";
    }

    //搜索课程
    @RequestMapping(value = "selectCourse", method = {RequestMethod.POST})
    private String selectCourse(String findByName, Model model) throws Exception {

        List<CourseSc> list = courseScService.findByName(findByName);

        model.addAttribute("courseList", list);
        return "admin/showCourse";
    }

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<其他操作>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    // 普通用户账号密码重置
    @RequestMapping("/userPasswordRest")
    public String userPasswordRestUI() throws Exception {
        return "admin/userPasswordRest";
    }

    // 普通用户账号密码重置处理
    @RequestMapping(value = "/userPasswordRest", method = {RequestMethod.POST})
    public String userPasswordRest(Userlogin userlogin) throws Exception {

        Userlogin u = userloginService.findByName(userlogin.getUsername());

        if (u != null) {
            if (u.getRole() == 0) {
                throw new CustomException("该账户为管理员账户，没法修改");
            }
            u.setPassword(userlogin.getPassword());
            userloginService.updateByName(userlogin.getUsername(), u);
        } else {
            throw new CustomException("没找到该用户");
        }

        return "admin/userPasswordRest";
    }

    // 本账户密码重置
    @RequestMapping("/passwordRest")
    public String passwordRestUI() throws Exception {
        return "admin/passwordRest";
    }


}
