import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { projection } from '@angular/core/src/render3';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-doc',
  templateUrl: './add-doc.component.html',
  styleUrls: ['./add-doc.component.css']
})
export class AddDocComponent implements OnInit {

  public pages: [string,string][] = [];
  public id = -1;
  public params = [];

  dName : string;

  constructor(private http: HttpClient, public dialogRef: MatDialogRef<AddDocComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    console.log("data");
    console.log(data.id);

    this.id = data.id;

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
    reader.onloadend = function() {
      let res: string = reader.result as string;
      let encoded = res.replace(/^data:(.*;base64;)?/,'');
      if((encoded.length%4)>0) {
        encoded += '='.repeat(4-(encoded.length%4));
      }

      console.log("pages[page][0]");
      console.log(pages[page][0]);

      console.log("pages[page][1]");
      console.log(pages[page][1]);

      pages[page][0] = encoded;

      console.log("AFTER");

      console.log("pages[page][0]");
      console.log(pages[page][0]);

      console.log("pages[page][1]");
      console.log(pages[page][1]);
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
    const http = this.http;
    const pages = this.pages;
    reader.onloadend = function() {
      let res: string = reader.result as string;
      let encoded = res.replace(/^data:(.*;base64;)?/,'');
      if((encoded.length%4)>0) {
        encoded += '='.repeat(4-(encoded.length%4));
      }

      console.log("ENCODED");
      console.log(encoded);

      console.log("pages[page][0]");
      console.log(pages[page][0]);

      console.log("pages[page][1]");
      console.log(pages[page][1]);

      pages[page][1] = encoded;

      console.log("AFTER");

      console.log("pages[page][0]");
      console.log(pages[page][0]);

      console.log("pages[page][1]");
      console.log(pages[page][1]);
    }
    reader.readAsDataURL(file);
    console.log("j'encode");
  }

  onNoClick(): void {
    console.log("");
    this.dialogRef.close(0);
  }

  onValidation(): void {

    console.log("validation");

    const json = {
      'name': this.dName,
      'pages': this.pages
    }

    console.log(json);

    if(this.id>=0){
      this.http.post(`agnosco/base/addDocToProject/${this.id}`,json,{}).subscribe(data => {
        console.log("envoye");
      });
    }else{
      console.log("FAIL ID");
    }
  

    this.dialogRef.close(1);
  }

  plusPage(){
    console.log("add one page");
    this.pages.push(["0","0"]);
    console.log(this.pages);
    }
  /* const json = {
    'name': "coucou",
    'prepared': false,
    'pages': []
  } */

}
