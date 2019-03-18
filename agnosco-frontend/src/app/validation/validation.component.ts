import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Validation, ValidationService } from '../service/validation.service';

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
    }
  }

  private params = [];
  private docName;
  private hidden = [false, false, false, false, false, false];

  public pages;
  public examples;


  pageD : any[];

  validation: Validation;

  constructor(private router: Router, private route: ActivatedRoute, private validationService: ValidationService, private http: HttpClient) {
    this.examples = [];
    this.pages = [];
  }

  ngOnInit() {
    this.route.params.forEach(param => {
      this.params.push(param.id);
    });

    this.docName = this.params[0]; // le nom du document est le 1er paramètre

    //test
    this.examples[0] = ["../../assets/images/Elephant.jpg", "To be or not to be", 0, "cross"];
    this.examples[1] = ["../../assets/images/Fraise.png", "That is the question", 1, "cross"];
    this.examples[2] = ["../../assets/images/Elephant.jpg", "Whether 'tis nobler in the mind", 2, "cross"];
    this.examples[3] = ["../../assets/images/Fraise.png", "To suffer the slings and arrows of outrageous fortune", 3, "cross"];
    this.examples[4] = ["../../assets/images/Elephant.jpg", "Or to take arms against a sea of troubles", 4, "cross"];
    this.examples[5] = ["../../assets/images/Fraise.png", "And by opposing end them.", 5, "cross"];

    //on récupère la liste des identifiants des pages du doc passé en paramètre 
    this.http.get('/base/documentPages/{docName}').subscribe(
      pages => this.pages = pages
    );

    //on récupère la liste des exemples qui composent la première page
    //getValidation() renvoie l'image de la page et les exemples, il faut donc faire un tri
    this.validationService.getValidation(0).subscribe(response => console.log("getValidation"));

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
      //this.http.put('/base/disableExample/${id}', {}, {}).subscribe(example => this.changeIconArrow(id));
    }else{
      this.validationService.enableEx(id);
      //this.http.put('/base/enableExample/${id}', {}, {}).subscribe(example => this.changeIconCross(id));
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

  validateAll(){
    this.validationService.validateAll();
  }
}
