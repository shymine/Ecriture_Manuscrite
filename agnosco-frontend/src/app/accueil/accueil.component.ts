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

    /*
    this.projects[0]=["Project 1", ["Document Un", "Document Deux", "Document Trois"]];
    this.projects[1]=["Project 2", ["Document Quatre", "Document Cinq", "Document Six"]];
*/
    //this.maxListIndex = -1;
  
    console.log("*** GET /base/projectsAndDocuments ***");

    /**/
    this.http.get(`agnosco/base/projectsAndDocuments`,{}).subscribe(returnedData => {
      console.log(returnedData);

      Object.keys(returnedData).forEach( key => {
        //this.maxListIndex += 1;
        let data = returnedData[key];
        let nomPro = returnedData[key].name;
        let docPro = returnedData[key].documents;

        console.log("suivant:");
        console.log(data);
        console.log("nom:");
        console.log(nomPro);
        console.log("doc");
        console.log(docPro);

        this.projects.push([nomPro,docPro]);

      });

      console.log("projects");
      console.log(this.projects);

      
    });
    
  }

  deletePro(p) {
    /* */
    console.log("Delete all documents from "+ this.projects[p][0]);

    
    
    this.projects[p][1].forEach(element => {
      console.log("*** DELETE /base/deleteDocument/{" + element.name +"} ***");/*
      this.http.delete(`/base/deleteDocument/${element}`).subscribe(returnedData =>{
        this.getAllProjects();
      });*/
    });
    

    this.projects.splice(p,1);
  }

  deleteDoc(doc) {
    /* */

    console.log("*** DELETE /base/deleteDocument/{" + doc +"} ***");
    /*
    this.http.delete(`/base/deleteDocument/${doc}`).subscribe(returnedData =>{
      this.getAllProjects();
    });
    */
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(MydialogComponent, {});

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
      if(result && result[0]) {
        /**/
        this.projects.push([result[0],[]]);
        console.log("*** POST /base/createNewProject/{"+result[0]+"}/{[]} ***");
        /*
        const newlist = [];
        this.http.post(`/base/createNewProject/${result[0]}/${newlist}`,{},{}).subscribe(returnedData => {
          this.getAllProjects();
        });
        */
      }else{
        console.log("no name");
      }
    });
  }

  showActions(ev){
    let el = ev.originalTarget.parentNode.parentNode.lastChild;
    let es = ev.originalTarget.parentNode.children[1];
    if(el.hidden) {
      el.hidden = false;
    }else {
      el.hidden = true;
    }
    if(es.hidden) {
      es.hidden = false;
    }else {
      es.hidden = true;
    }
  }

  hideActions(ev){
    let el = ev.originalTarget.lastChild;
    let es = ev.originalTarget.firstChild.children[1];
    el.hidden = true;
    es.hidden = true;
  }

  showX(ev) {
    let el = ev.originalTarget.lastChild;
    el.hidden = false;
  }

  hideX(ev) {
    let el = ev.originalTarget.lastChild;
    el.hidden = true;
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
