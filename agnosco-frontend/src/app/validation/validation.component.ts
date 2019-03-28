import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';
import { MydialogComponent } from '../mydialog/mydialog.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { forEach } from '@angular/router/src/utils/collection';

@Component({
  selector: 'app-validation',
  templateUrl: './validation.component.html',
  providers: [ ValidationService ],
  styleUrls: ['./validation.component.css']
})
export class ValidationComponent implements OnInit {

  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) { 
    if(event.keyCode == 13){
      this.validateAll();
      this.nextPage();
    }
  }

  private params = [];
  private docName;
  private hidden = [false, false, false, false, false, false];

  public pages = [];
  public examples = [];
  public currentPageIndex;
  public currentPage;

  public isFirstPage;
  public isLastPage;


  pageD : any[];

  validation: Validation;

  constructor(private router: Router, private route: ActivatedRoute, private validationService: ValidationService, public dialog: MatDialog, private http: HttpClient) {
    this.examples = [];
    this.pages = [];
    this.currentPageIndex = 0;
    this.currentPage = 0;
    this.isFirstPage = true;
    this.isLastPage = false;
  }

  ngOnInit() {
    this.route.params.forEach(param => {
      this.params.push(param.id);
    });

    this.docName = this.params[0]; // le nom du document est le 1er paramètre
    console.log("ID du document : " + this.docName);

    //test

    this.examples[0] = ["../../assets/images/Elephant.jpg", "To be or not to be", 0, "cross"];
    this.examples[1] = ["../../assets/images/Fraise.png", "That is the question", 1, "cross"];
    this.examples[2] = ["../../assets/images/Elephant.jpg", "Whether 'tis nobler in the mind", 2, "cross"];
    this.examples[3] = ["../../assets/images/Fraise.png", "To suffer the slings and arrows of outrageous fortune", 3, "cross"];
    this.examples[4] = ["../../assets/images/Elephant.jpg", "Or to take arms against a sea of troubles", 4, "cross"];
    this.examples[5] = ["../../assets/images/Fraise.png", "And by opposing end them.", 5, "cross"];

    
    //on récupère la liste des identifiants des pages du doc passé en paramètre 
    this.validationService.getPages(this.docName).subscribe(returnedData => {
      console.log("get pages : ");
      console.log(returnedData);

      //on parcourt la returnedData pour ne prendre que l'id des pages
      Object.keys(returnedData).forEach( key => {
        //let data = returnedData[key];
        //let pageExamples = returnedData[key].examples;
        //let groundTruth = returnedData[key].groundTruthPath;
        let id = returnedData[key].id;
        //let imPath = returnedData[key].imagePath;

        console.log("### " + key + " / id = " + id + " ###");
        this.pages.push(id);
      });

      this.currentPage = this.pages[this.currentPageIndex];
        
      console.log("current page : " + this.currentPage);
    });

    this.checkPageNumber();
    
    this.getPageData();
  }

  /**
   * On récupère la liste des exemples qui composent la première page.
   * validation.getPageData() renvoie l'image de la page et les exemples, il faut donc faire un tri
   */
  getPageData(){
    /*this.validationService.getPageData(this.currentPage).subscribe
    (returnedData => {
      console.log("get data : " + returnedData);

      //tri : on récupère que la liste des exemples et pas l'image de la page
      this.examples = returnedData[1];
    });*/

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
  }

  isCross(id){
    if(this.examples[id][3] === "cross") {
      return true;
    }
    return false;
  }

  goHome(){
    this.router.navigate(['']);
  }

  disableEx(id){
    console.log("disable " + id);
    if(this.hidden[id] == false){
      this.validationService.disableEx(id);
    }
    else{
      this.validationService.enableEx(id);
    }
    this.changeIcon(id);
    this.hidden[id] = !this.hidden[id];
  }

  changeIcon(id){
    if(this.examples[id][3] === "cross"){
      this.examples[id][3] = "arrow";
    }else if(this.examples[id][3] === "arrow"){
      this.examples[id][3] = "cross";
    }else{
      console.log("erreur this.examples[id][2] correspond à rien")
    }
    console.log("change to arrow");
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

  validateAll(){
    this.validationService.validateAll();
  }

  leave(){
    //affichage d'un message disant qu'on retourne au menu
    // est-ce qu'il y a vraiment besoin de ce bouton ??
    const dialogRef = this.dialog.open(MydialogComponent, {});
  }
}
