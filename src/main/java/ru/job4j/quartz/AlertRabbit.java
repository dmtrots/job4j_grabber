package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Properties config = loadProperties();
            int interval = Integer.parseInt(config.getProperty("rabbit.interval"));

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDetail job = newJob(Rabbit.class).build();

            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();

            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();

            scheduler.scheduleJob(job, trigger);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = AlertRabbit.class
                .getClassLoader()
                .getResourceAsStream("rabbit.properties")) {

            if (input == null) {
                throw new IllegalArgumentException("Файл rabbit.properties не найден");
            }
            properties.load(input);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
        }
    }
}