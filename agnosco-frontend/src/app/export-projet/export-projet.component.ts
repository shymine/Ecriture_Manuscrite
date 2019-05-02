import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-export-projet',
  templateUrl: './export-projet.component.html',
  styleUrls: ['./export-projet.component.css']
})
export class ExportProjetComponent implements OnInit {

  public isFinished = false;

  constructor(private http: HttpClient, public dialogRef: MatDialogRef<ExportProjetComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
      console.log("data");
      console.log(data);

      this.http.post(`agnosco/base/exportProject/${data.id}`,{},{}).subscribe(data => {
      console.log("returned data");

      if(data != null){
        this.isFinished = true;
      }
      });
      
      

  }

  ngOnInit() {
  }

}
