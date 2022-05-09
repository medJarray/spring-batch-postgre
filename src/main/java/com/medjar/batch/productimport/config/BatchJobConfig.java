package com.medjar.batch.productimport.config;

import com.medjar.batch.productimport.batch.reader.OrderReader;
import com.medjar.batch.productimport.batch.tasklet.ExtractFilesTasklet;
import com.medjar.batch.productimport.batch.writer.OrderWriter;
import com.medjar.batch.productimport.properties.BatchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.medjar.batch.productimport.constant.BatchImportConstant.*;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 05, 2022.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableBatchProcessing
public class BatchJobConfig {


    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationArguments args;
    private final BatchProperties batchProperties;

    @Bean
    public TaskExecutor stepTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(batchProperties.getStep().getMaxPoolSize());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return taskExecutor;
    }

    @Bean
    public Tasklet extractFileTsklet() {
        return new ExtractFilesTasklet(batchProperties);
    }

    @Bean
    public Flow saveOrdersFlow(OrderReader orderReader,
                               OrderWriter orderWriter,
                               @Qualifier("stepTaskExecutor") TaskExecutor stepTaskExecutor) {

        return saveFileFlow(SAVE_ORDERS_FLOW_PREFIX, true, JOB_PARAM_ORDER_TEMP_FILE, orderReader, orderWriter, stepTaskExecutor);
    }

    @Bean
    public Flow saveFilesFlow(@Qualifier("saveOrdersFlow") Flow saveOrdersFlow) {
        return new FlowBuilder<Flow>("saveFileFlow")
                .start(saveOrdersFlow)
                .build();
    }

    @Bean
    public Flow extractFlow(Tasklet extractFilesTasklet) {
        return new FlowBuilder<Flow>("extractFlow")
                .start(stepBuilderFactory.get("extractSdeFilesStep")
                        .tasklet(extractFilesTasklet)
                        .build())
                .build();
    }

    private <T> Flow saveFileFlow(String flowPrefixName,
                                  boolean mandatory,
                                  String jobParameter,
                                  ItemReader<T> itemReader,
                                  ItemWriter<T> itemWriter,
                                  TaskExecutor taskExecutor) {

        Step step = stepBuilderFactory.get(String.format(flowPrefixName, "Step"))
                .<T, T>chunk(batchProperties.getStep().getChunkSize())
                .reader(itemReader)
                // .processor() // todo add processor step
                .faultTolerant()
                .skip(FlatFileParseException.class) // todo replace with skipPolicy
                .writer(itemWriter)
                .taskExecutor(taskExecutor)
                .build();

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>(String.format(flowPrefixName, "Flow"));
        if (!mandatory) {
            JobExecutionDecider decider = (jobExecution, stepExecution) ->
                    jobExecution.getExecutionContext().containsKey(jobParameter) ?
                            new FlowExecutionStatus(DECIDER_CONTINUE_STATUS) :
                            new FlowExecutionStatus(DECIDER_SKIP_STATUS);

            flowBuilder.start(decider)
                    .on(DECIDER_CONTINUE_STATUS).to(step)
                    .on(DECIDER_SKIP_STATUS).end();
        } else {
            flowBuilder.start(step);
        }
        return flowBuilder.build();
    }

    @Bean
    public Job batchImportJob(@Qualifier("extractFlow") Flow extractFlow,
                              @Qualifier("saveFilesFlow") Flow saveFilesFlow) {
        return jobBuilderFactory.get("batchImportJob")
                .incrementer(new RunIdIncrementer())
                .start(extractFlow)
                .next(saveFilesFlow)
                .end()
                .build();
    }
}

