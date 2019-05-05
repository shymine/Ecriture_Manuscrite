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
  public oldpages = [];

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
        this.oldpages.push({'nameIm':returnedData[key].imgName, 'nameVt': returnedData[key].vtName, 'modified': 0, cancelled: 0}); //
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
    const tab = param[2];

    const file = event.target.files[0];
    console.log(file)
    const reader = new FileReader();
    const http = this.http;

    let pages;

    if(tab == "old"){
      pages = this.oldpages;
      this.oldpages[page].modified = 1;
    }else{
      pages = this.pages;
    }

    reader.onloadend = function() {
      let res: string = reader.result as string;
      let encoded = res.replace(/^data:(.*;base64;)?/,'');
      if((encoded.length%4)>0) {
        encoded += '='.repeat(4-(encoded.length%4));
      }

      console.log("file.name");
      console.log(file.name);

      pages[page].image = encoded;
      pages[page].name = file.name;

      
      console.log("encoded");

      console.log("pages[page]");
      console.log(pages[page]);
    }

    reader.readAsDataURL(file);
    console.log("j'encode");
  }

  encodeDataFile(param) {

    console.log("ENCODE..DATA");
    console.log(param);

    const event = param[0];
    const page = param[1];
    const tab = param[2];

    console.log("page");
    console.log(page);

    const file = event.target.files[0];
    console.log(file)

    const reader = new FileReader();
    const http = this.http;

    let pages;

    if(tab == "old"){
      pages = this.oldpages;
      this.oldpages[page].modified = 1;
    }else{
      pages = this.pages;
    }

    reader.onloadend = function() {
      let res: string = reader.result as string;

      console.log("encoded");

      pages[page].data = res;      

      console.log("pages[page]");
      console.log(pages[page]);
    }

    reader.readAsText(file);
    console.log("j'encode");
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

  /* XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX */
  onValidation(): void {

    console.log("validation");

    console.log("voilà ce qui va se passer!");

    console.log("les vieilles et leurs etats");
    console.log(this.oldpages);

    console.log(" X à ajouter X ");
    console.log(this.pages);

    /* 1) si pas à supprimer : modifier 2) supprimer 3) ajouter */

    this.dialogRef.close(1);
  }
  /* XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX */

  plusPage(){
    console.log("add one page");
    
    this.pages.push({'name':"default", 'image': "default", 'data':"default"});
    
    console.log(this.pages);
  }

  deleteOld(page){
    //page = index dans oldpages
    console.log("DELETE OLD PAGE");

    if(this.oldpages[page].cancelled == 1){
      console.log("RESTAURATION");
      this.oldpages[page].cancelled = 0;

    }else{
      console.log("SUPPRESSION");
      this.oldpages[page].cancelled = 1;
      
    }
  }

  deleteNew(page){
    //page = index dans pages

    console.log("DELETE NEW PAGE");
    console.log("annulation de l'ajout => suppression dans pages");

    for (var _j = page; _j < this.pages.length-1; _j++){
      this.pages[_j] = this.pages[_j+1];
    }
    this.pages.pop();
  }
}
