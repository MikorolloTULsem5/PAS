export interface UserType {
    archive:boolean,
    id:string,
    login:string
    userType: "Client" | "Admin" | "Resource admin" | null
}

export interface ClientType extends UserType{
    clientTypeName: string,
    firstName: string,
    lastName: string
}