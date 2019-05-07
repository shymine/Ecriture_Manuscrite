import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatSelect} from '@angular/material';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-add-doc',
  templateUrl: './add-doc.component.html',
  styleUrls: ['./add-doc.component.css']
})
export class AddDocComponent implements OnInit {

  public hideMessage = true;
  public pages = [];
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

    // console.log("ENCODE..IMAGE");
    // console.log(param);

    const event = param[0];
    const page = param[1];

    const file = event.target.files[0];
    // console.log(file)
    const reader = new FileReader();
    const http = this.http;
    const pages = this.pages;
    reader.onloadend = function() {
      let res: string = reader.result as string;
      let encoded =  res.replace(/^data:(.)*(;base64,)/,'');
      if((encoded.length%4)>0) {
        encoded += '='.repeat(4-(encoded.length%4));
      }
      console.log("encoded: ",encoded)
      console.log("non encoded:", res)

      console.log("file.name");
      console.log(file.name);

      pages[page].image64 = encoded;
      pages[page].name = file.name;

      console.log("encoded");

      // console.log("pages[page]");
      // console.log(pages[page]);
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

      console.log("encoded");

      pages[page].vtText = res;

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

  onValidation(): void {

    console.log("validation");

    const json = {
      'name': this.dName,
      'pages': this.pages
    }

    console.log(json);

    if(this.id>=0){
      this.http.post(`agnosco/base/addDocToProject/${this.id}`,json,{}).subscribe(data => {
        console.log("data:"+data);
        this.dialogRef.close(1);
      },
      error => {
        console.log("catch error:", error);
        this.hideMessage = false;
      });
    }else{
      console.log("FAIL ID");
      this.dialogRef.close(0);
    }
  }

  plusPage(){
    console.log("add one page");
    this.pages.push({'name':"default", 'image64': "default", 'vtText':"default"});
    console.log(this.pages);
  }

  deletePage(page){
    console.log("DELETE ONE PAGE");

    for (var _i = page; _i < this.pages.length; _i++){
      this.pages[_i] = this.pages[_i+1];
    }
    this.pages.pop();

    console.log("end of delete page");
    console.log(this.pages);
    
  }

}
