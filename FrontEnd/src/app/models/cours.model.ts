
export interface Course {
    id?: number;
    name: string;
    description: string;
    instructorId?: number;
    studentIds?: number[];
    materials?: Material[];
    createdAt?: Date;
    updatedAt?: Date;
  }
  
  export interface Material {
    id?: number;
    title: string;
    url: string;
    uploadedAt?: Date;
    course?: Course | number;
  }