package com.sky.aspect;

//自定义切面，实现公共字段自动填充

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AufoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    //前置通知，在通知中进行公共字段的通知
    @Before("autoFillPointCut()")
    public void aufoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段填充");

        //获取当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill= signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        //获取到当前被拦截的方法的参数 -实体对象
        Object[] args = joinPoint.getArgs();

        //没有参数
        if(args == null || args.length == 0){
            return;
        }

        Object enity = args[0];

        LocalDateTime now = LocalDateTime.now();
        Long currrentId = BaseContext.getCurrentId();

        if(operationType == OperationType.INSERT) {
            try {
                Method setCreateTime = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(enity, now);
                setUpdateTime.invoke(enity, now);
                setCreateUser.invoke(enity, currrentId);
                setUpdateUser.invoke(enity, currrentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(operationType == OperationType.UPDATE){
            try {

                Method setUpdateTime = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = enity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射为对象属性赋值
                setUpdateTime.invoke(enity, now);
                setUpdateUser.invoke(enity, currrentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
