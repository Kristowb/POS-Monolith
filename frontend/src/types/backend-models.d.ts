/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2026-05-23 21:48:09.

export interface AuthResponse {
    token: string;
    tokenType: string;
    expiresIn: number;
    user: UserResponse;
}

export interface AuthResponseBuilder {
}

export interface GoogleLoginRequest {
    idToken: string;
}

export interface GoogleLoginRequestBuilder {
}

export interface HealthCheckDto {
    status: string;
    timestamp: string;
    version: string;
}

export interface HealthCheckDtoBuilder {
}

export interface LoginRequest {
    usernameOrEmail: string;
    password: string;
}

export interface LoginRequestBuilder {
}

export interface RegisterRequest {
    username: string;
    email: string;
    fullName: string;
    password: string;
    role: string;
}

export interface RegisterRequestBuilder {
}

export interface UserRequest {
    username: string;
    email: string;
    fullName: string;
    role: string;
    password: string;
    active: boolean;
}

export interface UserRequestBuilder {
}

export interface UserResponse {
    id: number;
    username: string;
    email: string;
    fullName: string;
    role: string;
    active: boolean;
}

export interface UserResponseBuilder {
}
