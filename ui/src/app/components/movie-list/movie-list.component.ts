import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MovieService, Movie} from '../../services/movie.service';
import {MatTableModule} from '@angular/material/table';
import {NgxPaginationModule} from 'ngx-pagination';

@Component({
  selector: 'movie-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, NgxPaginationModule],
  templateUrl: './movie-list.component.html',
})
export class MovieListComponent implements OnInit {
  movies: Movie[] = [];
  totalItems: number = 0;
  currentPage: number = 1;
  totalPages: number = 0;
  pageSize: number = 10;

  constructor(private movieService: MovieService) {
  }

  ngOnInit(): void {
    this.loadMovies();
  }

  loadMovies(): void {
    this.movieService.getMovies(this.currentPage - 1 , this.pageSize).subscribe((response) => {
      this.movies = response.body ? response.body : [];

      const totalSizeHeader = response.headers.get('X-Total-Size');
      this.totalItems = totalSizeHeader ? Number(totalSizeHeader) : 0;

      const totalPagesHeader = response.headers.get('X-Total-Pages');
      this.totalPages = totalPagesHeader ? Number(totalPagesHeader) : 0;

      console.log("Total: " + this.totalItems);
      console.log("Current Page: " + this.currentPage);
      console.log("Page Size: " + this.pageSize);
    });
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadMovies();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadMovies();
    }
  }
}

