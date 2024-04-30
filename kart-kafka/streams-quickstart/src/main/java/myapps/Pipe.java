/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package myapps;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;


import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * In this example, we implement a simple Pipe program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text,
 * and writes the messages as-is into a sink topic "streams-pipe-output".
 */
public class Pipe {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kartStream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:29092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        String inputStreamName = "triage";

        TopicNameExtractor<String, String> triageTopicExtractor = (key, value, recordContext) -> {
            String dispatchTopic = "garage"; // Topic predefinito
            try {
                // Effettua il parsing del messaggio JSON per estrarre il valore di "Status"
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(value);
                int status = jsonNode.has("Status") ? jsonNode.get("Status").asInt() : -1; // Valore predefinito se "Status" non è presente
                // Determina il topic di destinazione in base al valore di "Status"
                if (status == 2) {
                    dispatchTopic = "scrapyard"; // Se lo status è 2, manda il messaggio al topic "kart-scrapyard"
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dispatchTopic;
        };

        builder.<String, String>stream(inputStreamName).to(triageTopicExtractor);

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
