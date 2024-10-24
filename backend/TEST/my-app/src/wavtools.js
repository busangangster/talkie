// wavtools.js

export class WavRecorder {
  constructor({ sampleRate }) {
    this.sampleRate = sampleRate;
    this.mediaRecorder = null;
  }

  async begin(stream) {
    this.mediaRecorder = new MediaRecorder(stream, {
      mimeType: "audio/webm;codecs=pcm",
    });
    return new Promise((resolve) => {
      this.mediaRecorder.onstart = resolve;
    });
  }

  record(callback) {
    this.mediaRecorder.ondataavailable = (event) => {
      const reader = new FileReader();
      reader.onload = () => {
        const audioBuffer = new Int16Array(reader.result);
        callback({ mono: audioBuffer });
      };
      reader.readAsArrayBuffer(event.data);
    };
    this.mediaRecorder.start(100); // 100ms마다 chunk를 기록
  }

  pause() {
    this.mediaRecorder.stop();
  }

  clear() {
    this.mediaRecorder = null;
  }
}

export class WavStreamPlayer {
  constructor({ sampleRate }) {
    this.sampleRate = sampleRate;
    this.context = new (window.AudioContext || window.webkitAudioContext)();
    this.audioBufferQueue = [];
  }

  async add16BitPCM(audioBuffer, id) {
    const audioBufferNode = this.context.createBuffer(1, audioBuffer.length, this.sampleRate);
    const channel = audioBufferNode.getChannelData(0);
    for (let i = 0; i < audioBuffer.length; i++) {
      channel[i] = audioBuffer[i] / 32768.0;
    }
    this.playBuffer(audioBufferNode);
  }

  playBuffer(buffer) {
    const source = this.context.createBufferSource();
    source.buffer = buffer;
    source.connect(this.context.destination);
    source.start(0);
  }
}
