package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StopWatch;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.lang.String.*;
import static java.util.stream.Collectors.*;

/**
 * Created by shaun on 27/02/16.
 */
@SpringBootApplication
@EnableAsync
public class Application implements CommandLineRunner {

    StopWatch stopWatch = new StopWatch();

    @Autowired
    GitHubLookupService gitHubLookupService;

    @Override
    public void run(String... args) throws Exception {


        stopWatch.start("Run method");

        Set<User> users = new HashSet<>();

        Future<User> page1 = gitHubLookupService.findUser("shaunlee0");
        Future<User> page2 = gitHubLookupService.findUser("CloudFoundry");
        Future<User> page3 = gitHubLookupService.findUser("Spring-Projects");

        //Wait until they are done!
        while (!(page1.isDone() && page2.isDone() && page3.isDone())) {
            Thread.sleep(10);//pause ten milliseconds
            users.add(page1.get());
            users.add(page2.get());
            users.add(page3.get());
        }


        stopWatch.stop();

        Set<User> shaun;

        shaun = users.
                stream().
                filter(user -> user.getName().equals("Shaun Lee"))
                .collect(toSet());

        shaun.stream().
                forEach(user -> System.out.println(format("Shaun's array consists of : \nUser name = %s, User blog = %s", user.getName(), user.getBlog())));



        users.stream().
                forEach(user -> System.out.println(format("User array consists of : \nUser name = %s, User blog = %s", user.getName(), user.getBlog())));

        System.out.println("Elapsed time = " + stopWatch.getLastTaskInfo().getTimeSeconds() + "s");

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
