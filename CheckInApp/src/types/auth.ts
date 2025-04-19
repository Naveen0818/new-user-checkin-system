export interface AuthRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    username: string;
    role: string;
}

export interface UserRegistrationRequest {
    username: string;
    password: string;
    firstName: string;
    lastName: string;
    email: string;
    locationId?: number;
    managerId?: number;
}

export interface UserDto {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    role: string;
    managerId?: number;
    managerName?: string;
    locationId?: number;
    locationName?: string;
} 