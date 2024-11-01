from fastapi import FastAPI
from pydantic import BaseModel
import torch
import torch.nn as nn
import pytorch_lightning as pl
from transformers import ElectraModel, AutoTokenizer
from konlpy.tag import Okt
from typing import Dict, List, Tuple
import re
from collections import Counter
# 감정 레이블 리스트
LABELS = ['불평/불만', '환영/호의', '감동/감탄', '지긋지긋', '고마움', '슬픔', '화남/분노', '존경', '기대감',
          '우쭐댐/무시함', '안타까움/실망', '비장함', '의심/불신', '뿌듯함', '편안/쾌적', '신기함/관심', '아껴주는',
          '부끄러움', '공포/무서움', '절망', '한심함', '역겨움/징그러움', '짜증', '어이없음', '없음', '패배/자기혐오',
          '귀찮음', '힘듦/지침', '즐거움/신남', '깨달음', '죄책감', '증오/혐오', '흐뭇함(귀여움/예쁨)', '당황/난처',
          '경악', '부담/안_내킴', '서러움', '재미없음', '불쌍함/연민', '놀람', '행복', '불안/걱정', '기쁨', '안심/신뢰']

# PyTorch 모델 정의
device = "cuda" if torch.cuda.is_available() else "cpu"


class KOTEtagger(pl.LightningModule):
    def __init__(self):
        super().__init__()
        self.electra = ElectraModel.from_pretrained("beomi/KcELECTRA-base", revision='v2021').to(device)
        self.tokenizer = AutoTokenizer.from_pretrained("beomi/KcELECTRA-base", revision='v2021')
        self.classifier = nn.Linear(self.electra.config.hidden_size, len(LABELS)).to(device)

    def forward(self, text: str):
        encoding = self.tokenizer.encode_plus(
            text,
            add_special_tokens=True,
            max_length=512,
            return_token_type_ids=False,
            padding="max_length",
            return_attention_mask=True,
            return_tensors='pt'
        ).to(device)
        output = self.electra(encoding["input_ids"], attention_mask=encoding["attention_mask"])
        output = output.last_hidden_state[:, 0, :]
        output = self.classifier(output)
        output = torch.sigmoid(output)
        return output


# 모델 초기화 및 로드
trained_model = KOTEtagger()
trained_model.load_state_dict(torch.load(r"C:\Users\SSAFY\Downloads\kote_pytorch_lightning.bin"), strict=False)
trained_model.eval()

# FastAPI 초기화
app = FastAPI()


# 요청 데이터 모델
class TextRequest(BaseModel):
    text: str

#응답 Dto
class EmotionResponse(BaseModel):
    text: str
    predictions: Dict[str,float]

class MorphAnalyzeResponse(BaseModel):
    morph_analyze: float

class WordCloudResponse(BaseModel):
    word_cloud: List[Tuple[str,int]];


# 감정 카테고리 매핑
category_mapping = {
    '기쁨점수': ['환영/호의', '감동/감탄', '고마움', '뿌듯함', '편안/쾌적', '즐거움/신남', '깨달음', '행복', '기쁨', '안심/신뢰'],
    '사랑스러움점수': ['존경', '기대감', '아껴주는', '흐뭇함(귀여움/예쁨)'],
    '슬픔점수': ['슬픔', '안타까움/실망', '절망', '패배/자기혐오', '힘듦/지침', '서러움', '불쌍함/연민'],
    '두려움점수': ['공포/무서움', '불안/걱정', '당황/난처', '경악'],
    '분노점수': ['불평/불만', '지긋지긋', '화남/분노', '우쭐댐/무시함', '의심/불신', '짜증', '어이없음', '한심함', '역겨움/징그러움', '귀찮음', '증오/혐오'],
    '놀라움점수': ['신기함/관심', '놀람']
}


# 감정 매핑 함수
def map_emotion_scores(emotion_preds):

    # 각 주요 감정 카테고리의 점수를 초기화
    category_scores = {
        '기쁨점수': 0,
        '사랑스러움점수': 0,
        '슬픔점수': 0,
        '두려움점수': 0,
        '분노점수': 0,
        '놀라움점수': 0
    }

    # 각 감정 레이블을 주요 감정 카테고리에 합산
    for emotion, score in emotion_preds.items():
        for category, emotions in category_mapping.items():
            if emotion in emotions:
                category_scores[category] += score
                break

    return category_scores

# 감성 분석 API 엔드포인트
@app.post("/api/data/emotion", response_model=EmotionResponse)
async def predict_emotion(request: TextRequest):
    text = request.text
    with torch.no_grad():
        # 모델을 통해 44개 감정 예측 점수 계산
        preds = trained_model(text)[0]
        # 감정 레이블과 예측 점수를 쌍으로 묶어 사전 형태로 변환
        emotion_preds = {label: float(pred) for label, pred in zip(LABELS, preds)}

    # 예측 결과 중 0.4 이상의 점수를 가진 감정만 필터링
    filtered_emotion_preds = {label: score for label, score in emotion_preds.items() if score > 0.4}

    # 44개의 감정을 6개의 주요 감정으로 매핑하여 합산
    mapped_scores = map_emotion_scores(filtered_emotion_preds)

    total = 0

    for key, value in mapped_scores.items():
        total +=value

    for key, value in mapped_scores.items():
        mapped_scores[key] = int(round(mapped_scores[key]*100/total))


    return EmotionResponse(text=text, predictions=mapped_scores)

@app.post("/api/data/vocabulary",response_model=MorphAnalyzeResponse)
async def morphAnalyze(request: TextRequest):

    text = request.text
    okt = Okt()
    sentences = re.split(r'[.!?]', text)

    if len(sentences) == 0:
        ans = 0  # 텍스트가 비어 있을 경우 평균 형태소 수를 0으로 설정
    else:
        ans = sum(len(okt.morphs(sentence)) for sentence in sentences) / len(sentences)

    return MorphAnalyzeResponse(morph_analyze=ans)

@app.post("/api/data/wordcloud",response_model=WordCloudResponse)
async def wordcloud(request: TextRequest):
    okt = Okt()
    text = request.text
    # 명사 추출 및 불용어 제거
    words = okt.nouns(text)
    stop_words = ['제', '저']
    filtered_words = [word for word in words if word not in stop_words]

    # 단어 빈도 계산 후 리스트 변환
    word_counts = Counter(filtered_words)
    word_counts_list = list(word_counts.items())

    return WordCloudResponse(word_cloud=word_counts_list)

