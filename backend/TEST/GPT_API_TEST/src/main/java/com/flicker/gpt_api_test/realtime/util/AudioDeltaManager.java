package com.flicker.gpt_api_test.realtime.util;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Component
public class AudioDeltaManager {

    private final List<String> deltas = new LinkedList<>();

    public void add(String delta) {
        deltas.add(delta);
    }

    private byte[] flush() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (String delta : deltas) {
            byte[] audioBytes = Base64.getDecoder().decode(delta);
            outputStream.write(audioBytes);
        }
        deltas.clear();
        return outputStream.toByteArray(); // 합쳐진 오디오 데이터를 반환
    }

    public String flushWithEncoding() throws IOException {
        return Base64.getEncoder().encodeToString(flush());
    }
}
