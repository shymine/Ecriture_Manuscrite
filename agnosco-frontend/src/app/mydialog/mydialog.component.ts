import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { projection } from '@angular/core/src/render3';

@Component({
  selector: 'app-mydialog',
  templateUrl: './mydialog.component.html',
  styleUrls: ['./mydialog.component.css']
})
export class MydialogComponent implements OnInit {

  public select;
  popo : string;

  public reconnaisseurs = [];


  constructor(private http: HttpClient, public dialogRef: MatDialogRef<MydialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    
  }

  ngOnInit() {
    /* */
    console.log("*** GET agnosco/base/availableRecogniser ***");
    //this.reconnaisseurs.push("rec 1");
    //this.reconnaisseurs.push("rec 2");
    //this.reconnaisseurs.push("rec 3");

    
    this.http.get(`agnosco/base/availableRecogniser`,{}).subscribe(returnedData => {
      console.log(returnedData);

      Object.keys(returnedData).forEach( key => {
        this.reconnaisseurs.push(returnedData[key]);
        console.log(returnedData[key]);
      });
    });
  }

  onNoClick(): void {
    console.log(this.popo);
    console.log(this.select);
    this.dialogRef.close();
  }

  onValidation(): void {
    console.log(this.popo);
    console.log(this.select);
    let project=[this.popo, this.select];
    this.dialogRef.close(project);
  }

}