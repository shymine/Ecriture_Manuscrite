import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';
import { ValidateDialogComponent } from '../validate-dialog/validate-dialog.component';
import { HelpValidationComponent } from '../help-validation/help-validation.component';
import { EndValidationComponent } from '../end-validation/end-validation.component';
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
      if(!this.isLastPage){
        this.nextPage();
      }else{
        this.endOfDocument();
      }
    }
  }

  private params = [];
  private docName;
  private hidden = [];

  public pages = [];
  public examples = []; //0 : path - 1 : transcription -  2 : id - 3 : enabled
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

    this.getPages();

    this.checkPageNumber();
    
  }


  goHome(){
    this.router.navigate(['']);
  }


  /**
   * Méthode qui récupère la liste des pages sous forme d'id.
   */
  getPages(){
    //test
    /*this.pages = [1,5,7];
    this.getPageData(); 
*/
    //on récupère la liste des identifiants des pages du doc passé en paramètre 
    this.validationService.getPages(this.docName).subscribe(returnedData => {
      console.log("get pages : ");
      console.log(returnedData);
      
      //on parcourt la returnedData pour ne prendre que l'id de la page
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

    //test

    /*this.examples[0] = ["../../assets/images/Elephant.jpg", "To be or not to be", 0, true];
    this.examples[1] = ["../../assets/images/Fraise.png", "That is the question", 1, true];
    this.examples[2] = ["../../assets/images/Elephant.jpg", "Whether 'tis nobler in the mind", 2, true];
    this.examples[3] = ["../../assets/images/Fraise.png", "To suffer the slings and arrows of outrageous fortune", 3, true];
    this.examples[4] = ["../../assets/images/Elephant.jpg", "Or to take arms against a sea of troubles", 4, true];
    this.examples[5] = ["../../assets/images/Fraise.png", "And by opposing end them.", 5, true];*/
  }


  /**
   * Méthode qui vérifie si on est à la 1e ou à la dernière page
   */
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


  /**
   * Retourne true si l'exemple n'est pas caché et vice-versa.
   * @param id index de l'exemple dans le tableau examples
   */
  isEnabled(id){
    return this.examples[id][3];
  }


  /**
   * Méthode qui cache l'exemple qui manque de pertinence.
   * @param id index de l'exemple dans le tableau examples
   */
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


  /**
   * Méthode qui fait passer à la page précédente.
   */
  previousPage(){
    console.log("PREVIOUS PAGE");
    this.currentPageIndex--;
    this.currentPage = this.pages[this.currentPageIndex];
    this.checkPageNumber();
    this.getPageData();

    console.log("index page : " + this.currentPageIndex);
    console.log("page number : " + this.currentPage);
  }


  /**
   * Méthode qui fait passer à la page suivante.
   */
  nextPage(){
    console.log("NEXT PAGE");
    this.currentPageIndex++;
    this.currentPage = this.pages[this.currentPageIndex];
    this.checkPageNumber();
    this.getPageData();

    console.log("index page : " + this.currentPageIndex);
    console.log("page number : " + this.currentPage);
  }


  /**
   * Méthode qui fabrique une string (str) contenant les exemples validés sous ce format :
   * [
      {
        'id':3,
        'imagePath':'assets/images/coucou.png',
        'transcript':'coucou'
      },
      {
        'id':4,
        'imagePath':'assets/images/salut.png',
        'transcript':'salut'
        
      }
    ]
   * Puis qui envoie cette string au back-end.
   */
  validateAll(){
    console.log("Validation");

    let str = "[\n";

    let notEmpty = false;

    for (let i = 0; i < this.examples.length; i++) {
      let e = this.examples[i];
      if(e[3] === true){ //si l'exemple est validé
        str = str.concat("{\n\'id\':" + e[2] + ",\n\'imagePath\':\"" + e[0] + "\",\n\'transcript\':\"" + e[1] + "\"\n},\n");
      }
      notEmpty = true;
    }

    //on enlève la dernière virgule s'il y a au moins un exemple dans la string
    if(notEmpty){
      str = str.substr(0, str.length -1);
    }

    str = str.concat("]");

    console.log(str);
    this.validationService.validateAll(str);
  }


  /**
   * Méthode qui renvoie true si le numéro de page p correspond à la page courante, false sinon. Elle est utilisée pour souligner le bon numéro de page dans l'interface.
   * @param p numéro de la page demandée
   */
  isCurrentPage(p){
    return (p==this.currentPageIndex+1);
  }


  /**
   * Méthode qui ouvre un dialog pour donner des informations sur le fonctionnement de la page.
   */
  getHelp(){
    const dialogRef = this.dialog.open(HelpValidationComponent, {});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log('The help dialog was closed');
    });
  }


  /**
   * Méthode appelée quand l'utilisateur quitte le dialog. Si le dialog renvoie un résultat, on valide tous les exemples de la page et on retourne à l'accueil. Sinon, on quitte juste le dialog et on reste sur la page de validation.
   */
  leave(){
    const dialogRef = this.dialog.open(EndValidationComponent, {});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log('The validation dialog was closed');
      console.log(result);
      if(result) {
        this.validateAll();
        this.router.navigate(['']);
        console.log("VALIDATION ET RETOUR A L'ACCUEIL");
      }else{
        console.log("ANNULATION");
      }
    });
  }


  /**
   * Méthode appelée quand c'est la fin du document : un dialog s'ouvre pour indiquer que c'est la fin du document et pour demander si on valide tout et si on retourne à l'accueil.
   */
  endOfDocument(){
    const dialogRef = this.dialog.open(EndValidationComponent, {});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log('The validation dialog was closed');
      console.log(result);
      if(result) {
        this.validateAll();
        this.router.navigate(['']);
        console.log("VALIDATION ET RETOUR A L'ACCUEIL");
      }else{
        console.log("ANNULATION");
      }
    });
  }
}
