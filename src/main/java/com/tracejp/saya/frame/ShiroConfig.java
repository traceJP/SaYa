package com.tracejp.saya.frame;

import com.tracejp.saya.frame.shiro.*;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.*;

/**
 * @author traceJP
 * @date 2021/4/7 22:14
 */
@Configuration
public class ShiroConfig {

    /**
     * shiro核心
     */
    @Bean
    public DefaultWebSecurityManager securityManager(TokenRealm tokenRealm, PasswordRealm passwordRealm, SmsRealm smsRealm) {

        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();

        // 自定义多realm认证器，添加自定义realm
        MultiRealmAuthenticator multiRealmAuthenticator = new MultiRealmAuthenticator();
        List<Realm> realms = new LinkedList<>();
        realms.add(tokenRealm);
        realms.add(smsRealm);
        realms.add(passwordRealm);
        multiRealmAuthenticator.setRealms(realms);
        manager.setAuthenticator(multiRealmAuthenticator);

        // 禁用shiro自带session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        manager.setSubjectDAO(subjectDAO);

        return manager;
    }

    /**
     * shiro自定义过滤器
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        // 添加自定义过滤器
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
        factoryBean.setFilters(filterMap);

        // 过滤器规则
        Map<String, String> filterRuleMap = new LinkedHashMap<>();
        // 接口规则设置
        filterRuleMap.put("/unauthorized/**", "anon");
        filterRuleMap.put("/login/**", "anon");    // 登录接口开放
        filterRuleMap.put("/tbTemplate/getToken", "anon");    // 测试接口

        // 所有请求都需要通过jwt拦截器
        filterRuleMap.put("/**", "jwt");

        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    /**
     * 强制使用cglib代理
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * spring生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
