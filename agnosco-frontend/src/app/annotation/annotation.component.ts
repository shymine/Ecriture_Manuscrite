import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';
import { ValidateDialogComponent } from '../validate-dialog/validate-dialog.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { forEach } from '@angular/router/src/utils/collection';

@Component({
  selector: 'app-annotation',
  templateUrl: './annotation.component.html',
  styleUrls: ['./annotation.component.css']
})
export class AnnotationComponent implements OnInit {

  private params = [];
  private docName;
  private hidden = [];
  public validationString;

  public pages = [];
  public examples = []; //0 : path - 1 : transcription -  2 : id - 3 : enabled
  public currentPageIndex;
  public currentPage;

  public isFirstPage;
  public isLastPage;
  validation: Validation;

  constructor(private router: Router, private route: ActivatedRoute, private validationService: ValidationService, public dialog: MatDialog, private http: HttpClient) {
    this.examples = [];
    this.pages = [];
    this.currentPageIndex = 0;
    this.currentPage = 0;
    this.isFirstPage = true;
    this.isLastPage = false;
    this.validationString = "[";
  }

  ngOnInit() {
    this.route.params.forEach(param => {
      this.params.push(param.id);
    });

    this.docName = this.params[0]; // le nom du document est le 1er paramètre
    console.log("ID du document : " + this.docName);

    this.getPages();

    this.checkPageNumber();
  }

  goHome(){
    this.router.navigate(['']);
  }

  goToDecoupe(){
    this.router.navigate(['/decoupe']);
  }

  goToValidation(p){
    this.router.navigate(['/validation',{'id':JSON.stringify(p)}]);
  }

  getPages(){
    //on récupère la liste des identifiants des pages du doc passé en paramètre 
    this.validationService.getPages(this.docName).subscribe(returnedData => {
      console.log("get pages : ");
      console.log(returnedData);
      
      //on parcourt la returnedData pour ne prendre que l'image, la transcription
      Object.keys(returnedData).forEach( key => {
        let id = returnedData[key].id;

        console.log("### " + key + " / id = " + id + " ###");
        this.pages.push(id);
      });

      this.currentPage = this.pages[this.currentPageIndex];
        
      console.log("current page : " + this.currentPage);   
	
		  this.getPageData();

    });

  }

  /**
   * On récupère la liste des exemples qui composent la première page.
   * validation.getPageData() renvoie l'image de la page et les exemples, il faut donc faire un tri
   */
  getPageData(){
    this.examples = [];

    this.validationService.getPageData(this.currentPage).subscribe
    (returnedData => {
      console.log("get data : ");
      console.log(returnedData);
      
      //on parcourt la returnedData pour ne prendre que l'id des pages
      Object.keys(returnedData).forEach( key => {
        let data = returnedData[key];
        let enabled = returnedData[key].enabled;
        let id = returnedData[key].id;
        let imagePath = returnedData[key].imagePath;
        let transcript = returnedData[key].transcript;
        let validated = returnedData[key].validated;

        let newExample = [imagePath, transcript, id, enabled];

        this.examples.push(newExample);
        this.hidden.push(!enabled);
      });
    });
  }

  //méthode qui vérifie si on est à la 1e ou à la dernière page
  checkPageNumber(){
    if(this.currentPageIndex == 0){
      //on grise la 1e flèche
      this.isFirstPage = true;
    }else{
      this.isFirstPage = false;
    }

    if(this.currentPageIndex == this.pages.length - 1){
      //on grise la 2e flèche
      this.isLastPage = true;
    }else{
      this.isLastPage = false;
    }
    this.currentPage = this.pages[this.currentPageIndex];
    console.log("check page : " + this.currentPage);
  }

  isEnabled(id){
    return this.examples[id][3];
  }

  disableEx(id){
    console.log("disable " + id);
    if(this.hidden[id] == false){
      let i = this.examples[id][2];
      this.validationService.disableEx(i);
    }
    else{
      let i = this.examples[id][2];
      this.validationService.enableEx(i);
    }
    this.examples[id][3] = !this.examples[id][3];
    this.hidden[id] = !this.hidden[id];
  }

  previousPage(){
    console.log("PREVIOUS PAGE");
    this.currentPageIndex--;
    this.currentPage = this.pages[this.currentPageIndex];
    this.checkPageNumber();
    this.getPageData();

    console.log("index page : " + this.currentPageIndex);
    console.log("page number : " + this.currentPage);
  }

  nextPage(){
    console.log("NEXT PAGE");
    this.currentPageIndex++;
    this.currentPage = this.pages[this.currentPageIndex];
    this.checkPageNumber();
    this.getPageData();

    console.log("index page : " + this.currentPageIndex);
    console.log("page number : " + this.currentPage);
  }

  isCurrentPage(p){
    return (p==this.currentPageIndex+1);
  }

}
