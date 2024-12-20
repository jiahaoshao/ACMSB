package net.fangyi.acmsb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);


    private TokenInterceptor tokenInterceptor;
    //构造方法
    public WebMvcConfig(TokenInterceptor tokenInterceptor){
        this.tokenInterceptor = tokenInterceptor;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer){
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3)));
        configurer.setDefaultTimeout(30000);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        //排除拦截，除了注册登录(此时还没token)，其他都拦截
        excludePath.add("/sign/**");
        excludePath.add("/static/**");
        excludePath.add("/static/images/avatar/**");
        excludePath.add("/static/images/article_image/**");
        excludePath.add("/articles/getarticles/**");
        excludePath.add("/user/finduserbyuid");
        excludePath.add("/articles/getarticlebyaid");
        excludePath.add("/articles/getcommentbyparentid");
        excludePath.add("/swagger-ui/**");
        excludePath.add("/swagger-ui/index,html");

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 设置上传的文件静态资源映射，application 里的 mvc 里也要设置下静态目录
        String resourcesPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\avatar\\";
        String filepath = "file:" + resourcesPath;
        logger.info("avatar file path: {}", filepath);
        registry.addResourceHandler("static/images/avatar/**")
                .addResourceLocations(filepath);

        resourcesPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\article_image\\";
        filepath = "file:" + resourcesPath;
        logger.info("article image file path: {}", filepath);
        registry.addResourceHandler("static/images/article_image/**")
                .addResourceLocations(filepath);
    }
}
