/**
 * 로컬 릴레이 서버를 실행하면 API 키를 숨기고 서버에서 사용자 정의 로직을 실행할 수 있습니다.
 *
 * 로컬 릴레이 서버 주소를 설정하세요:
 * REACT_APP_LOCAL_RELAY_SERVER_URL=http://localhost:8081
 *
 * 이 경우 `.env` 파일에 OPENAI_API_KEY=를 설정해야 합니다.
 * `npm run relay` 명령어로 `npm start`와 함께 병렬로 실행할 수 있습니다.
 */
const LOCAL_RELAY_SERVER_URL: string =
  process.env.REACT_APP_LOCAL_RELAY_SERVER_URL || '';

import { useEffect, useRef, useCallback, useState } from 'react';

import { RealtimeClient } from '@openai/realtime-api-beta';
import { ItemType } from '@openai/realtime-api-beta/dist/lib/client.js';
import { WavRecorder, WavStreamPlayer } from '../lib/wavtools/index.js';
import { instructions } from '../utils/conversation_config.js';
import { WavRenderer } from '../utils/wav_renderer';

import { X, Edit, Zap, ArrowUp, ArrowDown } from 'react-feather';
import { Button } from '../components/button/Button';
import { Toggle } from '../components/toggle/Toggle';
import { Map } from '../components/Map';

import './ConsolePage.scss';
import { isJsxOpeningLikeElement } from 'typescript';

/**
 * get_weather() 함수 호출 결과의 타입 정의
 */
interface Coordinates {
  lat: number;
  lng: number;
  location?: string;
  temperature?: {
    value: number;
    units: string;
  };
  wind_speed?: {
    value: number;
    units: string;
  };
}

/**
 * 모든 이벤트 로그에 대한 타입 정의
 */
interface RealtimeEvent {
  time: string;
  source: 'client' | 'server';
  count?: number;
  event: { [key: string]: any };
}

