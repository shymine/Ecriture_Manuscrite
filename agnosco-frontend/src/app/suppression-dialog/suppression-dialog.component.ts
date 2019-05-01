import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { projection } from '@angular/core/src/render3';

@Component({
  selector: 'app-suppression-dialog',
  templateUrl: './suppression-dialog.component.html',
  styleUrls: ['./suppression-dialog.component.css']
})
export class SuppressionDialogComponent implements OnInit {

  public select;


  constructor(private http: HttpClient, public dialogRef: MatDialogRef<SuppressionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    console.log("data");
    console.log(data);
    console.log("support");
    console.log(data.support);
    console.log("nom");
    console.log(data.nom);
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