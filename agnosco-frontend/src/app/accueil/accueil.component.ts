import { Component, OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { MydialogComponent } from '../mydialog/mydialog.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-accueil',
  templateUrl: './accueil.component.html',
  styleUrls: ['./accueil.component.css']
})
export class AccueilComponent implements OnInit {

  public projects;
  public maxListIndex;

  constructor(private router: Router, public dialog: MatDialog, private http: HttpClient) {
  //constructor(private router: Router, public dialog: MatDialog) {
    this.projects = [];
    this.maxListIndex = -1;
  }

  ngOnInit() {

    //test

    
    this.getAllProjects();
    

  }

  getAllProjects() {
    /**/
    this.projects[0]=["Project 1", ["Document Un", "Document Deux", "Document Trois"]];
    this.projects[1]=["Project 2", ["Document Quatre", "Document Cinq", "Document Six"]];

    this.maxListIndex = -1;
  
    console.log("*** get all projects ***");

    /*
    this.http.get(`/base/projectsAndDocuments`,{}).subscribe(returnedData => {
      console.log(returnedData);

      Object.keys(returnedData).forEach( key => {
        this.maxListIndex += 1;
        //this.projects[this.maxListIndex]= returnedData[key];
        console.log(returnedData[key]);

      });

      console.log("projects");

      
    });
    */
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(MydialogComponent, {});

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
      if(result) {
        /**/
        this.projects.push([result,[]]);
        /*
        const newlist = [];
        this.http.post(`/base/createNewProject/${project_name}/${newlist}`,{},{}).subscribe(returnedData => {
        */
      }
    });
  }

  showActions(ev){
    let el = ev.originalTarget.parentNode.parentNode.lastChild;
    if(el.hidden) {
      el.hidden = false;
    }else {
      el.hidden = true;
    }
  }

  hideActions(ev){
    let el = ev.originalTarget.lastChild;
    el.hidden = true;
  }

  showX(ev) {
    let el = ev.originalTarget.lastChild;
    el.hidden = false;
  }

  hideX(ev) {
    let el = ev.originalTarget.lastChild;
    el.hidden = true;
  }

  deletePro(p) {
    /* */
    this.projects.splice(p,1);
    let name = p; //?
    this.http.delete(`/base/deleteDocument/${name}`).subscribe(returnedData =>{
    });
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
