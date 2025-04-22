import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Material, MaterialUpdateRequest, MaterialUploadRequest } from '../models/material.model';
import { environment } from 'src/environments/courenvironment';

@Injectable({
  providedIn: 'root'
})
export class MaterialService {
    private apiUrl = `${environment.apiUrl}/api/courses`;

    constructor(private http: HttpClient) { }

    // Add material to a course (with JSON data)
    addMaterial(courseId: number, material: Material): Observable<Material> {
        return this.http.post<Material>(`${this.apiUrl}/${courseId}/materials`, material);
    }

    // Update material information
    updateMaterial(courseId: number, materialId: number, updates: MaterialUpdateRequest): Observable<Material> {
        return this.http.put<Material>(`${this.apiUrl}/${courseId}/materials/${materialId}`, updates);
    }

    // Delete material from course
    deleteMaterial(courseId: number, materialId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${courseId}/materials/${materialId}`);
    }

    // Upload file for material (if you're handling file uploads)
    uploadMaterialFile(request: MaterialUploadRequest): Observable<Material> {
        const formData = new FormData();
        formData.append('title', request.title);
        formData.append('file', request.file);
        formData.append('courseId', request.courseId.toString());

        return this.http.post<Material>(`${this.apiUrl}/${request.courseId}/materials/upload`, formData);
    }

    // Get all materials for a specific course
    getMaterialsByCourse(courseId: number): Observable<Material[]> {
        return this.http.get<Material[]>(`${this.apiUrl}/${courseId}/materials`);
    }

    // Get a specific material by ID
    getMaterialById(courseId: number, materialId: number): Observable<Material> {
        return this.http.get<Material>(`${this.apiUrl}/${courseId}/materials/${materialId}`);
    }
}