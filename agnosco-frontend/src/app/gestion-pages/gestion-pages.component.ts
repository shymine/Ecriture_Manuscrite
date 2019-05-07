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

  public hideMessage = true;
  public pages = [];
  public oldpages = [];

  public did = -1;

  dName : string;

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

  onNoClick(): void {
    console.log("");
    this.dialogRef.close(0);
  }
  /*
  pages sont de la forme:
  {
    'name' : name,
    'image64' : image,
    'vtText' : data
  }
  */

  /* XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX */
  onValidation(): void {

    console.log("validation");

    console.log("voilà ce qui va se passer!");

    for (var _i = 0; _i < this.oldpages.length; _i++){
      if(this.oldpages[_i].cancelled == 1){
        console.log("supprimer:");
        console.log(this.oldpages[_i]);
        this.http.delete(`agnosco/base/deleteDocument/${this.oldpages[_i].id}`).subscribe(returnedData =>{
        });
      }
    }

    for (var _j = 0; _j < this.pages.length; _j++){
      console.log("ajouter:");
      console.log(this.pages[_j]);
      let json = {
        'name': this.pages[_j].name,
        'image64': this.pages[_j].image,
        'vtText' : this.pages[_j].data
      }
      this.http.post(`agnosco/base/addPageToDocument/${this.did}`,json,{}).subscribe(data => {
        console.log("data: "+data);
      });
    }

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

  isCancelled(p){
    //p index de old
    console.log("isCancelled");
    console.log(this.oldpages[p]);
    if(this.oldpages[p].cancelled == 0){
      return false;
    }else{
      return true;
    }
  }
}
