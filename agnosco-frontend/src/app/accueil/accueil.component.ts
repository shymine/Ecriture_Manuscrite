import { Component, OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { MydialogComponent } from '../mydialog/mydialog.component';
import { SuppressionDialogComponent } from '../suppression-dialog/suppression-dialog.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { AddDocComponent } from '../add-doc/add-doc.component';
import { GestionPagesComponent } from '../gestion-pages/gestion-pages.component';
import { ExportProjetComponent } from '../export-projet/export-projet.component';

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


  // encodeImageFileAsURL(event: any) {
  //   var file = event.target.files[0];
  
  //   var reader = new FileReader();
  //   var http = this.http;
  //   reader.onloadend = function() {
  //       let encoded = (reader.result as string).replace(/^data:(.)*(;base64,)/,'');
  //       if ((encoded.length % 4) > 0) {
  //         encoded += '='.repeat(4 - (encoded.length % 4));
  //       }
  //     console.log('RESULT', encoded)
  //     http.post(`agnosco/base/test`, {"test":encoded}).subscribe(data => console.log(data))
  //   }
  //   reader.readAsDataURL(file);
  // }

  getAllProjects() {

    /*
    this.projects[0]=["Project 1",id, ["Document Un", "Document Deux", "Document Trois"]];
    this.projects[1]=["Project 2",id, ["Document Quatre", "Document Cinq", "Document Six"]];
*/
    //this.maxListIndex = -1;
  
    console.log("*** GET /base/projectsAndDocuments ***");
    this.projects = [];

    /**/
    this.http.get(`agnosco/base/projectsAndDocuments`,{}).subscribe(returnedData => {
      console.log(returnedData);

      Object.keys(returnedData).forEach( key => {
        let data = returnedData[key];
        let nomPro = returnedData[key].name;
        let docPro = returnedData[key].documents;
        let id = returnedData[key].id;
        let reco = returnedData[key].recogniser;

        console.log(".............");
        console.log("suivant:");
        console.log(data);
        console.log("nom:");
        console.log(nomPro);
        console.log("id");
        console.log(id);
        console.log("doc");
        console.log(docPro);
        console.log("recogniser");
        console.log(reco);
        console.log(".............");

        this.projects.push([nomPro,id,docPro]);

      });

      console.log("projects");
      console.log(this.projects);

      
    });
    
  }

  deletePro(p) {
    /* */
    console.log("//////////////////////////");
    console.log(p);

    //console.log("Delete all documents from "+ this.projects[p][0]);

    const dialogRef = this.dialog.open(SuppressionDialogComponent, {
      data : {
        'support': "projet",
        'nom' : p[0]
      }
    });

    dialogRef.afterClosed().subscribe(result => {

      if(result){
        this.http.delete(`agnosco/base/deleteProject/${p[1]}`).subscribe(returnedData =>{
          this.getAllProjects();
        });
      }
    });

    //this.projects.splice(p,1);
  }

  deleteDoc(doc) {

    console.log("*** DELETE `agnosco/base/deleteDocument/{" + doc.id +"} ***");
    console.log("doc name:" + doc.name);

    const dialogRef = this.dialog.open(SuppressionDialogComponent, {
      data: {
        'support':"document",
        'nom': doc.name
      }
    });

    dialogRef.afterClosed().subscribe(result => {

      if(result){
        this.http.delete(`agnosco/base/deleteDocument/${doc.id}`).subscribe(returnedData =>{
          this.getAllProjects();
        });
      }
    });
    
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(MydialogComponent, {});

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
      if(result && result[0]) {
        /**/
        // this.projects.push([result[0],result[1],[]]);
        console.log("reconaisseur:");
        console.log(result[1]);
        console.log("*** POST agnosco/base/createNewProject/{"+result[0]+"}/{"+result[1]+"} ***");
        this.http.post(`agnosco/base/createNewProject/${result[0]}/${result[1]}`,{},{}).subscribe((data: any) => {
          console.log("created");
          this.projects.push([data.name, data.id, data.documents]);
        });
        // this.getAllProjects();
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
    let elX = ev.originalTarget.children[1];
    let elY = ev.originalTarget.children[2];
    let elZ = ev.originalTarget.children[3];
    elX.hidden = false;
    elY.hidden = false;
    elZ.hidden = false;
  }

  hideX(ev) {
    let elX = ev.originalTarget.children[1];
    let elY = ev.originalTarget.children[2];
    let elZ = ev.originalTarget.children[3];
    elX.hidden = true;
    elY.hidden = true;
    elZ.hidden = true;
  }

  goToDecoupe(){
    this.router.navigate(['/decoupe']);
  }

  goToAnnotation(p){
    console.log("Bouton:");
    console.log(p);
    this.router.navigate(['/annotation',{'id':JSON.stringify(p)}]);
  }

  goToValidation(d){
    console.log("VALIDATION");
    console.log(d);
    this.router.navigate(['/validation',{'id':JSON.stringify(d)}]);
  }

  goToGestionPages(p,d){
    console.log("gestion Pages");
    console.log(p);
    console.log(d);
    
    const dialogRef = this.dialog.open(GestionPagesComponent, {
      data: {
        'pid': p[1],
        'pname': p[0],
        'd' : d
      }
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  addDoc(p){
    console.log("addDoc");
    console.log(p);
    
    const dialogRef = this.dialog.open(AddDocComponent, {
      data: {'id': p[1], "pname": p[0]}
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getAllProjects();
    });
  }
  
  export(p){
    console.log("export");
    console.log(p);

    const dialogRef = this.dialog.open(ExportProjetComponent, {
      data: {'id': p[1], "pname": p[0]}
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getAllProjects();
    });

  }

}
