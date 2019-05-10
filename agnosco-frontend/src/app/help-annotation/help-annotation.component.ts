import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-help-annotation',
  templateUrl: './help-annotation.component.html',
  styleUrls: ['./help-annotation.component.css']
})
export class HelpAnnotationComponent implements OnInit {

  constructor(private http: HttpClient, public dialogRef: MatDialogRef<HelpAnnotationComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    
  }

  ngOnInit() {
  }
}