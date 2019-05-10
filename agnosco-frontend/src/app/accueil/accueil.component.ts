import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MydialogComponent } from '../mydialog/mydialog.component';
import { SuppressionDialogComponent } from '../suppression-dialog/suppression-dialog.component';
import {MatDialog } from '@angular/material';
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
  public alertMessage = {'message':"Rien à signaler", 'hide':true};

  constructor(private router: Router, public dialog: MatDialog, private http: HttpClient) {
    this.projects = [];
    this.maxListIndex = -1;
  }

  ngOnInit() {

    this.getAllProjects();
    
  }

  /* affichage de tous les projets et docs: rangement dans this.projects */
  getAllProjects() {
  
    console.log("*** GET /base/projectsAndDocuments ***");
    this.projects = [];

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
        console.log("............");

        this.projects.push([nomPro,id,docPro]);
      });
      console.log("projects");
      console.log(this.projects);
    });
  }

  /* CREATION D'UN NOUVEAU PROJET : navigation vers my-dialog */

  openDialog(): void {
    const dialogRef = this.dialog.open(MydialogComponent, {});

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
      if(result && result[0]) {
        console.log("reconaisseur:");
        console.log(result[1]);
        console.log("*** POST agnosco/base/createNewProject/{"+result[0]+"}/{"+result[1]+"} ***");
        this.http.post(`agnosco/base/createNewProject/${result[0]}/${result[1]}`,{},{}).subscribe((data: any) => {
          console.log("created");
          this.projects.push([data.name, data.id, data.documents]);
        });
      }else{
        console.log("no name");
      }
    });
  }

  /* icones de gestions des projets/ doc/ pages: gestion des 'hidden' */

  /* icones des projets: chariot:export | plus:addDoc | croix: suppression*/
  showX(ev) {
    let elX = ev.originalTarget.children[1]; //export
    let elY = ev.originalTarget.children[2]; // addDoc
    let elZ = ev.originalTarget.children[3]; //suppression
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

  /* icones de documents: chariot: export | croix: suppression */

  showXActions(ev){
    let el = ev.originalTarget.parentNode.children[1]; // export
    let el2 = ev.originalTarget.parentNode.children[2]; // suppression
    el.hidden = false;
    el2.hidden = false;
  }

  /* affichage de la liste des actions possibles (annotation, validation, gestion des pages) */

  showActions(ev){
    let es = ev.originalTarget.parentNode.parentNode.lastChild;
    if(es.hidden) {
      es.hidden = false;
    }else {
      es.hidden = true;
    }
  }

  hideActions(ev){
    // lorsque le pointeurs quitte l'espace du document, les actions sont à nous cachées
    let el = ev.originalTarget.lastChild; // liste des actions (annotation, validation, gestion des pages)
    let es = ev.originalTarget.firstChild.children[1]; // export
    let es2 = ev.originalTarget.firstChild.children[2]; // suppression
    el.hidden = true;
    es.hidden = true;
    es2.hidden = true;
  }

  /* NAVIGATION: actions sur les documents: annotation , validation, gestion des pages, suppression, exportation */

  goToAnnotation(d,p){
    console.log("annotation");
    console.log("document: "+d.id);
    console.log("projet: "+p[1]);
    console.log(p);
    console.log(d);
    this.router.navigate(['/annotation',{'idd':d.id, 'named':d.name, 'prepared': d.prepared, 'idp':p[1], 'namep': p[0]}]);
  }

  goToValidation(d){
    console.log("VALIDATION");
    console.log("document: "+d.id);
    this.router.navigate(['/validation',{'idd':d.id, 'named': d.name, 'prepared': d.prepared}]);
  }

  goToGestionPages(p,d){
    console.log("gestion Pages");
    console.log(p);
    console.log(d);
    
    const dialogRef = this.dialog.open(GestionPagesComponent, {
      data: {
        'pid': p.id,
        'pname': p.name,
        'd' : d
      },
      width: '70%'
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log("*ACCUEIL*");
      console.log("result:",result);
      if(result != undefined){
        switch(result){
          case -1:
            this.alertMessage.message = ">> Erreur: l'identifiant du projet est introuvable <<";
            this.alertMessage.hide = false;
            break;
          case 0:
            // tout est ok
            break;
          case 1:
            this.alertMessage.message = ">> Une ou plusieurs pages n'ont pas pu être ajoutées au document: 'format de vérité terrain incorrect' <<";
            this.alertMessage.hide = false;
            break;
          case 2:
            this.alertMessage.message = ">> Une ou plusieurs pages n'ont pas pu être ajoutées au document: 'image et vérité terrain incompatibles' <<";
            this.alertMessage.hide = false;
            break;
          case 3:
            this.alertMessage.message = ">> Une ou plusieurs pages n'ont pas pu être ajoutées au document: 'format de vérité terrain incorrect + image et vérité terrain incompatibles' <<";
            this.alertMessage.hide=false;
            break;
          default:
            this.alertMessage.message = ">> Erreur non traitée <<";
            this.alertMessage.hide=false;
        }
      }
    });
  }

  //ajout un document
  addDoc(p){
    console.log("addDoc");
    console.log(p);
    
    const dialogRef = this.dialog.open(AddDocComponent, {
      data: {'id': p[1], "pname": p[0]},
      width: '70%'
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log("result:",result);
      if(result != undefined){
        switch(result){
          case -1:
            this.alertMessage.message = ">> Erreur: l'identifiant du projet est introuvable <<";
            this.alertMessage.hide = false;
            break;
          case 0:
            // tout est ok
            break;
          case 1:
            this.alertMessage.message = ">> Une ou plusieurs pages n'ont pas pu être ajoutées au document: 'format de vérité terrain incorrect' <<";
            this.alertMessage.hide = false;
            break;
          case 2:
            this.alertMessage.message = ">> Une ou plusieurs pages n'ont pas pu être ajoutées au document: 'image et vérité terrain incompatibles' <<";
            this.alertMessage.hide = false;
            break;
          case 3:
            this.alertMessage.message = ">> Une ou plusieurs pages n'ont pas pu être ajoutées au document: 'format de vérité terrain incorrect + image et vérité terrain incompatibles' <<";
            this.alertMessage.hide=false;
            break;
          default:
            this.alertMessage.message = ">> Erreur non traitée <<";
            this.alertMessage.hide=false;
        }
        this.getAllProjects();
      }
    });
  }
  
  // gère les erreurs d'importation de pages: voir addDoc ou gestionPages 
  fermerAlert(){
    this.alertMessage.hide = true;
    this.alertMessage.message = "Rien à signaler";
  }

  //  supprime un projet et les documents qu'il contient
  deletePro(p) {
    console.log("//////////////////////////");
    console.log(p);

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
  }

  // supprime un document
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
  
  //exporte un projet (plusieurs documents)
  exportP(p){
    console.log("export");
    console.log(p);

    const dialogRef = this.dialog.open(ExportProjetComponent, {
      data: {'id': p[1], "pname": p[0], 'support': "pro"}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log("*ACCUEIL*");
      this.getAllProjects();
    });

  }

  //exporte un document
  exportD(d){
    console.log("export");
    console.log(d);

    const dialogRef = this.dialog.open(ExportProjetComponent, {
      data: {'id': d.id, 'pname': d.name, 'support': "doc"}
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getAllProjects();
    });
  }

}
