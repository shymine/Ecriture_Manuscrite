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

  /* LISTES VISIBLES */
  public pages = []; // liste des pages candidates à ajouter dans la base de données
  public oldpages = []; // liste des pages déjà présentes dans la base de données

  public did = -1; // id du document

  constructor(private http: HttpClient, public dialogRef: MatDialogRef<GestionPagesComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    console.log("DATA");

    console.log("projet");
    console.log(data.pname);

    console.log("document");
    console.log(data.d.name);
    console.log(data.d.id);
    this.did = data.d.id;

    console.log("`agnosco/base/documentPages/${data.d.id}`");
    this.http.get(`agnosco/base/documentPages/${data.d.id}`,{}).subscribe(returnedData => {
      console.log("returned data");
      console.log(returnedData);
      Object.keys(returnedData).forEach( key => {
        console.log("key");
        console.log(returnedData[key]);
        this.oldpages.push({'nameIm':returnedData[key].imgName, 'nameVt': returnedData[key].vtName, 'cancelled': 0, 'id': returnedData[key].id}); //
      });
    });
  }

  ngOnInit() {
  }

  /* ANNULER : onNoClick | VALIDER: onValidation */

  onNoClick(): void {
    console.log("");
    this.dialogRef.close(0);
  }

  /*
  l'envoi au backend sera de la forme
  {
    json = {
      'deletedPages': [id1, id2,id3],
      'addedPages': [{'name':"nfgh", 'image64': image, 'vtText': vt}]
    }
  }
  */
  onValidation(): void {

    console.log("validation");

    let json = {'deletedPages':[], 'addedPages':[]};

    console.log("voilà ce qui va se passer!");

    // récupération des pages à supprimer de la base de données (oldpages avec cancelled:1)
    for (var _i = 0; _i < this.oldpages.length; _i++){
      if(this.oldpages[_i].cancelled == 1){
        console.log("supprimer:");
        console.log(this.oldpages[_i]);

        json.deletedPages.push(this.oldpages[_i].id);
      }
    }

    // récupération des pages à ajouter (pages)
    for (var _j = 0; _j < this.pages.length; _j++){
      console.log("ajouter:");
      console.log(this.pages[_j]);
      let p = {
        'name': this.pages[_j].name,
        'image64': this.pages[_j].image,
        'vtText' : this.pages[_j].data
      }

      json.addedPages.push(p);
    }

    console.log("recapitulons:");
    console.log(json);


    this.http.post(`agnosco/base/pagesGestion/${this.did}`,json,{}).subscribe(data => {
      this.dialogRef.close(0);
    },
    error => {
      console.log("catch error:", error.error.error);
      let answer = error.error.error + 1;
      console.log("answer:",answer);

      this.dialogRef.close(answer);
    });
  }

  /* AJOUT et SUPPRESSION des pages sur la liste des pages à enregistrer dans la  base de données */

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

  // si on souhaite supprimer une page qui n'est pas déjà présente de la base de données: on la supprimer de la liste des pages à ajouter
  deleteNew(page){
    //page = index dans pages

    console.log("DELETE NEW PAGE");
    console.log("annulation de l'ajout => suppression dans pages");

    for (var _j = page; _j < this.pages.length-1; _j++){
      this.pages[_j] = this.pages[_j+1];
    }
    this.pages.pop();
  }

  /* Boolean pour l'HTML */
  isCancelled(p){
    //p index de old
    if(this.oldpages[p].cancelled == 0){
      return false;
    }else{
      return true;
    }
  }

   /* ENCODAGE DES IMAGES ET VT LORS DE L'IMPORTATION */
   encodeImageFile(param) {

    console.log("ENCODE..IMAGE");
    console.log(param);

    const event = param[0];
    const page = param[1];

    const file = event.target.files[0];
    console.log(file)
    const reader = new FileReader();

    const pages = this.pages;

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

    console.log("page");
    console.log(page);

    const file = event.target.files[0];
    console.log(file)

    const reader = new FileReader();

    const pages = this.pages;

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
}
