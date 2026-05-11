export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  access_token: string;
  username: string;
}

export interface UserProfile {
  username: string;
  email: string;
  roles: string[];
  active: boolean;
}
