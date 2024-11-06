package com.e104.realtime.mqtt;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AudioDeltaService {

    private Map<Integer, List<String>> audioDeltasMap = new ConcurrentHashMap<>();

    /**
     * 입력받은 audioDelta 를 저장한다.
     *
     * @param userSeq 사용자 시퀀스.
     * @param delta   base64로 인코딩된 오디오 데이터.
     */
    public void add(int userSeq, String delta) {
        if (!audioDeltasMap.containsKey(userSeq)) {
            audioDeltasMap.put(userSeq, Collections.synchronizedList(new LinkedList<>()));
        }
        audioDeltasMap.get(userSeq).add(delta);
    }

    /**
     * 입력받은 audioDelta 를 합쳐서 반환한다.
     *
     * @param userSeq 사용자 시퀀스.
     * @return 합쳐진 오디오 데이터. 암호화되지 않음.
     */
    public byte[] squash(int userSeq) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            List<String> audioDeltas = audioDeltasMap.get(userSeq);
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
