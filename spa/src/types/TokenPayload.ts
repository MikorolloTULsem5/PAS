export interface AccessToken{
    accessToken: string
}

export enum RolesEnum{
    ADMIN = "ROLE_ADMIN",
    RESADMIN = "ROLE_RESOURCE_ADMIN",
    CLIENT = "ROLE_CLIENT"
}

export interface Authority{
    authority: RolesEnum
}
export interface TokenPayload {
    sub: string;
    exp: number;
    iat: number;
    authorities: Authority[]
}