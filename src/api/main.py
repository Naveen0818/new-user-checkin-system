from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any, Optional
import pandas as pd
import numpy as np
import os
import json

from src.models.credit_predictor import CreditPredictor
from src.models.card_recommender import CardRecommender
from src.utils.gpt_helper import get_credit_explanation, get_card_recommendations

app = FastAPI(
    title="Credit Eligibility Prediction API",
    description="API for credit eligibility prediction and card recommendations",
    version="1.0.0"
)

# Initialize models
predictor = CreditPredictor()
recommender = CardRecommender()

# Load models if they exist
MODEL_PATH = "models/credit_predictor.joblib"
INDEX_PATH = "models/card_recommender"
CARDS_DATA_PATH = "data/credit_cards.json"

if os.path.exists(MODEL_PATH):
    predictor.load_model(MODEL_PATH)

if os.path.exists(f"{INDEX_PATH}.index"):
    recommender.load_index(INDEX_PATH)

# Load credit cards data
with open(CARDS_DATA_PATH, 'r') as f:
    cards_data = json.load(f)

class CreditData(BaseModel):
    age_of_credit: float
    derogatory_marks: int
    credit_utilization: float
    missed_payments: int
    credit_inquiries: int
    total_accounts: int
    credit_limit: float
    income: float

class RecommendationRequest(BaseModel):
    credit_score: int
    income: float
    credit_history: str
    num_recommendations: Optional[int] = 3

class TrainingData(BaseModel):
    features: List[Dict[str, Any]]
    labels: List[int]

@app.post("/predict")
async def predict_eligibility(data: CreditData):
    """
    Predict credit eligibility based on user data.
    """
    try:
        # Convert input data to DataFrame
        input_data = pd.DataFrame([data.dict()])
        
        # Make prediction
        prediction = predictor.predict(input_data)
        
        # Get probability of being eligible
        eligibility_prob = float(prediction[0][1])
        
        # Get GPT-3.5 explanation
        explanation = get_credit_explanation(data.dict(), eligibility_prob)
        
        return {
            "eligibility_probability": eligibility_prob,
            "is_eligible": eligibility_prob >= 0.5,
            "explanation": explanation
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/recommend")
async def get_recommendations(request: RecommendationRequest):
    """
    Get credit card recommendations based on user profile.
    """
    try:
        # Get GPT-3.5 recommendations
        recommendations = get_card_recommendations(
            credit_score=request.credit_score,
            income=request.income,
            credit_history=request.credit_history,
            cards_data=cards_data
        )
        
        return {"recommendations": recommendations}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/train")
async def train_model(data: TrainingData):
    """
    Train the credit prediction model with new data.
    """
    try:
        # Convert training data to DataFrame
        X = pd.DataFrame(data.features)
        y = pd.Series(data.labels)
        
        # Train the model
        predictor.train(X, y)
        
        # Save the model
        os.makedirs(os.path.dirname(MODEL_PATH), exist_ok=True)
        predictor.save_model(MODEL_PATH)
        
        return {"message": "Model trained and saved successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health_check():
    """
    Health check endpoint.
    """
    return {"status": "healthy"} 