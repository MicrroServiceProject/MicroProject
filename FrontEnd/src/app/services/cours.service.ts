import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course, Material } from '../models/cours.model';
import { environment } from 'src/environments/courenvironment';

@Injectable({
  providedIn: 'root'
})
export class CoursService {
  private apiUrl = `${environment.apiUrl}/api/courses`;

  constructor(private http: HttpClient) { }

  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/${id}`);
  }

  addCourse(course: Course): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, course);
  }

  updateCourse(id: number, course: Course): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}`, course);
  }

  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  assignInstructorToCourse(courseId: number, instructorId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${courseId}/instructor/${instructorId}`, {});
  }

  assignInstructorToMultipleCourses(instructorId: number, courseIds: number[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/instructor/${instructorId}/assign`, courseIds);
  }

  searchCourses(keyword: string): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/search`, { params: { keyword } });
  }

  addMaterialToCourse(courseId: number, material: Material): Observable<Material> {
    return this.http.post<Material>(`${this.apiUrl}/${courseId}/materials`, material);
  }

  updateMaterial(courseId: number, materialId: number, material: Material): Observable<Material> {
    return this.http.put<Material>(`${this.apiUrl}/${courseId}/materials/${materialId}`, material);
  }

  removeMaterialFromCourse(courseId: number, materialId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${courseId}/materials/${materialId}`);
  }
}