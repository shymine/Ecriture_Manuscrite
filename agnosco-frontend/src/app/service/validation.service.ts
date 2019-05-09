import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';

/**
 * Cette interface est utilisée pour effectuer les appels REST vers le back-end pour alléger le code des composants principaux.
 */
export interface Validation{
  image: string;
  examples: string[]; //liste des imagettes et des transcriptions
}

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  constructor(private http: HttpClient) { 
  }


  /**
   * Cette méthode permet de récupérer les id des pages du document dont le nom est passé en paramètre.
   * @param docId 
   */
  getPages(docId): Observable<Object>{
    console.log("*** GET /base/documentPages ***");    
    
    return this.http.get(`agnosco/base/documentPages/${docId}`,{});
  }


  /**
   * Cette méthode permet de récupérer les données de la page numéro id, soit la liste des exemples (imagettes et transcriptions) ainsi que l'image associée à la page.
   * @param id numéro de la page dont on veut les données
   */
  getPageData(id): Observable<Object>{
    console.log("*** GET `agnosco/base/pageData/${id}` ***");

    return this.http.get(`agnosco/base/pageData/${id}`);
  }


  /**
   * Cette méthode permet de cacher un exemple qui manque de pertinence pour qu'il ne soit pas pris en compte pendant l'apprentissage.
   * @param id 
   */
  disableEx(id){
    console.log("*** PUT `agnosco/base/disableExample/${id}` ***");

    this.http.put(`agnosco/base/disableExample/${id}`, {}, {}).subscribe();

    console.log("service disable " + id);
  }


  /**
   * Cette méthode permet de réhabiliter un example qui a été caché précédemment.
   * @param id 
   */
  enableEx(id){
    console.log("*** PUT `agnosco/base/enableExample/${id}` ***");

    this.http.put(`agnosco/base/enableExample/${id}`, {}, {}).subscribe();

    console.log("service enable " + id);
  }


  /**
   * Cette méthode permet d'envoyer au back-end la liste des id des exemples validés par l'utilisateur.
   * @param valid 
   */
  validateAll(valid){
    console.log("*** POST `agnosco/base/validateExamples` ***");

    this.http.post(`agnosco/base/validateExamples`, {valid}, {}).subscribe
    (response => console.log(response));
    
    console.log("examples validated");
  }


  /**
   * Cette méthode permet d'envoyer au back-end les éléments dont la transcription a été modifiée par l'utilisateur.
   * @param str 
   */
  sendEdits(str){
    console.log("*** POST `agnosco/base/saveExamplesEdits` ***");

    this.http.post(`agnosco/base/saveExampleEdits`, {str}, {}).subscribe
    (response => console.log(response));
    
    console.log("edits sent");
  }
}
