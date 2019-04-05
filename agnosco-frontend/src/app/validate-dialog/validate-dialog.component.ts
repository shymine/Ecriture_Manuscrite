import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { projection } from '@angular/core/src/render3';

@Component({
  selector: 'app-validate-dialog',
  templateUrl: './validate-dialog.component.html',
  styleUrls: ['./validate-dialog.component.css']
})
export class ValidateDialogComponent implements OnInit {

  public select;


  constructor(private http: HttpClient, public dialogRef: MatDialogRef<ValidateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    
  }

  ngOnInit() {
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