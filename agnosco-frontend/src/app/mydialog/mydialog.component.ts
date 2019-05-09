import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-mydialog',
  templateUrl: './mydialog.component.html',
  styleUrls: ['./mydialog.component.css']
})
export class MydialogComponent implements OnInit {

  public select = "Default";
  pName : string;

  public reconnaisseurs = [];

  /* *** CREATION D'UN PROJET *** */
  constructor(private http: HttpClient, public dialogRef: MatDialogRef<MydialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    
  }

  ngOnInit() {

    console.log("*** GET agnosco/base/availableRecogniser ***");
    
    // afficher les reconnaisseurs disponibles
    this.http.get(`agnosco/base/availableRecogniser`,{}).subscribe(returnedData => {
      console.log(returnedData);

      Object.keys(returnedData).forEach( key => {
        this.reconnaisseurs.push(returnedData[key]);
        console.log(returnedData[key]);
      });
    });
  }

  /* ANNULER: onNoClick | VALIDATION: onValidation */

  onNoClick(): void {
    console.log(this.pName);
    console.log(this.select);
    this.dialogRef.close();
  }

  onValidation(): void {
    console.log("Validation");
    console.log(this.pName);
    console.log(this.select);
    let project=[this.pName, this.select];
    this.dialogRef.close(project);
  }
}