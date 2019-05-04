import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { isLoweredSymbol } from '@angular/compiler';

@Component({
  selector: 'app-gestion-pages',
  templateUrl: './gestion-pages.component.html',
  styleUrls: ['./gestion-pages.component.css']
})
export class GestionPagesComponent implements OnInit {

  /* 4 tableaux:
  . pages: les pages visibles sur l'IHM (manipulables)
  4 manipulations possibles:
    - Ajouter une pages (ok sous reserve de tests)
    - Supprimer une page (ok sous reserve de tests)
    - Modifier une page (que les nouvelles)
    - Restaurer une page (pas fait)

  . newpages: les pages ajoutées depuis le début de la manipulation

  . cancelledpages: les pages présentes dans la base de données et demandant à être supprimées
  en effet, seules les pages déjà intégrées à la base de données impliqueront une action de suppression
  si, lors de la manipulation, une page est ajoutée puis supprimée alors cette page ne sera tout simplement pas ajouté à la base de donnée.
  cette page n'apparait plus dans le tableau de pages à manipuler

  .modifypages: notifier les pages étant modifiées pour les modifier ensuite dans la base de données
  
  */

  public pages = [];
  public newpages = [];
  public cancelledpages = [];
  public modifypages = [];
  public pid = -1;
  public did = -1;

  dName : string;

  constructor(private http: HttpClient, public dialogRef: MatDialogRef<GestionPagesComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    console.log("DATA");

    console.log("projet");
    console.log(data.pname);
    console.log(data.pid);

    console.log("document");
    console.log(data.d.name);
    console.log(data.d.id);

    console.log("`agnosco/base/documentPages/${data.d.id}`");
    this.http.get(`agnosco/base/documentPages/${data.d.id}`,{}).subscribe(returnedData => {
      console.log("returned data");
      console.log(returnedData);
      Object.keys(returnedData).forEach( key => {
        console.log("key");
        console.log(returnedData[key]);
      });
    });
  }

  ngOnInit() {
  }

  encodeImageFile(param) {

    console.log("ENCODE..IMAGE");
    console.log(param);

    const event = param[0];
    const page = param[1];

    const file = event.target.files[0];
    console.log(file)
    const reader = new FileReader();
    const http = this.http;
    const pages = this.pages;

    const isM = this.isModified(page);

    reader.onloadend = function() {
      let res: string = reader.result as string;
      let encoded = res.replace(/^data:(.*;base64;)?/,'');
      if((encoded.length%4)>0) {
        encoded += '='.repeat(4-(encoded.length%4));
      }

      pages[page].image = encoded;

      
      console.log("encoded");

      console.log("pages[page]");
      console.log(pages[page]);
    }

    if(isM >= 0){
      this.modifypages.push(isM);
    }

    reader.readAsDataURL(file);
    console.log("j'encode");
    
  }

  encodeDataFile(param) {

    console.log("ENCODE..DATA");
    console.log(param);

    const event = param[0];
    const page = param[1];

    console.log("page");
    console.log(page);

    const file = event.target.files[0];
    console.log(file)

    const isM = this.isModified(page);

    const reader = new FileReader();
    const http = this.http;
    const pages = this.pages;

    reader.onloadend = function() {
      let res: string = reader.result as string;

      console.log("encoded");

      console.log("file.name");
      console.log(file.name);

      pages[page].data = res;
      pages[page].name = file.name;

      

      console.log("pages[page]");
      console.log(pages[page]);
    }
    reader.readAsText(file);
    console.log("j'encode");

    if(isM >= 0){
      this.modifypages.push(isM);
    }
  }

  onNoClick(): void {
    console.log("");
    this.dialogRef.close(0);
  }
  /*
  pages sont de la forme:
  {
    'nameVT' : nvt,
    'fichierImage' : imb64,
    'fichierVT' : vt
  }
  */

