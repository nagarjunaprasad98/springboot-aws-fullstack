package com.sb1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import java.util.Collections;

@Service
public class CloudWatchService {
    @Autowired
    private final CloudWatchLogsClient client;

    public CloudWatchService(CloudWatchLogsClient client) {
        this.client = client;
    }

    private static final String LOG_GROUP_NAME = "sb1-app";
    private static final String LOG_STREAM_NAME = "sb1-app-logstream";

    public synchronized void logMessage(String message) {

        ensureLogGroupAndStream();

        InputLogEvent event = InputLogEvent.builder()
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();

        PutLogEventsRequest request = PutLogEventsRequest.builder()
                .logGroupName(LOG_GROUP_NAME)
                .logStreamName(LOG_STREAM_NAME)
                .logEvents(Collections.singletonList(event))
                .sequenceToken(getUploadSequenceToken())
                .build();

        try {
            client.putLogEvents(request);
        } catch (InvalidSequenceTokenException e) {
            PutLogEventsRequest retry = request.toBuilder()
                    .sequenceToken(e.expectedSequenceToken())
                    .build();

            client.putLogEvents(retry);
        }
    }

    private void ensureLogGroupAndStream() {
        try {
            client.createLogGroup(
                    CreateLogGroupRequest.builder()
                            .logGroupName(LOG_GROUP_NAME)
                            .build()
            );
        } catch (ResourceAlreadyExistsException ignored) {}

        try {
            client.createLogStream(
                    CreateLogStreamRequest.builder()
                            .logGroupName(LOG_GROUP_NAME)
                            .logStreamName(LOG_STREAM_NAME)
                            .build()
            );
        } catch (ResourceAlreadyExistsException ignored) {}
    }

    private String getUploadSequenceToken() {
        DescribeLogStreamsResponse response =
                client.describeLogStreams(
                        DescribeLogStreamsRequest.builder()
                                .logGroupName(LOG_GROUP_NAME)
                                .logStreamNamePrefix(LOG_STREAM_NAME)
                                .build()
                );

        if (response.logStreams().isEmpty()) return null;

        return response.logStreams().get(0).uploadSequenceToken();
    }
}