export function ConsolePage() {
  /**
   * 사용자에게 API 키를 요청합니다.
   * 로컬 릴레이 서버를 사용하는 경우에는 필요하지 않습니다.
   */
  const apiKey = LOCAL_RELAY_SERVER_URL
    ? ''
    : localStorage.getItem('tmp::voice_api_key') ||
      prompt('OpenAI API Key') ||
      '';
  if (apiKey !== '') {
    localStorage.setItem('tmp::voice_api_key', apiKey);
  }

  /**
   * 다음을 인스턴스화합니다:
   * - WavRecorder (음성 입력)
   * - WavStreamPlayer (음성 출력)
   * - RealtimeClient (API 클라이언트)
   */
  const wavRecorderRef = useRef<WavRecorder>(
    new WavRecorder({ sampleRate: 24000 })
  );
  const wavStreamPlayerRef = useRef<WavStreamPlayer>(
    new WavStreamPlayer({ sampleRate: 24000 })
  );
  const clientRef = useRef<RealtimeClient>(
    new RealtimeClient(
      LOCAL_RELAY_SERVER_URL
        ? { url: LOCAL_RELAY_SERVER_URL }
        : {
            apiKey: apiKey,
            dangerouslyAllowAPIKeyInBrowser: true,
          }
    )
  );

  /**
   * 다음을 위한 참조 변수들:
   * - 오디오 시각화 렌더링 (캔버스)
   * - 이벤트 로그 자동 스크롤링
   * - 이벤트 로그 표시를 위한 타이밍 델타
   */
  const clientCanvasRef = useRef<HTMLCanvasElement>(null);
  const serverCanvasRef = useRef<HTMLCanvasElement>(null);
  const eventsScrollHeightRef = useRef(0);
  const eventsScrollRef = useRef<HTMLDivElement>(null);
  const startTimeRef = useRef<string>(new Date().toISOString());

  /**
   * 애플리케이션 상태 표시를 위한 모든 변수들
   * - items는 모든 대화 항목 (대화 내용)
   * - realtimeEvents는 이벤트 로그로, 확장이 가능합니다
   * - memoryKv는 set_memory() 함수용입니다
   * - coords, marker는 get_weather() 함수용입니다
   */
  const [items, setItems] = useState<ItemType[]>([]);
  const [realtimeEvents, setRealtimeEvents] = useState<RealtimeEvent[]>([]);
  const [expandedEvents, setExpandedEvents] = useState<{
    [key: string]: boolean;
  }>({});
  const [isConnected, setIsConnected] = useState(false);
  const [canPushToTalk, setCanPushToTalk] = useState(true);
  const [isRecording, setIsRecording] = useState(false);
  const [memoryKv, setMemoryKv] = useState<{ [key: string]: any }>({});
  const [coords, setCoords] = useState<Coordinates | null>({
    lat: 37.775593,
    lng: -122.418137,
  });
  const [marker, setMarker] = useState<Coordinates | null>(null);

  /**
   * 로그의 타이밍을 형식화하는 유틸리티 함수
   */
  const formatTime = useCallback((timestamp: string) => {
    const startTime = startTimeRef.current;
    const t0 = new Date(startTime).valueOf();
    const t1 = new Date(timestamp).valueOf();
    const delta = t1 - t0;
    const hs = Math.floor(delta / 10) % 100;
    const s = Math.floor(delta / 1000) % 60;
    const m = Math.floor(delta / 60_000) % 60;
    const pad = (n: number) => {
      let s = n + '';
      while (s.length < 2) {
        s = '0' + s;
      }
      return s;
    };
    return `${pad(m)}:${pad(s)}.${pad(hs)}`;
  }, []);

  /**
   * API 키를 클릭했을 때 실행되는 함수
   */
  const resetAPIKey = useCallback(() => {
    const apiKey = prompt('OpenAI API Key');
    if (apiKey !== null) {
      localStorage.clear();
      localStorage.setItem('tmp::voice_api_key', apiKey);
      window.location.reload();
    }
  }, []);

  /**
   * 대화에 연결합니다:
   * WavRecorder는 음성 입력, WavStreamPlayer는 출력, client는 API 클라이언트입니다.
   */
  const connectConversation = useCallback(async () => {
    const client = clientRef.current;
    const wavRecorder = wavRecorderRef.current;
    const wavStreamPlayer = wavStreamPlayerRef.current;

    // 상태 변수 설정
    startTimeRef.current = new Date().toISOString();
    setIsConnected(true);
    setRealtimeEvents([]);
    setItems(client.conversation.getItems());

    // 마이크 연결
    await wavRecorder.begin();

    // 오디오 출력 연결
    await wavStreamPlayer.connect();

    // 실시간 API 연결
    await client.connect();
    client.sendUserMessageContent([
      {
        type: `input_text`,
        text: `아이한테 자연스럽게 인사를 해봐`,
        // text: `For testing purposes, I want you to list ten car brands. Number each item, e.g. "one (or whatever number you are one): the item name".`
      },
    ]);

    if (client.getTurnDetectionType() === 'server_vad') {
      await wavRecorder.record((data) => client.appendInputAudio(data.mono));
    }
  }, []);

  /**
   * 대화 연결을 해제하고 상태를 초기화합니다
   */
  const disconnectConversation = useCallback(async () => {
    setIsConnected(false);
    setRealtimeEvents([]);
    setItems([]);
    setMemoryKv({});
    setCoords({
      lat: 37.775593,
      lng: -122.418137,
    });
    setMarker(null);

    const client = clientRef.current;
    client.disconnect();

    const wavRecorder = wavRecorderRef.current;
    await wavRecorder.end();

    const wavStreamPlayer = wavStreamPlayerRef.current;
    await wavStreamPlayer.interrupt();
  }, []);

  const deleteConversationItem = useCallback(async (id: string) => {
    const client = clientRef.current;
    client.deleteItem(id);
  }, []);

  /**
   * 푸시 투 토크 모드에서 녹음을 시작합니다
   * 각 샘플에 대해 .appendInputAudio() 호출
   */
  const startRecording = async () => {
    setIsRecording(true);
    const client = clientRef.current;
    const wavRecorder = wavRecorderRef.current;
    const wavStreamPlayer = wavStreamPlayerRef.current;
    const trackSampleOffset = await wavStreamPlayer.interrupt();
    if (trackSampleOffset?.trackId) {
      const { trackId, offset } = trackSampleOffset;
      await client.cancelResponse(trackId, offset);
    }
    await wavRecorder.record((data) => client.appendInputAudio(data.mono));
  };

  /**
   * 푸시 투 토크 모드에서 녹음을 중지합니다
   */
  const stopRecording = async () => {
    setIsRecording(false);
    const client = clientRef.current;
    const wavRecorder = wavRecorderRef.current;
    await wavRecorder.pause();
    client.createResponse();
  };

  /**
   * 통신 모드 (수동 <> VAD) 전환
   */
  const changeTurnEndType = async (value: string) => {
    const client = clientRef.current;
    const wavRecorder = wavRecorderRef.current;
    if (value === 'none' && wavRecorder.getStatus() === 'recording') {
      await wavRecorder.pause();
    }
    client.updateSession({
      turn_detection: value === 'none' ? null : { type: 'server_vad' },
    });
    if (value === 'server_vad' && client.isConnected()) {
      await wavRecorder.record((data) => client.appendInputAudio(data.mono));
    }
    setCanPushToTalk(value === 'none');
  };

  /**
   * 이벤트 로그 자동 스크롤
   */
  useEffect(() => {
    if (eventsScrollRef.current) {
      const eventsEl = eventsScrollRef.current;
      const scrollHeight = eventsEl.scrollHeight;
      // 높이가 변경된 경우에만 스크롤
      if (scrollHeight !== eventsScrollHeightRef.current) {
        eventsEl.scrollTop = scrollHeight;
        eventsScrollHeightRef.current = scrollHeight;
      }
    }
  }, [realtimeEvents]);

  /**
   * 대화 로그 자동 스크롤
   */
  useEffect(() => {
    const conversationEls = [].slice.call(
      document.body.querySelectorAll('[data-conversation-content]')
    );
    for (const el of conversationEls) {
      const conversationEl = el as HTMLDivElement;
      conversationEl.scrollTop = conversationEl.scrollHeight;
    }
  }, [items]);

  /**
   * 시각화 캔버스를 위한 렌더 루프 설정
   */
  useEffect(() => {
    let isLoaded = true;

    const wavRecorder = wavRecorderRef.current;
    const clientCanvas = clientCanvasRef.current;
    let clientCtx: CanvasRenderingContext2D | null = null;

    const wavStreamPlayer = wavStreamPlayerRef.current;
    const serverCanvas = serverCanvasRef.current;
    let serverCtx: CanvasRenderingContext2D | null = null;

    const render = () => {
      if (isLoaded) {
        if (clientCanvas) {
          if (!clientCanvas.width || !clientCanvas.height) {
            clientCanvas.width = clientCanvas.offsetWidth;
            clientCanvas.height = clientCanvas.offsetHeight;
          }
          clientCtx = clientCtx || clientCanvas.getContext('2d');
          if (clientCtx) {
            clientCtx.clearRect(0, 0, clientCanvas.width, clientCanvas.height);
            const result = wavRecorder.recording
              ? wavRecorder.getFrequencies('voice')
              : { values: new Float32Array([0]) };
            WavRenderer.drawBars(
              clientCanvas,
              clientCtx,
              result.values,
              '#0099ff',
              10,
              0,
              8
            );
          }
        }
        if (serverCanvas) {
          if (!serverCanvas.width || !serverCanvas.height) {
            serverCanvas.width = serverCanvas.offsetWidth;
            serverCanvas.height = serverCanvas.offsetHeight;
          }
          serverCtx = serverCtx || serverCanvas.getContext('2d');
          if (serverCtx) {
            serverCtx.clearRect(0, 0, serverCanvas.width, serverCanvas.height);
            const result = wavStreamPlayer.analyser
              ? wavStreamPlayer.getFrequencies('voice')
              : { values: new Float32Array([0]) };
            WavRenderer.drawBars(
              serverCanvas,
              serverCtx,
              result.values,
              '#009900',
              10,
              0,
              8
            );
          }
        }
        window.requestAnimationFrame(render);
      }
    };
    render();

    return () => {
      isLoaded = false;
    };
  }, []);

  /**
   * 핵심 RealtimeClient 및 오디오 캡처 설정
   * 모든 지침, 도구, 이벤트 등을 설정합니다.
   */
  useEffect(() => {
    // 참조 가져오기
    const wavStreamPlayer = wavStreamPlayerRef.current;
    const client = clientRef.current;

    // 지침 설정
    client.updateSession({ instructions: instructions });
    // 음성 인식 설정 (사용자 음성 인식을 위해 필요함)
    client.updateSession({ input_audio_transcription: { model: 'whisper-1' } });
    // 세션의 프롬프트 설정
    client.updateSession({
      instructions: `
                    너의 이름은 '토키'야.
                                        
                    1. 너는 5세에서 7세의 아이와 대화해야 해.
                      - 아이가 이해할 수 있도록 쉽게 말해야 하고, 어려운 단어는 사용하면 안 돼.

                    2. 대화할 때는 항상 반말을 사용하고, 친근하게 대해야 해.
                      - 예를 들어, "안녕! 오늘은 어떤 일이 있었어?"처럼.

                    3. 처음에는 아이의 이름, 좋아하는 것, 좋아하는 동물, 색깔 등을 물어보며 친해지는 시간을 가져야 해.
                      - 예를 들어, "너의 이름은 뭐니?" "가장 좋아하는 동물은 뭐야?" "무슨 색깔을 좋아해?"라고 물어봐.

                    4. 아이와의 대화에서 자연스럽게 질문과 대답을 주고받으면서 재미있게 놀아주는 역할을 해.
                      - 아이가 대답하면 적절한 반응과 함께 다음 질문을 해줘.
                      - 예를 들어, 아이가 "나는 강아지를 좋아해"라고 하면, "정말? 강아지는 귀엽지! 너는 어떤 강아지를 좋아해?"라고 질문해.

                    5. 같은 질문을 반복하지 않고, 새로운 질문으로 대화를 이어가야 해.
                      - 예를 들어, 아이가 좋아하는 것, 싫어하는 것, 오늘 있었던 일, 즐거웠던 일, 가족 등에 대한 질문을 바꿔서 계속 대화해.

                    6. 모든 대화는 한국어로 진행해야 해.

                    7. 아이가 말한 정보(대화 내용)는 모두 기억하고, 다음 대화에서 그 정보를 활용해야 해. 이미 했던 질문을 다시 하면 안 돼.
                      - 예를 들어, "너는 고양이를 좋아한다고 했지? 고양이에 대해 더 이야기해볼래?"처럼.

                    8. 아이가 질문했을 때는 항상 대답한 후 추가 질문을 해줘.
                      - 예: "재미있는 게임을 찾자! 너는 어떤 게임이 좋아?"라고 물어봐.

                    9. 대화가 자연스럽고 즐겁게 이어지도록 노력해야 해. 아이가 웃거나 즐거워하는 반응을 보일 수 있도록 해줘.
                        - 예를 들어, "우리 함께 놀면서 재미있는 이야기를 나눠보자!"라고 말해.`,
    });

    // 도구 추가
    client.addTool(
      {
        name: 'set_memory',
        description: '사용자에 대한 중요한 데이터를 메모리에 저장합니다.',
        parameters: {
          type: 'object',
          properties: {
            key: {
              type: 'string',
              description:
                '메모리 값의 키입니다. 항상 소문자와 밑줄만 사용하며, 다른 문자는 사용할 수 없습니다.',
            },
            value: {
              type: 'string',
              description: '값은 문자열로 표현될 수 있는 모든 것입니다.',
            },
          },
          required: ['key', 'value'],
        },
      },
      async ({ key, value }: { [key: string]: any }) => {
        setMemoryKv((memoryKv) => {
          const newKv = { ...memoryKv };
          newKv[key] = value;
          return newKv;
        });
        return { ok: true };
      }
    );
    client.addTool(
      {
        name: 'get_weather',
        description:
          '지정된 위도, 경도 좌표에 대한 날씨 정보를 가져옵니다. 위치에 대한 레이블을 지정하세요.',
        parameters: {
          type: 'object',
          properties: {
            lat: {
              type: 'number',
              description: '위도',
            },
            lng: {
              type: 'number',
              description: '경도',
            },
            location: {
              type: 'string',
              description: '위치의 이름',
            },
          },
          required: ['lat', 'lng', 'location'],
        },
      },
      async ({ lat, lng, location }: { [key: string]: any }) => {
        setMarker({ lat, lng, location });
        setCoords({ lat, lng, location });
        const result = await fetch(
          `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lng}&current=temperature_2m,wind_speed_10m`
        );
        const json = await result.json();
        const temperature = {
          value: json.current.temperature_2m as number,
          units: json.current_units.temperature_2m as string,
        };
        const wind_speed = {
          value: json.current.wind_speed_10m as number,
          units: json.current_units.wind_speed_10m as string,
        };
        setMarker({ lat, lng, location, temperature, wind_speed });
        return json;
      }
    );

    // 클라이언트와 서버의 실시간 이벤트 처리 (이벤트 로깅 용)
    client.on('realtime.event', (realtimeEvent: RealtimeEvent) => {
      setRealtimeEvents((realtimeEvents) => {
        const lastEvent = realtimeEvents[realtimeEvents.length - 1];
        if (lastEvent?.event.type === realtimeEvent.event.type) {
          // 동일한 이벤트가 연속으로 발생하면 디스플레이 목적으로 집계
          lastEvent.count = (lastEvent.count || 0) + 1;
          return realtimeEvents.slice(0, -1).concat(lastEvent);
        } else {
          return realtimeEvents.concat(realtimeEvent);
        }
      });
    });
    client.on('error', (event: any) => console.error(event));
    client.on('conversation.interrupted', async () => {
      const trackSampleOffset = await wavStreamPlayer.interrupt();
      if (trackSampleOffset?.trackId) {
        const { trackId, offset } = trackSampleOffset;
        await client.cancelResponse(trackId, offset);
      }
    });
    client.on('conversation.updated', async ({ item, delta }: any) => {
      const items = client.conversation.getItems();
      if (delta?.audio) {
        wavStreamPlayer.add16BitPCM(delta.audio, item.id);
      }
      if (item.status === 'completed' && item.formatted.audio?.length) {
        const wavFile = await WavRecorder.decode(
          item.formatted.audio,
          24000,
          24000
        );
        item.formatted.file = wavFile;
      }
      setItems(items);
    });

    setItems(client.conversation.getItems());

    return () => {
      // 정리; 기본값으로 재설정
      client.reset();
    };
  }, []);

  /**
   * 애플리케이션을 렌더링합니다.
   */
  return (
    <div data-component="ConsolePage">
      <div className="content-top">
        <div className="content-title">
          <img src="/openai-logomark.svg" />
          <span>realtime console</span>
        </div>
        <div className="content-api-key">
          {!LOCAL_RELAY_SERVER_URL && (
            <Button
              icon={Edit}
              iconPosition="end"
              buttonStyle="flush"
              label={`api key: ${apiKey.slice(0, 3)}...`}
              onClick={() => resetAPIKey()}
            />
          )}
        </div>
      </div>
      <div className="content-main">
        <div className="content-logs">
          <div className="content-block events">
            <div className="visualization">
              <div className="visualization-entry client">
                <canvas ref={clientCanvasRef} />
              </div>
              <div className="visualization-entry server">
                <canvas ref={serverCanvasRef} />
              </div>
            </div>
            <div className="content-block-title">events</div>
            <div className="content-block-body" ref={eventsScrollRef}>
              {!realtimeEvents.length && `awaiting connection...`}
              {realtimeEvents.map((realtimeEvent, i) => {
                const count = realtimeEvent.count;
                const event = { ...realtimeEvent.event };
                if (event.type === 'input_audio_buffer.append') {
                  event.audio = `[trimmed: ${event.audio.length} bytes]`;
                } else if (event.type === 'response.audio.delta') {
                  event.delta = `[trimmed: ${event.delta.length} bytes]`;
                }
                return (
                  <div className="event" key={event.event_id}>
                    <div className="event-timestamp">
                      {formatTime(realtimeEvent.time)}
                    </div>
                    <div className="event-details">
                      <div
                        className="event-summary"
                        onClick={() => {
                          // 이벤트 세부 사항 토글
                          const id = event.event_id;
                          const expanded = { ...expandedEvents };
                          if (expanded[id]) {
                            delete expanded[id];
                          } else {
                            expanded[id] = true;
                          }
                          setExpandedEvents(expanded);
                        }}
                      >
                        <div
                          className={`event-source ${
                            event.type === 'error'
                              ? 'error'
                              : realtimeEvent.source
                          }`}
                        >
                          {realtimeEvent.source === 'client' ? (
                            <ArrowUp />
                          ) : (
                            <ArrowDown />
                          )}
                          <span>
                            {event.type === 'error'
                              ? 'error!'
                              : realtimeEvent.source}
                          </span>
                        </div>
                        <div className="event-type">
                          {event.type}
                          {count && ` (${count})`}
                        </div>
                      </div>
                      {!!expandedEvents[event.event_id] && (
                        <div className="event-payload">
                          {JSON.stringify(event, null, 2)}
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
          <div className="content-block conversation">
            <div className="content-block-title">conversation</div>
            <div className="content-block-body" data-conversation-content>
              {!items.length && `awaiting connection...`}
              {items.map((conversationItem, i) => {
                return (
                  <div className="conversation-item" key={conversationItem.id}>
                    <div className={`speaker ${conversationItem.role || ''}`}>
                      <div>
                        {(
                          conversationItem.role || conversationItem.type
                        ).replaceAll('_', ' ')}
                      </div>
                      <div
                        className="close"
                        onClick={() =>
                          deleteConversationItem(conversationItem.id)
                        }
                      >
                        <X />
                      </div>
                    </div>
                    <div className={`speaker-content`}>
                      {/* 도구 응답 */}
                      {conversationItem.type === 'function_call_output' && (
                        <div>{conversationItem.formatted.output}</div>
                      )}
                      {/* 도구 호출 */}
                      {!!conversationItem.formatted.tool && (
                        <div>
                          {conversationItem.formatted.tool.name}(
                          {conversationItem.formatted.tool.arguments})
                        </div>
                      )}
                      {!conversationItem.formatted.tool &&
                        conversationItem.role === 'user' && (
                          <div>
                            {conversationItem.formatted.transcript ||
                              (conversationItem.formatted.audio?.length
                                ? '(awaiting transcript)'
                                : conversationItem.formatted.text ||
                                  '(item sent)')}
                          </div>
                        )}
                      {!conversationItem.formatted.tool &&
                        conversationItem.role === 'assistant' && (
                          <div>
                            {conversationItem.formatted.transcript ||
                              conversationItem.formatted.text ||
                              '(truncated)'}
                          </div>
                        )}
                      {conversationItem.formatted.file && (
                        <audio
                          src={conversationItem.formatted.file.url}
                          controls
                        />
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
          <div className="content-actions">
            <Toggle
              defaultValue={false}
              labels={['manual', 'vad']}
              values={['none', 'server_vad']}
              onChange={(_, value) => changeTurnEndType(value)}
            />
            <div className="spacer" />
            {isConnected && canPushToTalk && (
              <Button
                label={isRecording ? 'release to send' : 'push to talk'}
                buttonStyle={isRecording ? 'alert' : 'regular'}
                disabled={!isConnected || !canPushToTalk}
                onMouseDown={startRecording}
                onMouseUp={stopRecording}
              />
            )}
            <div className="spacer" />
            <Button
              label={isConnected ? 'disconnect' : 'connect'}
              iconPosition={isConnected ? 'end' : 'start'}
              icon={isConnected ? X : Zap}
              buttonStyle={isConnected ? 'regular' : 'action'}
              onClick={
                isConnected ? disconnectConversation : connectConversation
              }
            />
          </div>
        </div>
        <div className="content-right">
          <div className="content-block map">
            <div className="content-block-title">get_weather()</div>
            <div className="content-block-title bottom">
              {marker?.location || 'not yet retrieved'}
              {!!marker?.temperature && (
                <>
                  <br />
                  🌡️ {marker.temperature.value} {marker.temperature.units}
                </>
              )}
              {!!marker?.wind_speed && (
                <>
                  {' '}
                  🍃 {marker.wind_speed.value} {marker.wind_speed.units}
                </>
              )}
            </div>
            <div className="content-block-body full">
              {coords && (
                <Map
                  center={[coords.lat, coords.lng]}
                  location={coords.location}
                />
              )}
            </div>
          </div>
          <div className="content-block kv">
            <div className="content-block-title">set_memory()</div>
            <div className="content-block-body content-kv">
              {JSON.stringify(memoryKv, null, 2)}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
