import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';
import { HelpAnnotationComponent } from '../help-annotation/help-annotation.component';
import { EndAnnotationComponent } from '../end-annotation/end-annotation.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { forEach } from '@angular/router/src/utils/collection';
import { DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';

@Component({
  selector: 'app-annotation',
  templateUrl: './annotation.component.html',
  styleUrls: ['./annotation.component.css']
})
export class AnnotationComponent implements OnInit {

  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) { 
    if(event.keyCode == 13){
      this.sendEdits();
      if(!this.isLastPage){
        this.nextPage();
      }else{
        this.endAnnotation();
      }
    }
  }

  private params = [];

  private docId;
  private docName;
  private projectId;
  private projectName;
  private docPrepared;

  private docMmPro = [];

  private hidden = [];

  public pages;
  public examples;
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
  validation: Validation;

  public dangerousUrl;
  public trustedUrl;

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

    if(this.docPrepared == "false"){
      console.log("doc not prepared !!!!!");
      this.http.post(`agnosco/base/prepareExamplesOfDocument/${this.docId}`, {}, {}).subscribe(response => {
        console.log(response);
        console.log("le document a été préparé");
        
        this.getPages();

        this.checkPageNumber();
      });
    }

    else{
      this.getPages();

      this.checkPageNumber();
    }
  }

  showActions(ev){
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
    this.router.navigate(['/annotation',{'idd':d.id, 'named':d.name, 'idp':this.projectId, 'namep': this.projectName}]);
  }

  goToValidationD(d){
    console.log("VALIDATION");
    console.log("document: "+d.id);
    this.router.navigate(['/validation',{'idd':d.id, 'named': d.name}]);
  }

  goHome(){
    this.router.navigate(['']);
  }

  goToDecoupe(){
    this.router.navigate(['/decoupe']);
  }

  goToValidation(){
    this.router.navigate(['/validation',{'id':this.docId}]);
  }

  /**
   * Méthode qui récupère la liste des pages sous forme d'id.
   */
  getPages(){
    //on récupère la liste des identifiants des pages du doc passé en paramètre 
    this.validationService.getPages(this.docId).subscribe(returnedData => {
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

        console.log("type de l'image = " + imageType)

        let path64 = "data:image/" + imageType + ";base64," + image64;
        console.log("path base 64 : ");
        console.log(path64);

        let securePath64 = this.sanitizer.bypassSecurityTrustUrl(path64);

        let newExample = [id, securePath64, transcript, enabled, validated];

        this.examples.push(newExample);
        this.hidden.push(!enabled);
      });
    });

    //Test    

    let p64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
    let securePath64 = this.sanitizer.bypassSecurityTrustUrl(p64);
    this.examples.push([1, securePath64, "yo man", true, false]);

    console.log("ex pushed");

    this.hidden.push(false);
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
        'transcript':'coucou'
      },
      {
        'id':4,
        'transcript':'salut'
        
      }
     ]
   */
  sendEdits() {
    console.log("Send edits");

    let str = "[\n";

    let notEmpty = false;

    for (let i = 0; i < this.examples.length; i++) {
      let e = this.examples[i];

      // on récupère la transcription affichée qui a pu être modifiée
      let newTranscript = document.getElementById(i.toString()).innerHTML;

      //si elle a été modifiée et si l'exemple est enabled, on ajoute l'exemple dans str
      if(this.examples[i][2] !== newTranscript && this.examples[i][3]){
        str = str.concat("{\n\'id\':" + e[0] + ",\n\'transcript\':\"" + e[2] + "\"\n},\n");
      }

      notEmpty = true;
    }

    //on enlève la dernière virgule s'il y a au moins un exemple dans la string
    if(notEmpty){
      str = str.substr(0, str.length - 1);
    }

    str = str.concat("]");

    console.log(str);
    this.validationService.sendEdits(str);
    
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
   * Fonction pour traduire une image en base 64
   * @param event 
   */
  getBase64(event) {
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
      //me.modelvalue = reader.result;
      console.log(reader.result);
    };
    reader.onerror = function (error) {
      console.log('Error: ', error);
    };
  }


  endAnnotation(){
    const dialogRef = this.dialog.open(EndAnnotationComponent, {});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log('The validation dialog was closed');
      console.log(result);
      if(result == 1) {
        this.sendEdits();
        this.router.navigate(['']);
        console.log("SEND EDITS ET RETOUR A L'ACCUEIL");
      }else if(result == 2){
        this.sendEdits();
        this.router.navigate(['/validation',{'id':this.docId}]);
        console.log("SEND EDITS ET VALIDATION");
      }else{
        console.log("ANNULATION");
      }
    });
  }

}
