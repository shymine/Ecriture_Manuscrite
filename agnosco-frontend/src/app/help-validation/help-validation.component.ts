import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-help-validation',
  templateUrl: './help-validation.component.html',
  styleUrls: ['./help-validation.component.css']
})
export class HelpValidationComponent implements OnInit {

  constructor(private http: HttpClient, public dialogRef: MatDialogRef<HelpValidationComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    
  }

  ngOnInit() {
  }
}