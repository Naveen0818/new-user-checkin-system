# CheckIn App

A React Native application for user check-in management.

## Prerequisites

- Node.js (v14 or later)
- npm or yarn
- Expo CLI
- iOS Simulator (for Mac) or Android Studio (for Android)

## Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm start
```

3. Run on iOS:
```bash
npm run ios
```

4. Run on Android:
```bash
npm run android
```

## Features

- User authentication (login/register)
- Secure token-based authentication
- User profile management
- Check-in functionality

## API Configuration

The app is configured to connect to a local Spring Boot backend running on `http://localhost:8080`. To change this:

1. Open `src/services/api.ts`
2. Update the `API_URL` constant with your backend URL

## Project Structure

```
src/
  ├── screens/         # Screen components
  ├── services/        # API services
  ├── types/          # TypeScript types
  └── components/     # Reusable components
```

## Development

- The app uses TypeScript for type safety
- React Navigation for screen management
- Axios for API calls
- AsyncStorage for local storage

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request
