import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { AuthRequest, AuthResponse, UserRegistrationRequest, UserDto } from '../types/auth';

const API_URL = 'http://localhost:8080'; // Change this to your backend URL

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add token to requests if it exists
api.interceptors.request.use(async (config) => {
    const token = await AsyncStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const authService = {
    login: async (credentials: AuthRequest): Promise<AuthResponse> => {
        const response = await api.post<AuthResponse>('/auth/login', credentials);
        await AsyncStorage.setItem('token', response.data.token);
        return response.data;
    },

    register: async (userData: UserRegistrationRequest): Promise<UserDto> => {
        const response = await api.post<UserDto>('/auth/register', userData);
        return response.data;
    },

    logout: async (): Promise<void> => {
        await AsyncStorage.removeItem('token');
    },

    getCurrentUser: async (): Promise<UserDto | null> => {
        try {
            const response = await api.get<UserDto>('/users/me');
            return response.data;
        } catch (error) {
            return null;
        }
    },
}; 