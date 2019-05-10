import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';
import { ValidateDialogComponent } from '../validate-dialog/validate-dialog.component';
import { HelpValidationComponent } from '../help-validation/help-validation.component';
import { EndValidationComponent } from '../end-validation/end-validation.component';
import {MatDialog } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-validation',
  templateUrl: './validation.component.html',
  providers: [ ValidationService ],
  styleUrls: ['./validation.component.css']
})
export class ValidationComponent implements OnInit {

  /* un listener attend l'appui sur la touche Entrée pour obtenir les 4 exemples suivants ;
  - si on est arrivé à la fin de la page, on appelle nextPage() pour passer à la page suivante
  - si on est arrivé à la fin du document, on appelle endOfDocument()
  */
  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) { 
    if(event.keyCode == 13){
      console.log("a fait entrée");
      this.validate4();
      if(this.compteur4 > this.examples.length){
        if(!this.isLastPage){
          this.nextPage();
        }else{
          console.log("fini");
          this.endOfDocument();
        }
      }else{
        this.getNext4();
      }    
    }
  }

  private params = [];
  private docId;
  private docName;
  private docPrepared;
  private hidden = [];
  private compteur4;

  public pages = [];
  public examples = [];
  public ex4 = [];
  /*
  format du tableau examples :
  0 : id
  1 : path64
  2 : transcription
  3 : enabled
  4 : validated
  */

  public currentPageIndex;
  public currentPage;

  public isFirstPage;
  public isLastPage;
  public validStr;
  public strNotEmpty;

  pageD : any[];

  validation: Validation;


  constructor(private router: Router, private route: ActivatedRoute, private validationService: ValidationService, public dialog: MatDialog, private http: HttpClient, private sanitizer: DomSanitizer) {
    this.examples = [];
    this.ex4 = [];
    this.pages = [];
    this.currentPageIndex = 0;
    this.currentPage = 0;
    this.isFirstPage = true;
    this.isLastPage = false;
    this.compteur4 = 0;
    this.validStr = "[";
    this.strNotEmpty = false;
  }

  ngOnInit() {
    this.route.params.forEach(param => {
      this.params.push(param.idd);
      this.params.push(param.named);
      this.params.push(param.prepared);
    });    

    this.docId = this.params[0];
    this.docName = this.params[1];
    this.docPrepared = this.params[2];

    // si le document n'a pas été découpé au préalable, on appelle le pré-traitement
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
   */
  getPageData(){
    this.examples = [];
    this.ex4 = [];

    this.validationService.getPageData(this.currentPage).subscribe
    ((returnedData:any) => {
      console.log("get data : ");
      console.log(returnedData);

      let ex = returnedData.examples;
      
      //on parcourt la returnedData pour ne prendre que l'id des pages
      Object.keys(ex).forEach( key => {
        let id = ex[key].id;
        let transcript = ex[key].transcript;
        let image64 = ex[key].image64;
        let enabled = ex[key].enabled;
        let validated = ex[key].validated;
        let extension = ex[key].extension;

        let imageType = "";
        console.log("extension : " + extension);

        switch(extension) {
          case "jpg": {
            imageType = "jpeg";
            break;
          }
          case "tif": {
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

      // remplissage de ex4 avec les 4 premiers exemples
      for(let i = 0; i< 4; i+=1){
        this.ex4.push(this.examples[i]);
      }

      this.compteur4 = 4;
    },
      error => {
        console.log("catch error:", error);
      }
    );    
  }


  /**
   * Méthode qui remplit ex4 avec les 4 exemples suivants.
   */
  getNext4(){
    this.ex4 = [];

    for(let i = this.compteur4; i< this.compteur4 + 4 ; i+=1){
      if(this.examples[i] != undefined){
        this.ex4.push(this.examples[i]);
      }else{
        console.log("element pas pushed car undefined");
      }
    }

    this.compteur4 += 4;

    console.log("new ex4 :");
    console.log(this.ex4);
  }

  /**
   * Méthode qui remplit ex4 avec les 4 exemples précédents.
   */
  getPrevious4(){
    console.log("get previous 4");

    if((this.compteur4-8) < 0){
      console.log("pas possible, déjà au début");
    }else{
      this.ex4 = [];

      for(let i = this.compteur4 - 8; i< this.compteur4 - 4 ; i+=1){
        if(this.examples[i] != undefined){
          this.ex4.push(this.examples[i]);
        }else{
          console.log("element pas pushed car undefined");
        }
      }

      this.compteur4 -= 4;

      console.log("new ex4 :");
      console.log(this.ex4);
    }
  }


  /**
   * Méthode qui vérifie si on est à la 1e ou à la dernière page.
   */
  checkPageNumber(){
    console.log("page index : " + this.currentPageIndex);
    if(this.currentPageIndex == 0){
      //la 1e flèche est grisée
      this.isFirstPage = true;
    }else{
      this.isFirstPage = false;
    }

    if(this.currentPageIndex == this.pages.length - 1){
      //la 2e flèche est grisée
      this.isLastPage = true;
    }else{
      this.isLastPage = false;
    }

    this.currentPage = this.pages[this.currentPageIndex];
    console.log("check page : " + this.currentPage);
  }


  /**
   * Retourne true si l'exemple n'est pas caché et vice-versa.
   * @param id index de l'exemple dans le tableau ex4
   */
  isEnabled(id){
    return this.ex4[id][3];
  }


  /**
   * Retourne true si l'exemple est validé.
   * @param id index de l'exemple dans le tableau ex4
   */
  isValidated(id){
    console.log("val : " + this.ex4[id][4]);
    return this.ex4[id][4];
  }


  isBegining(){
    if((this.compteur4-8) < 0){
      return true;
    }else{
      return false;
    }
  }

  
  isEnd(){
    console.log("ex4 : ", this.compteur4, " / length : ", this.examples.length);
    if((this.compteur4) > this.examples.length){
      return true;
    }else{
      return false;
    }
  }


  /**
   * Méthode qui cache l'exemple qui manque de pertinence.
   * @param id index de l'exemple dans le tableau ex4
   */
  disableEx(id){
    console.log("disable " + id);
    if(this.hidden[id + this.compteur4 - 4] == false){
      //on récupère l'id de l'exemple à cacher
      let i = this.ex4[id][0];
      this.ex4[id][3] = false;
      this.validationService.disableEx(i);
    }
    else{
      let i = this.ex4[id][0];
      this.ex4[id][3] = true;
      this.validationService.enableEx(i);
    }

    //mise à jour du booléen enabled dans ex4 et dans examples
    this.ex4[id][3] = !this.ex4[id][3];

    this.examples[id + this.compteur4 - 4][3] = !this.examples[id + this.compteur4 - 4][3];
    this.hidden[id + this.compteur4 - 4] = !this.hidden[id + this.compteur4 - 4];
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
   * Méthode qui fabrique une string (str) contenant les id des exemples validés sous ce format : [3,6,9]
   * Puis qui envoie cette string au back-end via une requête REST.
   */
  validate4(){
    console.log("Validation");

    this.strNotEmpty = false;

    for (let i = 0; i < this.ex4.length; i++) {
      let e = this.ex4[i];

      //si l'exemple est enabled
      if(e[3] == true){ 
        this.validStr = this.validStr.concat(e[0] + ",");
        this.strNotEmpty = true;
      }
    }
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

        //on enlève la dernière virgule s'il y a au moins un exemple dans la string
        if(this.strNotEmpty == true){
          this.validStr = this.validStr.substr(0, this.validStr.length - 1);
        }

        this.validStr = this.validStr.concat("]");
        this.validationService.validateAll(this.validStr);
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
    console.log("dialog defin");
    const dialogRef = this.dialog.open(EndValidationComponent, {});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log('The validation dialog was closed');
      console.log(result);

      if(result) {

        //on enlève la dernière virgule s'il y a au moins un exemple dans la string
        if(this.strNotEmpty == true){
          this.validStr = this.validStr.substr(0, this.validStr.length - 1);
        }

        this.validStr = this.validStr.concat("]");
        this.validationService.validateAll(this.validStr);
        this.router.navigate(['']);
        console.log("VALIDATION ET RETOUR A L'ACCUEIL");
      }else{
        console.log("ANNULATION");
      }
    });
  }
}
