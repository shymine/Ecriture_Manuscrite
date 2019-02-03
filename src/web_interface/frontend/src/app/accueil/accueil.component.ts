import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-accueil',
  templateUrl: './accueil.component.html',
  styleUrls: ['./accueil.component.css']
})
export class AccueilComponent implements OnInit {

  public projects;

  constructor(private router: Router) {
    this.projects = [];
  }

  ngOnInit() {

    //test
    this.projects[0]=["Project 1", ["Document Un", "Document Deux", "Document Trois"]];
    this.projects[1]=["Project 2", ["Document Quatre", "Document Cinq", "Document Six"]];

    this.afficheListe();

  }

  afficheListe(){

  }

  goToDecoupe(){
    this.router.navigate(['/decoupe']);
  }

  goToAnnotation(p){
    console.log("Bouton:");
    console.log(p);
    this.router.navigate(['/annotation',{'id':JSON.stringify(p)}]);
  }

  goToValidation(p){
    this.router.navigate(['/validation',{'id':JSON.stringify(p)}]);
  }

}
