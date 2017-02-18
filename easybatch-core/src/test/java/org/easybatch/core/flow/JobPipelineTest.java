/**
 * The MIT License
 *
 *   Copyright (c) 2017, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.easybatch.core.flow;

import org.easybatch.core.filter.PoisonRecordFilter;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.reader.BlockingQueueRecordReader;
import org.easybatch.core.reader.StringRecordReader;
import org.easybatch.core.record.Record;
import org.easybatch.core.writer.BlockingQueueRecordWriter;
import org.easybatch.core.writer.CollectionRecordWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easybatch.core.flow.JobPipelineBuilder.aNewJobPipelineBuilder;
import static org.easybatch.core.flow.predicate.JobCompleted.jobCompleted;
import static org.easybatch.core.flow.predicate.JobFailed.jobFailed;
import static org.easybatch.core.job.JobBuilder.aNewJob;
import static org.easybatch.core.job.JobStatus.COMPLETED;
import static org.easybatch.core.job.JobStatus.FAILED;
import static org.easybatch.core.util.Utils.LINE_SEPARATOR;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobPipelineTest {

    @Mock
    private Job job1, job2, job3;
    @Mock
    private JobReport jobReport1, jobReport2, jobReport3;

    @Test
    public void testJobPipeline() throws Exception {
        when(job1.call()).thenReturn(jobReport1);
        when(jobReport1.getStatus()).thenReturn(COMPLETED);
        when(job2.call()).thenReturn(jobReport2);
        when(jobReport2.getStatus()).thenReturn(FAILED);
        when(job3.call()).thenReturn(jobReport3);

        JobPipeline jobPipeline = aNewJobPipelineBuilder()
                .startWith(job1)
                .when(jobCompleted()).then(job2)
                .when(jobFailed()).then(job3)
                .build();

        Map<Job, JobReport> jobReports = jobPipeline.call();

        verify(job1).call();
        verify(job2).call();
        verify(job3).call();
        assertThat(jobReports).hasSize(3);
        assertThat(jobReports.get(job1)).isEqualTo(jobReport1);
        assertThat(jobReports.get(job2)).isEqualTo(jobReport2);
        assertThat(jobReports.get(job3)).isEqualTo(jobReport3);
    }

    @Ignore
    @Test
    public void integrationTest() throws Exception {
        // Given
        String dataSource = "foo" + LINE_SEPARATOR + "bar";
        BlockingQueue<Record> buffer = new LinkedBlockingQueue<>();
        List<String> records = new ArrayList<>();

        Job job1 = aNewJob()
                .named("job1")
                .reader(new StringRecordReader(dataSource))
                .writer(new BlockingQueueRecordWriter(buffer))
                .build();
        Job job2 = aNewJob()
                .named("job2")
                .reader(new BlockingQueueRecordReader(buffer))
                .filter(new PoisonRecordFilter())
                .writer(new CollectionRecordWriter(records))
                .build();

        // When
        JobPipeline jobPipeline = aNewJobPipelineBuilder()
                .startWith(job1)
                .when(jobCompleted()).then(job2)
                .build();
        Map<Job, JobReport> jobReports = jobPipeline.call();

        // Then
        assertThat(jobReports).hasSize(2);
        assertThat(records).hasSize(2);
    }
}