/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package PasswordCracker;

import static PasswordCracker.PasswordCrackerUtil.TOTAL_PASSWORD_RANGE_SIZE;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;

public class CandidateRangeInputFormat extends InputFormat<Text, Text> {
    private List<InputSplit> splits;

    @Override
    public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        return new CandidateRangeRecordReader();
    }


    // It generate the splits which are consist of string (or solution space range) and return to JobClient.
    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
        splits = new ArrayList<>();

        int numberOfSplit = job.getConfiguration().getInt("numberOfSplit", 1);    //get map_count
        long subRangeSize = (TOTAL_PASSWORD_RANGE_SIZE + numberOfSplit - 1) / numberOfSplit;

        /** COMPLETE **/
        long rangeBegin, rangeEnd;
        for (int i = 0; i < numberOfSplit; i++) {
            rangeBegin = i * subRangeSize;
            rangeEnd = (i != numberOfSplit - 1) ? ((i + 1) * subRangeSize - 1) : (TOTAL_PASSWORD_RANGE_SIZE - 1);

            String inputRange = Long.toString(rangeBegin) + "/" + Long.toString(rangeEnd);

            splits.add(new CandidateRangeInputSplit(inputRange, rangeBegin, subRangeSize, new String[]{}));
        }

        return splits;
    }
}
