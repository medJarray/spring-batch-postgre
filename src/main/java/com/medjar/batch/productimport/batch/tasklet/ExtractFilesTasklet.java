package com.medjar.batch.productimport.batch.tasklet;

import com.medjar.batch.productimport.properties.BatchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.medjar.batch.productimport.constant.BatchImportConstant.*;
import static com.medjar.batch.productimport.util.FileUtils.extractTargzFile;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 09, 2022.
 */
@Slf4j
@RequiredArgsConstructor
public class ExtractFilesTasklet implements Tasklet {

    private final BatchProperties batchProperties;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        BatchProperties.ImportFiles importFiles = batchProperties.getFiles();
        String file = chunkContext.getStepContext().getJobParameters().get(JOB_PARAM_FILE).toString();
        Resource resource = new ClassPathResource(file);

        String tarFileName = resource.getFilename();
        if (!StringUtils.hasText(tarFileName)) {
            throw new UnexpectedJobExecutionException("The name of the tar file is missing.");
        }

        Pattern pattern = Pattern.compile(batchProperties.getFiles().getTarFileNamePattern(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tarFileName);
        log.info("batchProperties.getFiles().getTarFileNamePattern() : {}", batchProperties.getFiles().getTarFileNamePattern());
        log.info("tarFileName : {}", tarFileName);
        if (!matcher.matches()) {
            throw new UnexpectedJobExecutionException(String.format("The name of the tar %s is malformed.", tarFileName));
        }
        String zipPrefix = matcher.group(BatchProperties.ImportFiles.ZIP_FILENAME_PREFIX_GROUP);
        List<File> unzippedFiles;
        try (InputStream fileInputStream = new FileInputStream("/" + ((ClassPathResource) resource).getPath())) {
            unzippedFiles = extractTargzFile(fileInputStream);
        }

        if (CollectionUtils.isEmpty(unzippedFiles)) {
            throw new UnexpectedJobExecutionException(String.format("The tar file %s don't contains any files or it's corrupted", resource.getInputStream()));
        }

        log.info("{} files are unzipped from the tar file {}", unzippedFiles.size(), tarFileName);

        Map<String, File> jobContextParameters = new HashMap<>();
        unzippedFiles.forEach(unzippedFile -> {
            String parameter = setUpCorrespondFileJobParam(unzippedFile, zipPrefix, importFiles);
            if (StringUtils.hasText(parameter)) {
                jobContextParameters.put(parameter, unzippedFile);
            }
        });

        // TODO check for mandatory files
        /*if (!isMandatoryFilesExists(jobContextParameters)) {
         throw new UnexpectedJobExecutionException("Some mandatory files doesn't exists on the tar file");
         }*/

        ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

        jobContextParameters.forEach(executionContext::put);

        return RepeatStatus.FINISHED;
    }

    private String setUpCorrespondFileJobParam(File unzippedFile, String zipPrefix, BatchProperties.ImportFiles importFiles) {
        String fileName = unzippedFile.getName().toLowerCase();
        String jobParameter = null;
        if (fileName.startsWith(importFiles.getSaleFilePrefix())) {
            jobParameter = JOB_PARAM_ORDER_TEMP_FILE;
        } else if (fileName.startsWith(importFiles.getOrderFilePrefix())) {
            jobParameter = JOB_PARAM_ORDER_TEMP_FILE;
        } else if (fileName.startsWith(importFiles.getProductFilePrefix())) {
            jobParameter = JOB_PARAM_PRODUCT_TEMP_FILE;
        }
        return jobParameter;
    }
}
