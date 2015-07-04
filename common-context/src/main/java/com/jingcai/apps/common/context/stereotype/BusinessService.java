package com.jingcai.apps.common.context.stereotype;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * Created by lejing on 15/7/3.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Service
@Transactional(readOnly = true)
public @interface BusinessService {
}
