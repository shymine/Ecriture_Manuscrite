import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MydialogComponent } from '../mydialog/mydialog.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';

@Component({
  selector: 'app-accueil',
  templateUrl: './accueil.component.html',
  styleUrls: ['./accueil.component.css']
})
export class AccueilComponent implements OnInit {

  public projects;
  public newProject;

  constructor(private router: Router, public dialog: MatDialog) {
    this.projects = [];
  }

  ngOnInit() {

    //test
    this.projects[0]=["Project 1", ["Document Un", "Document Deux", "Document Trois"]];
    this.projects[1]=["Project 2", ["Document Quatre", "Document Cinq", "Document Six"]];

    this.afficheListe();

  }

  openDialog(): void {
    const dialogRef = this.dialog.open(MydialogComponent, {
      data: {name: this.newProject}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
    });
  }

  afficheListe(){

  }

  goToDecoupe(){
    this.router.navigate(['/decoupe']);
  }

  goToAnnotation(p){
    console.log("Bouton:");
    console.log(p);
    this.router.navigate(['/annotation',{'id':JSON.stringify(p)}]);
  }

  goToValidation(p){
    this.router.navigate(['/validation',{'id':JSON.stringify(p)}]);
  }

}
