package com.medjar.batch.productimport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.batch.JobExecutionExitCodeGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.medjar.batch.productimport.constant.BatchImportConstant.JOB_PARAM_FILE;
import static com.medjar.batch.productimport.constant.BatchImportExceptionConstant.*;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 05, 2022.
 */

@Slf4j
@Profile("!it")
@Component
@RequiredArgsConstructor
public class BatchRunner implements ApplicationRunner {

    private final Job job;
    private final JobLauncher JobLauncher;
    private final ApplicationContext applicationContext;
    private final JobExecutionExitCodeGenerator jobExecutionExitCodeGenerator;


    @Override
    public void run(ApplicationArguments args) {
        int exitStatus = 0;
        try {
            String file = getMandatoryParamValue(args, JOB_PARAM_FILE);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString(JOB_PARAM_FILE, file)
                    .toJobParameters();

            JobExecution run = JobLauncher.run(job, jobParameters);
            if (!ExitStatus.COMPLETED.equals(run.getExitStatus())) {
                log.error(BATCH_EXECUTION_FAILED, run.getExitStatus(), run);
                exitStatus = 1;
            }
        } catch (Exception e) {
            log.error(CANNOT_LUNCH_BATCH, e);
            exitStatus = 1;
        } finally {
            shutdown(exitStatus);
        }
    }

    protected void shutdown(int status) {
        log.info("Exiting with status {}", status);
        if (status != 0) {
            System.exit(SpringApplication.exit(applicationContext, () -> status));
        } else {
            System.exit(SpringApplication.exit(applicationContext, jobExecutionExitCodeGenerator));
        }
    }

    private String getMandatoryParamValue(ApplicationArguments args, String key) {
        List<String> optionalValues = args.getOptionValues(key);
        if (CollectionUtils.isEmpty(optionalValues)) {
            String message = String.format(MISSING_MANDATORY_PARAM, key);
            log.error(message);
            shutdown(1);
            throw new IllegalArgumentException(message);
        }
        return optionalValues.get(0);
    }
}

