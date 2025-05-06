# Credit Eligibility Prediction System

An AI-powered credit eligibility prediction system that evaluates user credit details and provides personalized credit card recommendations.

## Features

- Credit eligibility prediction using XGBoost/LightGBM
- RAG-based credit card recommendations
- User-friendly web interface
- RESTful API endpoints
- Custom model training capabilities

## Project Structure

```
├── src/
│   ├── data/           # Data processing and dataset management
│   ├── models/         # ML model implementations
│   ├── api/           # FastAPI endpoints
│   └── utils/         # Utility functions
├── tests/             # Test cases
├── config/            # Configuration files
└── requirements.txt   # Project dependencies
```

## Setup

1. Create a virtual environment:
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

3. Run the application:
```bash
# Start the API server
uvicorn src.api.main:app --reload

# Start the web interface
streamlit run src/app.py
```

## API Endpoints

- POST `/predict`: Get credit eligibility prediction
- POST `/recommend`: Get credit card recommendations
- POST `/train`: Train/update the prediction model

## Model Details

- Primary Model: XGBoost/LightGBM for credit prediction
- RAG System: Sentence Transformers + FAISS for recommendations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
