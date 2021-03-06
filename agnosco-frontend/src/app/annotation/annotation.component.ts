import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';
import { HelpAnnotationComponent } from '../help-annotation/help-annotation.component';
import { EndAnnotationComponent } from '../end-annotation/end-annotation.component';
import {MatDialog } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-annotation',
  templateUrl: './annotation.component.html',
  styleUrls: ['./annotation.component.css']
})
export class AnnotationComponent implements OnInit {

  // un listener écoute l'appui sur la touche Entrée
  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) { 
    if(event.keyCode == 13){
      this.saveEdits();
      if(this.compteur4 > this.examples.length){
        if(!this.isLastPage){
          this.sendEdits();
          this.nextPage();
        }else{
          this.endAnnotation();
        }
      }else{
       this.getNext4();
      }
    }
  }

  private params = [];

  private docId;
  private docName;
  private projectId;
  private projectName;
  private docPrepared;
  private compteur4;
  private strEdits;

  private docMmPro = [];

  private hidden = [];

  public pages;
  public examples;
  /*
  format du tableau examples :
  0 : id
  1 : path64
  2 : transcription
  3 : enabled
  4 : validated
  */

  public ex4;
  public pageImage;

  public currentPageIndex;
  public currentPage;

  public isFirstPage;
  public isLastPage;
  public isEndOfPage;

  validation: Validation;


  constructor(private router: Router, private route: ActivatedRoute, private validationService: ValidationService, public dialog: MatDialog, private http: HttpClient, private sanitizer: DomSanitizer) {
    this.examples = [];
    this.ex4 = [];
    this.pages = [];
    this.currentPageIndex = 0;
    this.currentPage = 0;
    this.isFirstPage = true;
    this.isLastPage = false;
    this.isEndOfPage = false;
    this.compteur4 = 0;
    this.strEdits = "[";
  }

  ngOnInit() {
    this.route.params.forEach(param => {
      this.params.push(param.idd);
      this.params.push(param.named);
      this.params.push(param.idp);
      this.params.push(param.namep);
      this.params.push(param.prepared);
    });

    this.docId = this.params[0];
    this.docName = this.params[1];
    this.projectId = this.params[2];
    this.projectName = this.params[3];
    this.docPrepared = this.params[4];


    console.log("ID du document : " + this.docId);
    console.log("nom du document : " + this.docName);
    console.log("ID du projet : " + this.projectId);
    console.log("nom du projet : " + this.projectName);
    console.log("doc prepared : " + this.docPrepared);

    this.getAllDocuments();

    if(this.docPrepared == "false"){
      console.log("doc not prepared !!!!!");
      this.http.post(`agnosco/base/prepareExamplesOfDocument/${this.docId}`, {}, {}).subscribe(response => {
        console.log(response);
        console.log("le document a été préparé");
        
        this.getPages();

        this.docPrepared = "true";
      });
    }

    else{
      this.getPages();
    }
  }

  /**
   * Méthode qui sauvegarde les exemples modifiés dans la string strEdits.
   */
  saveEdits(){
    let notEmpty = false;

    for (let i = 0; i < this.ex4.length; i++) {
      let e = this.ex4[i];

      // on récupère la transcription affichée qui a pu être modifiée
      let newTranscript = document.getElementById("trans" + i).innerHTML;

      //si elle a été modifiée et si l'exemple est enabled, on ajoute l'exemple dans strEdits
      if(this.ex4[i][2] !== newTranscript && this.ex4[i][3]){
        this.strEdits = this.strEdits.concat("\n{\n\'id\':" + e[0] + ",\n\'transcript\':\"" + newTranscript + "\"\n},");
        
        this.examples[i + this.compteur4 - 4][2] = newTranscript;
        notEmpty = true;
      }
    }

    //on enlève la dernière virgule s'il y a au moins un exemple dans la string pour avoir le bon format pour l'appel REST
    if(notEmpty){
      this.strEdits = this.strEdits.substr(0, this.strEdits.length - 1);
    }

    this.strEdits = this.strEdits.concat("\n]");

    console.log(this.strEdits);
  }

  getId(i){
    let id = "trans" + i;
    return id;
  }

  isPrepared(){
    console.log("doc prep : " + this.docPrepared);
    if(this.docPrepared == "false"){
      return false;
    }else{
      return true;
    }
  }

  showActions(ev){
    console.log("show actions");
    let es = ev.originalTarget.parentNode.parentNode.lastChild;
    if(es.hidden) {
      es.hidden = false;
    }else {
      es.hidden = true;
    }
  }

  goToAnnotation(d){
    console.log("annotation");
    console.log("document: "+d.id);
    console.log("projet: "+this.projectId);
    this.route.params.subscribe(val => {
      this.docMmPro = [];
      this.docId = d.id;
      this.docName = d.name;
      this.docPrepared = d.prepared;
      this.getAllDocuments();

      if(this.docPrepared == "false"){
        console.log("doc not prepared !!!!!");
        this.http.post(`agnosco/base/prepareExamplesOfDocument/${this.docId}`, {}, {}).subscribe(response => {
          console.log(response);
          console.log("le document a été préparé");
          
          this.getPages();
        });
      }
      else{
        this.getPages();
      }
    });
  }

  goToValidationD(d){
    console.log("VALIDATION");
    console.log("document: "+d.id);
    this.router.navigate(['/validation',{'idd':d.id, 'named': d.name, 'prepared': d.prepared}]);
  }

  goToValidation(){
    console.log("VALIDATION");
    console.log("document: "+ this.docId);
    this.router.navigate(['/validation',{'idd':this.docId, 'named': this.docName, 'prepared': this.docPrepared}]);
  }

  goHome(){
    this.router.navigate(['']);
  }

  goToDecoupe(){
    this.router.navigate(['/decoupe']);
  }

  /**
   * Méthode qui récupère la liste des pages sous forme d'id.
   */
  getPages(){
    this.pages = [];

    //on récupère la liste des identifiants des pages du document courant
    this.validationService.getPages(this.docId).subscribe(returnedData => {
      console.log("get pages : ");
      console.log(returnedData);
      
      //on parcourt la returnedData pour ne prendre que l'id des pages
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
   * On récupère la liste des exemples qui composent la première page, soit l'id, la transcription, l'imagette, les booléens enabled et validated ainsi que l'extension de l'imagette (tif, png...).
   */
  getPageData(){
    this.examples = [];
    this.ex4 = [];

    this.validationService.getPageData(this.currentPage).subscribe
    ((returnedData:any) => {
      console.log("get data : ");
      console.log(returnedData);

      let pageImage64 = returnedData.pageImage;
      let pagePath64 = "data:image/png;base64," + pageImage64;
      this.pageImage = this.sanitizer.bypassSecurityTrustUrl(pagePath64);

      let ex = returnedData.examples;
      
      Object.keys(ex).forEach( key => {
        let data = ex[key];
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

        // le sanitizer sert à contourner l'alerte de sécurité lancée par l'url de l'image en base 64
        let securePath64 = this.sanitizer.bypassSecurityTrustUrl(path64);

        let newExample = [id, securePath64, transcript, enabled, validated];

        this.examples.push(newExample);
        this.hidden.push(!enabled);
      });

      // remplissage d'ex4 qui contient les 4 exemples courants
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
    console.log("get next 4");

    if(this.compteur4 > this.examples.length){
      console.log("pas possible, fin du doc");
      this.endAnnotation();
    }else{
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

  isBegining(){
    if((this.compteur4-8) < 0){
      return true;
    }else{
      return false;
    }
  }
  
  /**
   * Renvoie true si on est à la fin de la page, false sinon.
   */
  isEnd(){
    console.log("ex4 : ", this.compteur4, " / length : ", this.examples.length);
    if((this.compteur4) > this.examples.length){
      return true;
    }else{
      return false;
    }
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
   * @param id index de l'exemple dans le tableau ex4
   */
  isEnabled(id){
    return this.ex4[id][3];
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

    // mise à jour du booléen enabled dans ex4 ainsi que dans examples
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
   * Méthode qui renvoie true si le numéro de page p correspond à la page courante, false sinon. Elle est utilisée pour souligner le bon numéro de page dans l'interface.
   * @param p numéro de la page demandée
   */
  isCurrentPage(p){
    return (p==this.currentPageIndex+1);
  }

  /**
   * Méthode qui envoie au back-end les exemples dont les transcriptions ont été modifiées sous ce format :
   * [
      {
        'id':3,
        'transcript':'hello'
      },
      {
        'id':4,
        'transcript':'salut'
        
      }
     ]
   */
  sendEdits() {
    console.log("Send edits");
    this.validationService.sendEdits(this.strEdits);
    
  }

  /**
   * Méthode qui ouvre un dialog pour donner des informations sur le fonctionnement de la page.
   */
  getHelp(){
    const dialogRef = this.dialog.open(HelpAnnotationComponent, {});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log('The help dialog was closed');
    });
  }

  /**
   * Méthode appelée à la fin de l'annotation d'un document pour ouvrir le dialog de fin.
   */
  endAnnotation(){
    const dialogRef = this.dialog.open(EndAnnotationComponent, {});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log('The validation dialog was closed');
      console.log(result);
      if(result == 1) {
        this.sendEdits();
        this.goHome();
        console.log("SEND EDITS ET RETOUR A L'ACCUEIL");
      }else if(result == 2){
        this.sendEdits();
        this.goToValidation();
        console.log("SEND EDITS ET VALIDATION");
      }else{
        console.log("ANNULATION");
      }
    });
  }

  /**
   * Méthode appelée pour obtenir la liste des autres documents du même projet pour permettre la navigation en leur sein.
   */
  getAllDocuments() {
  
    console.log("*** GET /base/projectsAndDocuments ***");
    /**/
    this.http.get(`agnosco/base/projectsAndDocuments`,{}).subscribe(returnedData => {
      console.log(returnedData);

      Object.keys(returnedData).forEach( key => {
        if(returnedData[key].id == this.projectId){

        
          let docs = returnedData[key].documents;
          docs.forEach(element => {
            if(element.id != this.docId){
              this.docMmPro.push(element);
            }
          });

          console.log(".............");
          

          console.log(this.docMmPro);
          console.log("............");
        }
      });
    });
  }

}
