export enum Role {
    USER = 'USER',
    ADMIN = 'ADMIN',
    ARTISTE = 'ARTISTE'
  }
  
  export interface User {
    userId: number;
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    active: boolean;
    role: Role;
  }