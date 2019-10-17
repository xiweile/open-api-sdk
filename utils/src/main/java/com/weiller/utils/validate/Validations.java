package com.weiller.utils.validate;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * ValidationUtil	@version 1.0
 */
public class Validations {
    
	/**
     * 开启快速结束模式 failFast (true)
     */
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();
    
    /**
     * 功能描述: <br>
     * 〈注解验证参数〉
     *
     * @param object
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static void validate(Object object) {
    	if(object==null) {
    		return;
    	}
    	Set<ConstraintViolation<Object>> errors = validator.validate(object);
        // 抛出检验异常
    	if (errors.isEmpty()) return;
        throw new ValidationException(errors.iterator().next().getPropertyPath().iterator().next().getName(),errors.iterator().next().getMessage());
    }
}