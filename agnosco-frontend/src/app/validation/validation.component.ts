import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';
import { ValidateDialogComponent } from '../validate-dialog/validate-dialog.component';
import { HelpValidationComponent } from '../help-validation/help-validation.component';
import { EndValidationComponent } from '../end-validation/end-validation.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';

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
  private docId;
  private docName;
  private docPrepared;
  private hidden = [];

  public pages = [];
  public examples = [];
  //0 : path
  //1 : transcription
  //2 : id
  //3 : enabled

  //new version
  //0 : id
  //1 : path64
  //2 : transcription
  //3 : enabled
  //4 : validated
  public currentPageIndex;
  public currentPage;

  public isFirstPage;
  public isLastPage;

  pageD : any[];

  validation: Validation;


  constructor(private router: Router, private route: ActivatedRoute, private validationService: ValidationService, public dialog: MatDialog, private http: HttpClient, private sanitizer: DomSanitizer) {
    this.examples = [];
    this.pages = [];
    this.currentPageIndex = 0;
    this.currentPage = 0;
    this.isFirstPage = true;
    this.isLastPage = false;
  }

  ngOnInit() {
    this.route.params.forEach(param => {
      this.params.push(param.idd);
      this.params.push(param.named);
      this.params.push(param.prepared);
    });    

    this.docId = this.params[0]; // le nom du document est le 1er paramètre
    console.log("ID du document : " + this.docId);
    this.docName = this.params[1];
    this.docPrepared = this.params[2];

    if(this.docPrepared == "false"){
      console.log("doc not prepared !!!!!");
      this.http.post(`agnosco/base/prepareExamplesOfDocument/${this.docId}`, {}, {}).subscribe(response => {
        console.log(response);
        console.log("le document a été préparé");

        this.docPrepared = "true";
        
        this.getPages();
      });
    }

    else{
      this.getPages();
    }
    
  }


  isPrepared(){
    console.log("doc prep : " + this.docPrepared);
    if(this.docPrepared == "false"){
      return false;
    }else{
      return true;
    }
  }


  goHome(){
    this.router.navigate(['']);
  }


  /**
   * Méthode qui récupère la liste des pages sous forme d'id.
   */
  getPages(){
    //on récupère la liste des identifiants des pages du doc passé en paramètre 
    this.validationService.getPages(this.docId).subscribe(returnedData => {
      console.log("get pages : ");
      console.log(returnedData);
      
      //on parcourt la returnedData pour ne prendre que l'id de la page
      Object.keys(returnedData).forEach( key => {
        let id = returnedData[key].id;

        console.log("### " + key + " / id = " + id + " ###");
        this.pages.push(id);
      });

      this.checkPageNumber();
	
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
        let id = returnedData[key].id;
        let transcript = returnedData[key].transcript;
        let image64 = returnedData[key].image64;
        let enabled = returnedData[key].enabled;
        let validated = returnedData[key].validated;
        let extension = returnedData[key].extension;

        let imageType = "";
        console.log("extension : " + extension);

        switch(extension) {
          case "jpg": {
            imageType = "jpeg";
            break;
          }
          case "tif": {
            console.log("MOOUUUUUUUAAAAAAAAAAAAAA");
            imageType = "tiff";
            break;
          }
          default: {
            imageType = extension;
            break;
          }
        }

        let path64 = "data:image/" + imageType + ";base64," + image64;

        let securePath64 = this.sanitizer.bypassSecurityTrustUrl(path64);

        let newExample = [id, securePath64, transcript, enabled, validated];

        this.examples.push(newExample);
        this.hidden.push(!enabled);
      });
    },
      error => {
        console.log("catch error:", error);
      }
    );
  }


  /**
   * Méthode qui vérifie si on est à la 1e ou à la dernière page
   */
  checkPageNumber(){
    console.log("page index : " + this.currentPageIndex);
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
   * Retourne true si l'exemple est validé.
   * @param id index de l'exemple dans le tableau examples
   */
  isValidated(id){
    return this.examples[id][4];
  }


  /**
   * Méthode qui cache l'exemple qui manque de pertinence.
   * @param id index de l'exemple dans le tableau examples
   */
  disableEx(id){
    console.log("disable " + id);
    if(this.hidden[id] == false){
      //on récupère l'id de l'exemple à cacher
      let i = this.examples[id][0];
      this.validationService.disableEx(i);
    }
    else{
      let i = this.examples[id][0];
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
   * Méthode qui fabrique une string (str) contenant les id des exemples validés sous ce format :
   * [3,6,9]
   * Puis qui envoie cette string au back-end.
   */
  validateAll(){
    console.log("Validation");

    let str = "[";

    let notEmpty = false;

    for (let i = 0; i < this.examples.length; i++) {
      let e = this.examples[i];
      console.log("exemple enabled : " + e[3]);
      if(e[3] == true){ //si l'exemple est enabled
        str = str.concat(e[0] + ",");
      }
      notEmpty = true;
    }

    //on enlève la dernière virgule s'il y a au moins un exemple dans la string
    if(notEmpty){
      str = str.substr(0, str.length - 1);
    }

    str = str.concat("]");

    console.log("validation string : " + str);
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
    const dialogRef = this.dialog.open(ValidateDialogComponent, {});
    
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
