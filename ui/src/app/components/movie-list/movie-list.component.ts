import {Component, Inject, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MovieService, Movie} from '../../services/movie.service';
import {MatTableModule} from '@angular/material/table';
import {MAT_DIALOG_DATA, MatDialog, MatDialogModule} from '@angular/material/dialog';

@Component({
  selector: 'movie-list',
  standalone: true,
  imports: [CommonModule, MatTableModule],
  templateUrl: './movie-list.component.html',
})
export class MovieListComponent implements OnInit {
  movies: Movie[] = [];
  totalItems: number = 0;
  currentPage: number = 1;
  totalPages: number = 0;
  pageSize: number = 10;
  dialog = inject(MatDialog);

  constructor(private movieService: MovieService) {
  }

  ngOnInit(): void {
    this.loadMovies();
  }

  loadMovies(): void {
    this.movieService.getMovies(this.currentPage - 1, this.pageSize).subscribe((response) => {
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

  loadMovie(id: number) {
    this.movieService.getMovie(id).subscribe((response) => {

      this.dialog.open(DialogContent, {
        data: response.body,
        width: '400px',
      });
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

@Component({
  imports: [MatDialogModule],
  selector: 'dialog-content',
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p><strong>Year:</strong> {{ data.year }}</p>
      <p><strong>ID:</strong> {{ data.id }}</p>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button mat-dialog-close>Close</button>
    </mat-dialog-actions>
  `,
})
export class DialogContent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: Movie) {}
}

