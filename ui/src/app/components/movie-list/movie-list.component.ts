import {Component, Inject, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule, FormControl} from '@angular/forms';
import {MovieService, Movie} from '../../services/movie.service';
import {MatTableModule} from '@angular/material/table';
import {debounceTime, distinctUntilChanged} from 'rxjs/operators';
import {MAT_DIALOG_DATA, MatDialog, MatDialogModule} from '@angular/material/dialog';

@Component({
  selector: 'movie-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, ReactiveFormsModule],
  templateUrl: './movie-list.component.html',
})
export class MovieListComponent implements OnInit {
  movies: Movie[] = [];
  totalItems: number = 0;
  currentPage: number = 1;
  totalPages: number = 0;
  pageSize: number = 10;
  dialog = inject(MatDialog);
  searchControl = new FormControl('');
  pageControl = new FormControl(this.currentPage);

  constructor(private movieService: MovieService) {
  }

  ngOnInit(): void {
    this.loadMovies("");
    this.pageControl.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(value => {
      this.currentPage = value ? value : 0;
      console.log("Page manually changed to " + value);
      this.loadMovies(this.searchControl.value || "");
    });

    this.searchControl.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(value => {
      this.currentPage = 1;
      console.log("Search text changed to " + value);
      this.loadMovies(this.searchControl.value || "");
    });
  }

  loadMovies(name: string): void {
      this.movieService.getMovies(this.currentPage - 1, this.pageSize, name).subscribe((response) => {
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
      this.pageControl.setValue(this.currentPage, {emitEvent: true});
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.pageControl.setValue(this.currentPage, {emitEvent: true});
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
      <button mat-dialog-close>Close</button>
    </mat-dialog-actions>
  `,
})
export class DialogContent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: Movie) {
  }
}

