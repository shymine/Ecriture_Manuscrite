import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

export interface Validation{
  image: string; //normalement, c'est une image et pas une string
  examples: string[]; //liste des imagettes et des transcriptions
}

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  private JsonFileValidation;
  private fd = new FormData();


  constructor(private http: HttpClient) { 
    this.JsonFileValidation = "./validateExample.js";
    this.fd.append('data', this.JsonFileValidation);
  }

  /**
   * Cette méthode permet de récupérer les données de la page numéro id, soit la liste des exemples (imagettes et transcriptions) ainsi que l'image associée à la page (utilisée que pour la V1).
   * @param id numéro de la page dont on veut les données
   */
  getValidation(id): Observable<Object>{
    console.log("*** GET `agnosco/base/pageData/${id}` ***");

    return this.http.get(`agnosco/base/pageData/${id}`);
      /*.pipe(
        retry(3), // retry a failed request up to 3 times
        catchError(this.handleError) // then handle the error
      );*/
  }

  disableEx(id){
    console.log("*** PUT `agnosco/base/disableExample/${id}` ***");

    this.http.put(`agnosco/base/disableExample/${id}`, {}, {});

    console.log("service disable " + id);
  }

  enableEx(id){
    console.log("*** PUT `agnosco/base/enableExample/${id}` ***");

    this.http.put(`agnosco/base/enableExample/${id}`, {}, {});

    console.log("service enable " + id);
  }

  validateAll(){
    console.log("*** POST `agnosco/base/validateExamples` ***");

    this.http.post(`agnosco/base/validateExamples`, this.fd, {}).subscribe
    (response => console.log(response));
    
    console.log("validate all examples");
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // return an observable with a user-facing error message
    return throwError(
      'Something bad happened; please try again later.');
  };
}
