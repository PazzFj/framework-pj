package com.pazz.framework.samples.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.ICache;
import com.pazz.framework.samples.cache.AppVersionCache;
import com.pazz.framework.samples.cache.AppVersionCacheProvider;
import com.pazz.framework.samples.entity.UserEntity;
import com.pazz.framework.samples.service.IUserService;
import com.pazz.framework.web.controller.AbstractWebController;
import com.pazz.framework.web.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:02
 * @description:
 */
@RestController()
@RequestMapping("/common/user")
public class UserController extends AbstractWebController {

    @Autowired
    private IUserService userService;

    @RequestMapping("/get")
    @ResponseBody
    public UserEntity getUser() {
        UserEntity u = new UserEntity();
        u.setName("测试");
        //分页
        Page<UserEntity> page = PageHelper.startPage(1, 10).doSelectPage(() -> userService.select(u));
        //总数
        long total = page.getTotal();
        System.out.println(page.getTotal());
        return userService.getUser("1");
    }

    @RequestMapping("/version")
    @ResponseBody
    public Response getAppversion() {
        ICache cache = CacheManager.getInstance().getCache(AppVersionCache.UUID);
        return returnSuccess(cache.get(AppVersionCacheProvider.APP_VERSION_KEY));
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Response addUser(@RequestBody @Valid UserEntity userEntity, BindingResult result) {
        return returnSuccess(null);
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

}
