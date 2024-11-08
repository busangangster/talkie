package com.e104.realtime.mqtt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AudioDelta {

    private final List<String> audioDeltas = Collections.synchronizedList(new LinkedList<>());

    public void add(String audioDeltaPiece) {
        audioDeltas.add(audioDeltaPiece);
    }

    public byte[] squash(int userSeq) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (String delta : audioDeltas) {
                byte[] audioBytes = Base64.getDecoder().decode(delta);
                outputStream.write(audioBytes);
            }
            audioDeltas.clear();
            return outputStream.toByteArray(); // 합쳐진 오디오 데이터를 반환
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
