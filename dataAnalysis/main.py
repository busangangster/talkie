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
trained_model.load_state_dict(torch.load("/app/kote_pytorch_lightning.bin"), strict=False)
#trained_model.load_state_dict(torch.load(r"C:\Users\SSAFY\Downloads\kote_pytorch_lightning.bin"), strict=False)
trained_model.eval()

# FastAPI 초기화
app = FastAPI()


# 요청 데이터 모델
class TextListRequest(BaseModel):
    textList: List[str]

#응답 Dto
class EmotionResponse(BaseModel):
    predictions: Dict[str,int]

class MorphAnalyzeResponse(BaseModel):
    morph_analyze: float

class WordCloudItem(BaseModel):
    word: str
    count: int

class WordCloudResponse(BaseModel):
    wordCloud: List[WordCloudItem]


# 감정 카테고리 매핑
category_mapping = {
    'happyScore': ['환영/호의', '감동/감탄', '고마움', '뿌듯함', '편안/쾌적', '즐거움/신남', '깨달음', '행복', '기쁨', '안심/신뢰'],
    'loveScore': ['존경', '기대감', '아껴주는', '흐뭇함(귀여움/예쁨)'],
    'sadScore': ['슬픔', '안타까움/실망', '절망', '패배/자기혐오', '힘듦/지침', '서러움', '불쌍함/연민'],
    'scaryScore': ['공포/무서움', '불안/걱정', '당황/난처', '경악'],
    'angryScore': ['불평/불만', '지긋지긋', '화남/분노', '우쭐댐/무시함', '의심/불신', '짜증', '어이없음', '한심함', '역겨움/징그러움', '귀찮음', '증오/혐오'],
    'amazingScore': ['신기함/관심', '놀람']
}


# 감정 매핑 함수
def map_emotion_scores(emotion_preds):

    # 각 주요 감정 카테고리의 점수를 초기화
    category_scores = {
        'happyScore': 0,
        'loveScore': 0,
        'sadScore': 0,
        'scaryScore': 0,
        'angryScore': 0,
        'amazingScore': 0
    }

    # 각 감정 레이블을 주요 감정 카테고리에 합산
    for emotion, score in emotion_preds.items():
        for category, emotions in category_mapping.items():
            if emotion in emotions:
                category_scores[category] += score
                break

    return category_scores

@app.post("/api/data/emotion", response_model=EmotionResponse)
async def predict_emotion(request: TextListRequest):
    texts = request.textList

    mapped_scores = {
        'happyScore': 0,
        'loveScore': 0,
        'sadScore': 0,
        'scaryScore': 0,
        'angryScore': 0,
        'amazingScore': 0
    }

    # 배치 사이즈 설정
    batch_size = 50

    # 텍스트를 배치로 나누기
    for i in range(0, len(texts), batch_size):
        batch_texts = texts[i:i + batch_size]  # 현재 배치 텍스트

        input_text = ''

        for text in batch_texts:
            input_text += text

        # 각 텍스트에 대한 감정 예측 수행
        with torch.no_grad():
            preds = trained_model(input_text)  # 배치 전체를 모델에 전달
            for pred in preds:
                emotion_preds = {label: float(pred_val) for label, pred_val in zip(LABELS, pred)}

                filtered_emotion_preds = {label: score for label, score in emotion_preds.items() if score > 0.4}
                mapped_score = map_emotion_scores(filtered_emotion_preds)

                # mapped_score 값을 mapped_scores에 누적
                for key in mapped_scores.keys():
                    mapped_scores[key] += mapped_score.get(key, 0)

    # 모든 점수의 합 구하기
    total = sum(mapped_scores.values())
    if total > 0:
        # 각 감정 점수를 총합 100이 되도록 정규화
        for key in mapped_scores:
            mapped_scores[key] = int(round(mapped_scores[key] * 100 / total))

    # 정규화 후 다시 합산하여 정확히 100이 되도록 조정
    total_after_rounding = sum(mapped_scores.values())
    difference = 100 - total_after_rounding

    if difference != 0:
        # 가장 큰 값에 남은 차이를 더하거나 빼서 100으로 맞춤
        max_key = max(mapped_scores, key=mapped_scores.get)
        mapped_scores[max_key] += difference

    return EmotionResponse(predictions=mapped_scores)



@app.post("/api/data/vocabulary",response_model=MorphAnalyzeResponse)
async def morphAnalyze(request: TextListRequest):

    text = request.textList
    N = len(text)

    okt = Okt()

    if len(text) == 0:
        ans = -1  # 텍스트가 비어 있을 경우 평균 형태소 수를 0으로 설정
    else:
        ans = sum(len(okt.morphs(te)) for te in text) / N

    return MorphAnalyzeResponse(morph_analyze=ans)


@app.post("/api/data/wordcloud", response_model=WordCloudResponse)
async def wordcloud(request: TextListRequest):

    okt = Okt()
    text = request.textList

    # 명사 추출 및 불용어 제거
    words = []

    for te in text:
        words.extend(okt.nouns(te))

    stop_words = ['제', '저']
    filtered_words = [word for word in words if (word not in stop_words) and (len(word) > 1)]


    # 단어 빈도 계산
    word_counts = Counter(filtered_words)

    # 단어와 빈도수의 리스트를 WordCloudItem 객체 리스트로 변환
    word_counts_list = [WordCloudItem(word=word, count=count) for word, count in word_counts.items()]

    return WordCloudResponse(wordCloud=word_counts_list)
