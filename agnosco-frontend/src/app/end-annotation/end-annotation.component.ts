import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { projection } from '@angular/core/src/render3';

@Component({
  selector: 'app-end-annotation',
  templateUrl: './end-annotation.component.html',
  styleUrls: ['./end-annotation.component.css']
})
export class EndAnnotationComponent implements OnInit {

  public select;


  constructor(private http: HttpClient, public dialogRef: MatDialogRef<EndAnnotationComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    
  }

  ngOnInit() {
  }

  onNoClick(): void {
    console.log(this.select);
    this.dialogRef.close();
  }

  retourAccueil(): void {
    this.select = 1;
    console.log(this.select);
    this.dialogRef.close(this.select);
  }

  goToValidation(): void {
    this.select = 2;
    console.log(this.select);
    this.dialogRef.close(this.select);
  }

}