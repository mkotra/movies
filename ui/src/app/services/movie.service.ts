import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';

export interface Movie {
  id: number;
  title: string;
  year: number;
}

@Injectable({
  providedIn: 'root',
})
export class MovieService {
  private apiUrl = '/movies';
  private username = 'user1';
  private password = 'pass1';

  constructor(private http: HttpClient) {
  }

  getMovies(page: number, size: number): Observable<HttpResponse<Movie[]>> {
    // Base64 encode the username and password for Basic Auth
    const authorizationHeader = 'Basic ' + btoa(`${this.username}:${this.password}`);

    // Log the Authorization header for debugging purposes
    console.log('Authorization Header:', authorizationHeader);

    // Create HttpHeaders object with Authorization header
    const headers = new HttpHeaders().set('Authorization', authorizationHeader);

    // Send the GET request with the Authorization header and pagination params
    return this.http.get<Movie[]>(`${this.apiUrl}?page=${page}&page_size=${size}`, {headers, observe: 'response'}).pipe(
      catchError(error => {
        // Log error for debugging
        console.error('Error occurred while fetching movies:', error);
        return throwError(() => new Error('Failed to fetch movies'));
      })
    );
  }
}

