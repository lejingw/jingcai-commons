package com.jingcai.apps.common.context.stereotype;

//import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * Created by lejing on 15/7/3.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Transactional(readOnly = true)
@org.springframework.stereotype.Component
public @interface BusinessService {
    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any
     */
    String value() default "";
}
