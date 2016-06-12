package space.cyclic.reference.beans;

import space.cyclic.reference.interfaces.SuperBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Singleton;

@SuperBean
@Singleton
public class Highlander {
    @Resource(lookup = "java:comp/env/concurrent/bestExecutor")
    ManagedExecutorService executorService;

    @PostConstruct
    public void initialize(){
        System.out.println(executorService);
    }

    @Override
    public String toString(){
        return "THERE CAN ONLY BE ONE!!";
    }
}
