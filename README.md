# Credit Eligibility Prediction System

A comprehensive system for predicting credit eligibility and recommending credit cards using machine learning and GPT-powered analysis.

## Features

- **Credit Eligibility Prediction**: Uses machine learning (Logistic Regression) to predict credit eligibility based on various factors
- **Credit Card Recommendations**: Provides personalized credit card recommendations based on credit profile
- **GPT-Powered Analysis**: Detailed explanations of credit decisions and recommendations using OpenAI's GPT
- **RESTful API**: Easy-to-use endpoints for predictions and recommendations
- **Model Training**: Endpoint to trigger model training with synthetic data

## Tech Stack

- **Backend**: Java Spring Boot
- **Machine Learning**: Apache Commons Math (Logistic Regression)
- **Natural Language Processing**: OpenAI GPT API
- **Database**: H2 (in-memory)
- **API Documentation**: SpringDoc OpenAPI

## Prerequisites

- Java 17 or higher
- Maven
- OpenAI API key

## Setup

1. Clone the repository:
```bash
git clone https://github.com/Naveen0818/new-user-checkin-system.git
cd new-user-checkin-system
```

2. Set up environment variables:
Create a `.env` file in the root directory with:
```
OPENAI_API_KEY=your_api_key_here
```

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Credit Prediction

```http
POST /api/credit/predict
Content-Type: application/json

{
    "ageOfCredit": 5.2,
    "derogatoryMarks": 0,
    "creditUtilization": 0.3,
    "missedPayments": 0,
    "creditInquiries": 2,
    "totalAccounts": 3,
    "creditLimit": 10000.0,
    "income": 75000.0
}
```

### Credit Card Recommendations

```http
POST /api/credit/recommend
Content-Type: application/json

{
    "ageOfCredit": 5.2,
    "derogatoryMarks": 0,
    "creditUtilization": 0.3,
    "missedPayments": 0,
    "creditInquiries": 2,
    "totalAccounts": 3,
    "creditLimit": 10000.0,
    "income": 75000.0
}
```

### Model Training

```http
POST /api/model/train
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── credit/
│   │           ├── controller/
│   │           │   ├── CreditController.java
│   │           │   └── ModelController.java
│   │           ├── model/
│   │           │   ├── CreditCard.java
│   │           │   ├── CreditData.java
│   │           │   └── TrainingData.java
│   │           ├── service/
│   │           │   ├── CreditPredictionService.java
│   │           │   ├── GPTService.java
│   │           │   └── ModelTrainingService.java
│   │           └── CreditEligibilityApplication.java
│   └── resources/
│       └── data/
│           └── credit_cards.json
```

## Model Details

The system uses a Logistic Regression model implemented with Apache Commons Math. The model considers the following features:

- Age of Credit History
- Number of Derogatory Marks
- Credit Utilization Ratio
- Number of Missed Payments
- Number of Credit Inquiries
- Total Number of Accounts
- Total Credit Limit
- Annual Income

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- OpenAI for providing the GPT API
- Apache Commons Math for the machine learning implementation
- Spring Boot team for the excellent framework
