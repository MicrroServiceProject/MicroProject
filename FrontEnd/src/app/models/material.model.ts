import { Course } from './cours.model';

export interface Material {
    id?: number;
    title: string;
    url: string;
    uploadedAt?: Date;
    course?: Course | number; // Can be either the full Course object or just the ID
}

export interface MaterialUploadRequest {
    title: string;
    file: File;
    courseId: number;
}

export interface MaterialUpdateRequest {
    title?: string;
    url?: string;
}