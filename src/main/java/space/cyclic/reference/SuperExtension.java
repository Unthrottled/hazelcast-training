package space.cyclic.reference;

import org.apache.log4j.Logger;
import space.cyclic.reference.interfaces.SuperBean;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


public class SuperExtension implements Extension {
    private static Logger logger = Logger.getLogger(SuperExtension.class);
    private List<Bean<?>> superBeanList = new ArrayList<>();

    public <T> void collect(@Observes ProcessBean<T> event) {
        Bean<?> bean = event.getBean();
        if (event.getAnnotated().isAnnotationPresent(SuperBean.class)
                && event.getAnnotated().isAnnotationPresent(Singleton.class)) {
            superBeanList.add(bean);
            logger.warn("Super Bean collect : " + bean.getBeanClass().getCanonicalName());
        }
    }

    public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        for (Bean<?> bean : superBeanList) {
            logger.warn("First call to : " + bean.getClass().getCanonicalName()
                    + "Super Bean says : " + beanManager
                    .getReference(bean,
                            bean.getBeanClass(),
                            beanManager.createCreationalContext(bean)).toString());
        }
    }
}
