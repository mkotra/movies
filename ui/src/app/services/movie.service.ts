import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
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

  getMovies(page: number, size: number, name: string): Observable<HttpResponse<Movie[]>> {
    const params = new HttpParams().append("page", page).append('page_size', size).append("name", name)
    const headers = this.authHeaders();

    return this.http.get<Movie[]>(`${this.apiUrl}`, {params, headers, observe: 'response'}).pipe(
      catchError(error => {
        console.error('Error occurred while fetching movies:', error);
        return throwError(() => new Error('Failed to fetch movies'));
      })
    );
  }

  getMovie(id: number): Observable<HttpResponse<Movie>> {
    const headers = this.authHeaders();

    return this.http.get<Movie>(`${this.apiUrl}/${id}`, {headers, observe: 'response'}).pipe(
      catchError(error => {
        console.error('Error occurred while fetching movie details:', error);
        return throwError(() => new Error('Failed to fetch movie details'));
      })
    );
  }

  authHeaders(): HttpHeaders {
    const authorizationHeader = 'Basic ' + btoa(`${this.username}:${this.password}`);
    return  new HttpHeaders().set('Authorization', authorizationHeader);
  }
}

