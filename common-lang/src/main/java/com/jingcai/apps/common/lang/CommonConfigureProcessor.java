package com.jingcai.apps.common.lang;

import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.patterns.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * 设置通用配置<br/>
 * <p/>
 * 使用方法：<br/>
 * <pre>
 *  <bean class="cn.javass.common.spring.CommonConfigureProcessor">
 * <property name="config">
 * <map>
 * <!--  aspectj表达式 选择所有Action结尾的Bean 注入scope数据 为 prototype  -->
 * <entry key="cn.javass..*Action">
 * <props>
 * <prop key="scope">prototype</prop>
 * </props>
 * </entry>
 * <!-- aspectj表达式 选择所有的HibernateDaoSupport实现Bean 注入sessionFactory -->
 * <entry key="org.springframework.orm.hibernate3.support.HibernateDaoSupport+">
 * <props>
 * <prop key="property-ref">sessionFactory=sessionFactory</prop>
 * </props>
 * </entry>
 * </map>
 * </property>
 * </bean>
 * </pre>
 * <p/>
 * 目前支持三种配置：
 * scope:注入作用域
 * property-ref:注入Bean引用 如之上的sessionFactory
 * propertyName=beanName
 * property-value:注入常量值
 * propertyName=常量
 *
 * @author Zhangkaitao
 * @version 1.0
 */
public class CommonConfigureProcessor implements BeanFactoryPostProcessor {

	private Logger log = LoggerFactory.getLogger(CommonConfigureProcessor.class);

	private Map<String, Properties> config = new HashMap<String, Properties>();

	public void setConfig(Map<String, Properties> config) {
		this.config = config;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
		log.debug("apply common config start");
		for (Entry<String, Properties> entry : config.entrySet()) {
			String aspectjPattern = entry.getKey();
			Properties props = entry.getValue();

			List<BeanDefinition> bdList = findBeanDefinition(aspectjPattern, factory);

			apply(bdList, props);
		}
		log.debug("apply common config end");
	}


	private void apply(List<BeanDefinition> bdList, Properties props) {
		for (Entry<Object, Object> entry : props.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			switch (SupportedConfig.keyToEnum(key)) {
				case scope:
					applyScope(bdList, value);
					break;
				case propertyRef:
					applyPropertyRef(bdList, value);
					break;
				case propertyValue:
					applyPropertyValue(bdList, value);
					break;
				default:
					throw new IllegalArgumentException(String.format("错误的配置：[%s]", key));
			}


		}
	}


	private void applyPropertyValue(List<BeanDefinition> bdList, String value) {
		for (BeanDefinition bd : bdList) {

			String propertyName = value.split("=")[0];
			String propertyValue = value.substring(propertyName.length() + 1);
			bd.getPropertyValues().add(propertyName, propertyValue);

			log.debug("apply property value {} to {}", value, bd.getBeanClassName());
		}
	}

	private void applyPropertyRef(List<BeanDefinition> bdList, String value) {
		for (BeanDefinition bd : bdList) {

			String propertyName = value.split("=")[0];
			String propertyValue = value.substring(propertyName.length() + 1);
			bd.getPropertyValues().addPropertyValue(propertyName, new RuntimeBeanReference(propertyValue));

			log.debug("apply property ref {} to {}", value, bd.getBeanClassName());
		}
	}

	private void applyScope(List<BeanDefinition> bdList, String value) {
		for (BeanDefinition bd : bdList) {
			bd.setScope(value);
			log.debug("apply scope {} to {}", value, bd.getBeanClassName());
		}
	}

	private List<BeanDefinition> findBeanDefinition(String aspectjPattern, ConfigurableListableBeanFactory factory) {
		List<BeanDefinition> bdList = new ArrayList<BeanDefinition>();

		for (String beanName : factory.getBeanDefinitionNames()) {
			BeanDefinition bd = factory.getBeanDefinition(beanName);

			if (matches(aspectjPattern, bd.getBeanClassName())) {
				bdList.add(bd);
			}

		}

		return bdList;
	}


	private boolean matches(String aspectjPattern, String beanClassName) {
		if (!StringUtils.hasLength(beanClassName)) {
			return false;
		}
		return new AspectJTypeMatcher(aspectjPattern).matches(beanClassName);
	}

	//支持的操作
	private enum SupportedConfig {
		scope("scope"),
		propertyRef("property-ref"),
		propertyValue("property-value"),

		error("error"); //出错的情况

		private final String key;

		SupportedConfig(String key) {
			this.key = key;
		}

		public static SupportedConfig keyToEnum(String key) {
			if (key == null) {
				return error;
			}
			for (SupportedConfig config : SupportedConfig.values()) {
				if (config.key.equals(key.trim())) {
					return config;
				}
			}
			return error;
		}

	}


	public interface TypeMatcher {
		boolean matches(String className);
	}

	static class AspectJTypeMatcher implements TypeMatcher {
		private final World world;
		private final TypePattern typePattern;

		public AspectJTypeMatcher(String pattern) {
			this.world = new BcelWorld(Thread.currentThread().getContextClassLoader(), IMessageHandler.THROW, null);
			this.world.setBehaveInJava5Way(true);
			PatternParser patternParser = new PatternParser(pattern);
			TypePattern typePattern = patternParser.parseTypePattern();
			typePattern.resolve(this.world);
			IScope scope = new SimpleScope(this.world, new FormalBinding[0]);
			this.typePattern = typePattern.resolveBindings(scope, Bindings.NONE, false, false);
		}

		public boolean matches(String className) {
			ResolvedType resolvedType = this.world.resolve(className);
			return this.typePattern.matchesStatically(resolvedType);
		}
	}

	public static void main(String[] args) {
		System.out.println(new AspectJTypeMatcher("cn.javass..*Action").matches("cn.javass.test.web.action.AbcAction"));
		System.out.println(new AspectJTypeMatcher("org.springframework.beans.factory.config.BeanFactoryPostProcessor+").matches("com.jingcai.apps.common.lang.CommonConfigureProcessor"));
	}
}  