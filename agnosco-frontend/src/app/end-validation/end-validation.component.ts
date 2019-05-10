import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-end-validation',
  templateUrl: './end-validation.component.html',
  styleUrls: ['./end-validation.component.css']
})
export class EndValidationComponent implements OnInit {

  public select;


  constructor(private http: HttpClient, public dialogRef: MatDialogRef<EndValidationComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    
  }

  ngOnInit() {
    console.log("dialog ouvert");
  }

  onNoClick(): void {
    console.log(this.select);
    this.dialogRef.close();
  }

  onValidation(): void {
    this.select = true;
    console.log(this.select);
    this.dialogRef.close(this.select);
  }

}