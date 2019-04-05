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
  private pagesObtained;

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
    this.pagesObtained = false;
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


  getPages(){
    //test

    this.pages = [1,5,7];

    this.getPageData(); 
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
        let transcript = returnedData[key].transcript; //enlever le Some()
        let validated = returnedData[key].validated;

        let newExample = [imagePath, transcript, id, enabled];

        this.examples.push(newExample);
      });
    });

    //test

    /*this.examples[0] = ["../../assets/images/Elephant.jpg", "To be or not to be", 0, true];
    this.examples[1] = ["../../assets/images/Fraise.png", "That is the question", 1, true];
    this.examples[2] = ["../../assets/images/Elephant.jpg", "Whether 'tis nobler in the mind", 2, true];
    this.examples[3] = ["../../assets/images/Fraise.png", "To suffer the slings and arrows of outrageous fortune", 3, true];
    this.examples[4] = ["../../assets/images/Elephant.jpg", "Or to take arms against a sea of troubles", 4, true];
    this.examples[5] = ["../../assets/images/Fraise.png", "And by opposing end them.", 5, true];*/
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

  validateAll(){
    this.validationService.validateAll();
  }

  leave(){
    //affichage d'un message disant qu'on retourne au menu
    // est-ce qu'il y a vraiment besoin de ce bouton ??
    const dialogRef = this.dialog.open(MydialogComponent, {});
  }
}
