/*
 * Copyright 2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.spring.billRun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.billRun.model.Bill;
import io.spring.billRun.model.Usage;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@EnableTask
public class BillingConfiguration {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	public BillingConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}

	@Autowired
	ObjectMapper objectMapper;

	@Value("${usage.file.name:classpath:usageinfo.json}")
	private Resource usageResource;

	@Bean
	public Job job1(ItemReader<Usage> reader, ItemProcessor<Usage, Bill> itemProcessor, ItemWriter<Bill> writer) {
		Step step = new StepBuilder("BillProcessing", this.jobRepository)
				.<Usage, Bill>chunk(1, this.transactionManager)
				.reader(reader)
				.processor(itemProcessor)
				.writer(writer)
				.build();

		return new JobBuilder("BillJob", this.jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}

	@Bean
	public JsonItemReader<Usage> jsonItemReader() {
		JacksonJsonObjectReader<Usage> jsonObjectReader =
				new JacksonJsonObjectReader<>(Usage.class);
		jsonObjectReader.setMapper(objectMapper);

		return new JsonItemReaderBuilder<Usage>()
				.jsonObjectReader(new JacksonJsonObjectReader<>(Usage.class))
				.resource(this.usageResource)
				.name("UsageJsonItemReader")
				.build();
	}

	@Bean
	public JdbcBatchItemWriter<Bill> jdbcBillWriter(DataSource dataSource) {
		JdbcBatchItemWriter<Bill> writer = new JdbcBatchItemWriterBuilder<Bill>()
						.beanMapped()
				.dataSource(dataSource)
				.sql("INSERT INTO BILL_STATEMENTS (id, first_name, last_name, minutes, data_usage,bill_amount) VALUES (:id, :firstName, :lastName, :minutes, :dataUsage, :billAmount)")
				.build();
		return writer;
	}

	@Bean
	ItemProcessor<Usage, Bill> billProcessor() {
		return new BillProcessor();
	}

}
