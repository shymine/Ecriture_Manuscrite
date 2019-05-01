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

  public pages = [];
  public newpages = [];
  public cancelledpages = [];
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

    if(this.isNew(page)>=0){

      const file = event.target.files[0];
      console.log(file)
      const reader = new FileReader();
      const http = this.http;
      const pages = this.pages;

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
      reader.readAsDataURL(file);
      console.log("j'encode");
    }
  }

  encodeDataFile(param) {

    console.log("ENCODE..DATA");
    console.log(param);

    const event = param[0];
    const page = param[1];

    if(this.isNew(page)>=0){

      console.log("page");
      console.log(page);

      const file = event.target.files[0];
      console.log(file)

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
/*
    const json = {
      'name': this.dName,
      'pages': this.pages
    }

    console.log(json);

    if(this.pid>=0){
      this.http.post(`agnosco/base/addDocToProject/${this.pid}`,json,{}).subscribe(data => {
        console.log("envoye");
      });
    }else{
      console.log("FAIL ID");
    }
  
*/
    this.dialogRef.close(1);
  }

  plusPage(){
    console.log("add one page");
    
    this.newpages.push(this.pages.length);
    this.pages.push({'name':"default", 'image': "default", 'data':"default"});
    
    console.log(this.pages);
  }

  deletePage(page){
    console.log("DELETE ONE PAGE");

    let n = this.isNew(page);
    console.log("N");
    console.log(n);
    console.log("length");
    console.log(this.newpages.length);
    if(n >= 0){
      console.log("annulation de l'ajout");
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
    }else{
      console.log("ajout des pages à supprimer");
      this.cancelledpages.push(page);
      console.log("objets à supprimer:");
      console.log(this.cancelledpages);
    }

    for (var _j = page; _j < this.pages.length-1; _j++){
      this.pages[_j] = this.pages[_j+1];
    }
    this.pages.pop();

    console.log("end of delete page");
    console.log(this.pages);
    
  }

  isNew(p){
    console.log("IS NEW?");
    console.log(p)
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
}