  onValidation(): void {

    console.log("validation");

    /* 1) si pas à supprimer : modifier 2) supprimer 3) ajouter */

    this.dialogRef.close(1);
  }

  plusPage(){
    console.log("add one page");
    
    this.newpages.push(this.pages.length);
    this.pages.push({'name':"default", 'image': "default", 'data':"default"});
    
    console.log(this.pages);
  }

  // page = index dans pages
  deletePage(page){
    console.log("DELETE ONE PAGE");

    let m = this.isCancelled(page);

    console.log("M");
    console.log(m);

    if(m >= 0){

      console.log("Actually.. Restaure it");
      this.restaurer(m);

    }else{

      let n = this.isNew(page);
      console.log("N");
      console.log(n);

      /* La page vient juste d'être ajoutée => on la supprime */
      if(n >= 0){
        this.annulationAjout(n);
        //et suppression dans pages
        for (var _j = page; _j < this.pages.length-1; _j++){
          this.pages[_j] = this.pages[_j+1];
        }
        this.pages.pop();

      /* la page est ancienne => on la rajoute aux pages à supprimer
      /!\ on ne les retire pas de la liste, on les grise (peuvent etre recpérées)
      puisque non supprimées: pas besoin de décaler les indices dans news */
      }else{
        console.log("ajout des pages à supprimer");
        this.cancelledpages.push(page);
        console.log("objets à supprimer:");
        console.log(this.cancelledpages);
      }

    }

    console.log("end of delete page");
    console.log("pages");
    console.log(this.pages);
    console.log("news");
    console.log(this.newpages);
    console.log("cancelled");
    console.log(this.cancelledpages);
    
  }

/* p est l'index de la liste des pages */

  //retirer de cancelledpages
  restaurer(n){
    console.log("annulation de la suppression");
    console.log("page: "+n);

    for (var _i = n; _i < this.cancelledpages.length-1; _i++){
      console.log("_i");
      console.log(_i);
      console.log("avant: "+this.cancelledpages[_i]);
      console.log("mtn: "+this.cancelledpages[_i+1]);
      this.cancelledpages[_i] = this.cancelledpages[_i+1];
    }
    this.cancelledpages.pop();
    console.log("objets à ajouter:");
    console.log(this.cancelledpages);
  }

  annulationAjout(n){
    console.log("annulation de l'ajout");
    console.log("page: "+n);

    for (var _i = n; _i < this.newpages.length-1; _i++){
      console.log("_i");
      console.log(_i);
      console.log("avant: "+this.newpages[_i]);
      console.log("normalement: "+this.newpages[_i+1]);
      this.newpages[_i] = this.newpages[_i+1]-1;
    }
    this.newpages.pop();
    console.log("objets à ajouter:");
    console.log(this.newpages);
  }

  //retourne son index dans newpages sinon -1
  isNew(p){
    console.log("IS NEW?");
    console.log("p: "+p);
    console.log("les news");
    console.log(this.newpages);
    for(var _i = 0; _i < this.newpages.length ; _i++){
      if(this.newpages[_i] == p){
        console.log(_i);
        return _i;
      }
    }
    console.log(-1);
    return -1;
  }

  //retourne son index dans cancelledpages sinon -1
  isCancelled(p){
    console.log("IS CANCELLED?");
    console.log("p: "+p);
    console.log("les cancelled");
    console.log(this.cancelledpages);
    for(var _i = 0; _i < this.cancelledpages.length ; _i++){
      if(this.cancelledpages[_i] == p){
        console.log(_i);
        return _i;
      }
    }
    console.log(-1);
    return -1;
  }

  //retourne son index dans modifypages sinon -1
  isModified(p){
    console.log("IS MODIFIED?");
    console.log("p: "+p);
    console.log("les modified");
    console.log(this.modifypages);
    for(var _i = 0; _i < this.modifypages.length ; _i++){
      if(this.modifypages[_i] == p){
        console.log(_i);
        return _i;
      }
    }
    console.log(-1);
    return -1;
  }
}
